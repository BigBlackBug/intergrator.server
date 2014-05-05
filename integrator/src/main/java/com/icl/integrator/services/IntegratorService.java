package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.model.IntegratorUser;
import com.icl.integrator.services.utils.RestrictedAccess;
import com.icl.integrator.services.utils.Synchronized;
import com.icl.integrator.services.validation.ValidationService;
import com.icl.integrator.util.IntegratorException;
import com.icl.integrator.util.connectors.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
@Service
public class IntegratorService implements IntegratorAPI {

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
	@RestrictedAccess
	public <T extends DestinationDescriptor> ResponseDTO<Map<String, ResponseDTO<String>>>
	deliver(IntegratorPacket<DeliveryDTO, T> delivery) {
		logger.info("Received a delivery request");
		DeliveryDTO packet = delivery.getData();
		validationService.validate(packet.getRequestData());

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
		//TODO проверка если не добавлен
		logger.info("Received a service registration request");
		List<ActionRegistrationResultDTO> result =
				registrationService.register(registrationDTO.getData());
		String serviceName = registrationDTO.getData().getServiceName();
		versioningService.logModification(new Modification(Modification.SubjectType.SERVICE,
		                                                   Modification.ActionType.ADDED,
		                                                   serviceName));
		return new ResponseDTO<>(result);
	}

	@Override
	@Synchronized
	public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
			IntegratorPacket<ServiceDestinationDescriptor, T> serviceDescriptor) {
		logger.info("Received a ping request for " + serviceDescriptor);
		try {
			workerService.pingService(serviceDescriptor.getData());
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
		List<ServiceDTO> serviceList = workerService.getServiceList();
		return new ResponseDTO<>(serviceList);
	}

	@Override
	@RestrictedAccess
	public <T extends DestinationDescriptor, Y extends ActionDescriptor>
	ResponseDTO<List<ActionEndpointDTO<Y>>> getSupportedActions(
			IntegratorPacket<String, T> serviceName) {
		List<ActionEndpointDTO> supportedActions = workerService
				.getSupportedActions(serviceName.getData());
		List<ActionEndpointDTO<Y>> result = new ArrayList<>();
		for (ActionEndpointDTO actionEndpoint : supportedActions) {
			result.add(actionEndpoint);
		}
		//TODO really?
		return new ResponseDTO<>(result);
	}

	@Override
	@RestrictedAccess
	public <T extends DestinationDescriptor, Y extends ActionDescriptor> ResponseDTO<Void>
	addAction(IntegratorPacket<AddActionDTO<Y>, T> actionDTO) {
		//TODO разрулить это дело. если типа изменений по сервису нет,то пусть коммитит.
		//TODO везде натыкать таких проверок
		workerService.addAction(actionDTO.getData());
		String actionName = actionDTO.getData().getActionRegistration().getAction().getActionName();
		versioningService.logModification(
				new Modification(Modification.SubjectType.ACTION,
				                 Modification.ActionType.ADDED,
				                 actionName)
		);
		return new ResponseDTO<>(true);
	}

	@Override
	@RestrictedAccess
	public <ADType extends ActionDescriptor,
			DDType extends DestinationDescriptor> ResponseDTO<FullServiceDTO<ADType>>
	getServiceInfo(IntegratorPacket<String, DDType> serviceName) {
		FullServiceDTO<ADType> serviceInfo =
				workerService.getServiceInfo(serviceName.getData());
		return new ResponseDTO<>(serviceInfo);
	}

	@Override
	@RestrictedAccess
	public <T extends DestinationDescriptor, Y> ResponseDTO<List<ResponseDTO<Void>>>
	registerAutoDetection(
			IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO) {
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
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		IntegratorUser user = (IntegratorUser) authentication.getPrincipal();
		List<Modification> modifications = versioningService.fetchModifications(user.getUsername());
		return new ResponseDTO<>(modifications);
	}

	@Override
	@RestrictedAccess
	public <T extends DestinationDescriptor> ResponseDTO<Void> removeService(
			IntegratorPacket<String, T> serviceName) {
		workerService.removeService(serviceName.getData());
		return new ResponseDTO<>(true);
	}

}
