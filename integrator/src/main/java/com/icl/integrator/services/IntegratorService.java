package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.editor.EditActionDTO;
import com.icl.integrator.dto.editor.EditServiceDTO;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.util.ErrorCode;
import com.icl.integrator.model.IntegratorUser;
import com.icl.integrator.services.utils.Synchronized;
import com.icl.integrator.services.validation.ValidationService;
import com.icl.integrator.util.ExceptionUtils;
import com.icl.integrator.util.IntegratorException;
import com.icl.integrator.util.connectors.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
//TODO доабвить настройки доставки к действию
@Service
public class IntegratorService implements IntegratorAPI {

	//TODO move to props
	private static final String errorMessage =
			"Состояние Вашего клиента не совпадает с состоянием сервера." +
					"Пожалуйста вызовите метод fetchUpdates, чтобы получить изменения, " +
					"совершённые с момента последнего обновления. " +
					"Для получения ожидаемого результата желательно обработать полученные данные";

	private static Log logger = LogFactory.getLog(IntegratorService.class);

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private PacketProcessor packetProcessor;

	@Autowired
	private IntegratorWorkerService workerService;

	@Autowired
	private DeliveryCreator deliveryCreator;

	@Autowired
	private ValidationService validationService;

	@Autowired
	private VersioningService versioningService;

	@Override
	@Synchronized
	public <T extends DestinationDescriptor> ResponseDTO<Map<String, ResponseDTO<String>>>
	deliver(IntegratorPacket<DeliveryDTO, T> delivery) {
		logger.info("Received a delivery request");
		DeliveryDTO packet = delivery.getData();
		validationService.validate(packet.getRequestData());

		List<String> destinations = packet.getDestinations();
		String action = packet.getAction();
		boolean canContinue = true;
		for (String service : destinations) {
			//service not deleted/changed
			canContinue &=
					versioningService.hasAccess(getCurrentUsername(),
					                            new Modification.ServiceSubject(service),
					                            Modification.ActionType.REMOVED,
					                            Modification.ActionType.CHANGED);
			//action not deleted changed
			canContinue &= versioningService.hasAccess(getCurrentUsername(),
			                                           new Modification.ActionSubject(service,
			                                                                          action),
			                                           Modification.ActionType.CHANGED,
			                                           Modification.ActionType.REMOVED
			);
		}
		if (!canContinue) {
			return new ResponseDTO<>(new ErrorDTO(errorMessage, ErrorCode.OUTDATED_CLIENT_STATE));
		}

		Deliveries deliveries =
				deliveryCreator.createDeliveries(packet, packet.getResponseHandlerDescriptor());
		Map<String, ResponseDTO<String>> serviceToRequestID =
				packetProcessor.process(deliveries.getDeliveries());
		serviceToRequestID.putAll(deliveries.getErrorMap());
		return new ResponseDTO<>(serviceToRequestID);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor>
	ResponseDTO<Boolean>
	ping(IntegratorPacket<Void, T> responseHandler) {
		return new ResponseDTO<>(Boolean.TRUE);
	}

	@Override
	@Synchronized
	public <T extends ActionDescriptor, Y extends DestinationDescriptor>
	ResponseDTO<List<ActionRegistrationResultDTO>> registerService(
			IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO) {
		logger.info("Received a service registration request");
		String serviceName = registrationDTO.getData().getServiceName();
		String username = getCurrentUsername();
		Modification.ServiceSubject subject = new Modification.ServiceSubject(serviceName);
		Modification modification = new Modification(Modification.ActionType.ADDED, subject);

		if (!versioningService.hasAccess(username, modification)) {
			return new ResponseDTO<>(new ErrorDTO(errorMessage, ErrorCode.OUTDATED_CLIENT_STATE));
		}

		List<ActionRegistrationResultDTO> result =
				workerService.registerService(registrationDTO.getData());

		versioningService.logModification(username, modification);
		return new ResponseDTO<>(result);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
			IntegratorPacket<ServiceDestinationDescriptor, T> serviceDescriptor) {
		logger.info("Received a ping request for " + serviceDescriptor);
		String username = getCurrentUsername();
		ServiceDestinationDescriptor destinationDescriptor = serviceDescriptor.getData();
		String service = destinationDescriptor.getService();
		String action = destinationDescriptor.getAction();
		if (!versioningService.hasAccess(username, new Modification.ServiceSubject(service),
		                                 Modification.ActionType.CHANGED,
		                                 Modification.ActionType.REMOVED) ||
				!versioningService
						.hasAccess(username, new Modification.ActionSubject(service, action),
						           Modification.ActionType.CHANGED,
						           Modification.ActionType.REMOVED)) {
			return new ResponseDTO<>(new ErrorDTO(errorMessage, ErrorCode.OUTDATED_CLIENT_STATE));
		}
		try {
			workerService.pingService(destinationDescriptor);
			return new ResponseDTO<>(Boolean.TRUE);
		} catch (ConnectionException ex) {
			logger.error(ex, ex);
			return new ResponseDTO<>(new ErrorDTO(ex, ErrorCode.SERVICE_NOT_AVAILABLE));
		}
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor> ResponseDTO<List<ServiceDTO>>
	getServiceList(IntegratorPacket<Void, T> packet) {
		//TODO сброс версий для сервисов???
		List<ServiceDTO> serviceList = workerService.getServiceList();
		return new ResponseDTO<>(serviceList);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor, Y extends ActionDescriptor>
	ResponseDTO<List<ActionEndpointDTO<Y>>> getSupportedActions(
			IntegratorPacket<String, T> serviceNamePacket) {
		String serviceName = serviceNamePacket.getData();
		if (!versioningService.hasAccess(
				getCurrentUsername(), new Modification.ServiceSubject(serviceName),
				Modification.ActionType.CHANGED, Modification.ActionType.REMOVED)) {
			return new ResponseDTO<>(new ErrorDTO(errorMessage, ErrorCode.OUTDATED_CLIENT_STATE));
		}
		List<ActionEndpointDTO<Y>> supportedActions = workerService
				.getSupportedActions(serviceName);
		return new ResponseDTO<>(supportedActions);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor, Y extends ActionDescriptor> ResponseDTO<Void>
	addAction(IntegratorPacket<AddActionDTO<Y>, T> actionDTO) {
		AddActionDTO<Y> data = actionDTO.getData();
		String serviceName = data.getServiceName();
		String actionName = data.getActionRegistration().getAction().getActionName();
		String username = getCurrentUsername();

		Modification.ActionSubject subject =
				new Modification.ActionSubject(serviceName, actionName);
		Modification modification = new Modification(Modification.ActionType.ADDED, subject);
		//или сервис уже удалили
		//или уже кто-то добавил действие с таким именем
		if (!versioningService.hasAccess(username,
		                                 new Modification.ServiceSubject(serviceName),
		                                 Modification.ActionType.REMOVED,
		                                 Modification.ActionType.CHANGED)
				|| !versioningService.hasAccess(username, modification)) {
			return new ResponseDTO<>(new ErrorDTO(errorMessage, ErrorCode.OUTDATED_CLIENT_STATE));
		}
		workerService.addAction(data);

		versioningService.logModification(username, modification);
		return new ResponseDTO<>(true);
	}

	@Override
	@Synchronized
	public <ADType extends ActionDescriptor,
			DDType extends DestinationDescriptor> ResponseDTO<FullServiceDTO<ADType>>
	getServiceInfo(IntegratorPacket<String, DDType> serviceNamePacket) {
		String serviceName = serviceNamePacket.getData();
		if (!versioningService
				.hasAccess(getCurrentUsername(), new Modification.ServiceSubject(serviceName),
				           Modification.ActionType.REMOVED)) {
			return new ResponseDTO<>(new ErrorDTO(errorMessage, ErrorCode.OUTDATED_CLIENT_STATE));
		}
		FullServiceDTO<ADType> serviceInfo =
				workerService.getServiceInfo(serviceNamePacket.getData());
		return new ResponseDTO<>(serviceInfo);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor, Y> ResponseDTO<List<ResponseDTO<Void>>>
	registerAutoDetection(
			IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO) {
		//TODO версионность
		logger.info("Received an autodetection registration request");
		try {
			List<ResponseDTO<Void>> result =
					registrationService.register(autoDetectionDTO.getData());
			return new ResponseDTO<>(result);
		} catch (JsonProcessingException e) {
			throw new IntegratorException(e);
		}
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor>
	ResponseDTO<List<DeliveryActionsDTO>> getActionsForDelivery(
			IntegratorPacket<Void, T> packet) {
		logger.info("Received a getActionsForDelivery request");
		List<DeliveryActionsDTO> result = workerService.getAllActionMap();
		return new ResponseDTO<>(result);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor, Y extends ActionDescriptor>
	ResponseDTO<List<ServiceAndActions<Y>>>
	getServicesSupportingActionType(IntegratorPacket<ActionMethod, T> packet) {
		logger.info("Received a getServicesSupportingActionType request");
		List<ServiceAndActions<Y>> serviceDTOListMap =
				workerService.getServicesSupportingActionType(packet.getData());
		return new ResponseDTO<>(serviceDTOListMap);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor>
	ResponseDTO<List<Modification>>
	fetchUpdates(IntegratorPacket<Void, T> responseHandler) {
		List<Modification> modifications = versioningService.fetchModifications(getCurrentUsername());
		return new ResponseDTO<>(modifications);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor> ResponseDTO<Void> removeService(
			IntegratorPacket<String, T> serviceNamePacket) {
		String serviceName = serviceNamePacket.getData();
		Modification modification = new Modification(
				Modification.ActionType.REMOVED, new Modification.ServiceSubject(serviceName));

		String username = getCurrentUsername();
		if (!versioningService.hasAccess(username, modification)) {
			return new ResponseDTO<>(new ErrorDTO(errorMessage, ErrorCode.OUTDATED_CLIENT_STATE));
		}

		workerService.removeService(serviceName);
		versioningService.logModification(username, modification);
		return new ResponseDTO<>(true);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor> ResponseDTO<Void> editService(
			IntegratorPacket<EditServiceDTO, T> editServiceDTO) {
		EditServiceDTO data = editServiceDTO.getData();
		String serviceName = data.getServiceName();
		Modification.ServiceSubject subject = new Modification.ServiceSubject(serviceName);
		if (!versioningService.hasAccess(getCurrentUsername(), subject,
		                                 Modification.ActionType.CHANGED,
		                                 Modification.ActionType.REMOVED)) {
			return new ResponseDTO<>(new ErrorDTO(errorMessage, ErrorCode.OUTDATED_CLIENT_STATE));
		}
		try {
			workerService.editService(data);
		}catch(DataAccessException ex){
			String realSQLError = ExceptionUtils.getRealSQLError(ex);
			logger.error(realSQLError);
			return new ResponseDTO<>(new ErrorDTO(realSQLError));
		}
		versioningService.logModification(getCurrentUsername(),
		                                  new Modification(Modification.ActionType.CHANGED,
		                                                   subject)
		);
		return new ResponseDTO<>(true);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor> ResponseDTO<Void> editAction(
			IntegratorPacket<EditActionDTO, T> editActionDTO) {
		EditActionDTO data = editActionDTO.getData();
		String serviceName = data.getServiceName();
		String actionName = data.getActionName();
		Modification.ActionSubject subject =
				new Modification.ActionSubject(serviceName, actionName);
		if (!versioningService.hasAccess(getCurrentUsername(), subject,
		                                 Modification.ActionType.CHANGED,
		                                 Modification.ActionType.REMOVED)) {
			return new ResponseDTO<>(new ErrorDTO(errorMessage, ErrorCode.OUTDATED_CLIENT_STATE));
		}
		try {
			workerService.editAction(data);
		}catch(DataAccessException ex){
			String realSQLError = ExceptionUtils.getRealSQLError(ex);
			logger.error(realSQLError);
			return new ResponseDTO<>(new ErrorDTO(realSQLError));
		}
		versioningService.logModification(getCurrentUsername(),
		                                  new Modification(Modification.ActionType.CHANGED,
		                                                   subject)
		);
		return new ResponseDTO<>(true);
	}

	private String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return ((IntegratorUser) authentication.getPrincipal()).getUsername();
	}

}
