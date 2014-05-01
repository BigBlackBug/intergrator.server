package com.icl.integrator.services;

import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.services.validation.ValidationService;
import com.icl.integrator.util.connectors.ConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private DeliveryService deliveryService;

	@Autowired
	private DeliveryCreator deliveryCreator;

	@Autowired
	private ValidationService validationService;

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Map<String, ResponseDTO<String>>> deliver(
			IntegratorPacket<DeliveryDTO, T> delivery) {
		logger.info("Received a delivery request");
		ResponseDTO<Map<String, ResponseDTO<String>>> response;
		try {
			DeliveryDTO packet = delivery.getData();
			validationService.validate(packet.getRequestData());

			Deliveries deliveries =
					deliveryCreator.createDeliveries(packet, packet.getResponseHandlerDescriptor());
			Map<String, ResponseDTO<String>> serviceToRequestID =
					packetProcessor.process(deliveries.getDeliveries());
			serviceToRequestID.putAll(deliveries.getErrorMap());
			response = new ResponseDTO<>(serviceToRequestID);
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}
		deliveryService.deliver(delivery.getResponseHandlerDescriptor(), response);
		return response;
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Boolean> ping(
			IntegratorPacket<Void, T> responseHandler) {
		deliveryService.deliver(responseHandler.getResponseHandlerDescriptor(), Boolean.TRUE);
		return new ResponseDTO<>(Boolean.TRUE);
	}

	@Override
	public <T extends ActionDescriptor, Y extends DestinationDescriptor>
	ResponseDTO<List<ActionRegistrationResultDTO>> registerService(
			IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO) {
		logger.info("Received a service registration request");
		ResponseDTO<List<ActionRegistrationResultDTO>> response;
		try {
			List<ActionRegistrationResultDTO> result =
					registrationService.register(registrationDTO.getData());
			response = new ResponseDTO<>(result);
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}

		deliveryService.deliver(registrationDTO.getResponseHandlerDescriptor(), response);
		return response;
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
			IntegratorPacket<ServiceDestinationDescriptor, T> serviceDescriptor) {
		logger.info("Received a ping request for " + serviceDescriptor);
		ResponseDTO<Boolean> response;
		try {
			workerService.pingService(serviceDescriptor.getData());
			response = new ResponseDTO<>(Boolean.TRUE);
		} catch (ConnectionException ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex, ErrorCode.SERVICE_NOT_AVAILABLE));
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}
		deliveryService.deliver(serviceDescriptor.getResponseHandlerDescriptor(), response);
		return response;
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<List<ServiceDTO>> getServiceList(
			IntegratorPacket<Void, T> packet) {
		ResponseDTO<List<ServiceDTO>> response;
		try {
			List<ServiceDTO> serviceList = workerService.getServiceList();
			response = new ResponseDTO<>(serviceList);
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}

		deliveryService.deliver(packet.getResponseHandlerDescriptor(), response);
		return response;
	}

	@Override
	public <T extends DestinationDescriptor, Y extends ActionDescriptor>
	ResponseDTO<List<ActionEndpointDTO<Y>>> getSupportedActions(
			IntegratorPacket<String, T> serviceName) {
		ResponseDTO<List<ActionEndpointDTO<Y>>> response;
		try {
			List<ActionEndpointDTO> supportedActions = workerService
					.getSupportedActions(serviceName.getData());
			List<ActionEndpointDTO<Y>> result = new ArrayList<>();
			for (ActionEndpointDTO actionEndpoint : supportedActions) {
				result.add(actionEndpoint);
			}
			response = new ResponseDTO<>(result);
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}
		deliveryService.deliver(serviceName.getResponseHandlerDescriptor(), response);
		return response;
	}

	@Override
	public <T extends DestinationDescriptor, Y extends ActionDescriptor> ResponseDTO<Void> addAction(
			IntegratorPacket<AddActionDTO<Y>, T> actionDTO) {
		ResponseDTO<Void> response;
		AddActionDTO<Y> packet = actionDTO.getData();
		try{
			workerService.addAction(packet);
			response = new ResponseDTO<>(true);
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}
		deliveryService.deliver(actionDTO.getResponseHandlerDescriptor(), response);
		return response;
	}

	@Override
	public <ADType extends ActionDescriptor,
			DDType extends DestinationDescriptor> ResponseDTO<FullServiceDTO<ADType>>
	getServiceInfo(IntegratorPacket<String, DDType> serviceName) {
		ResponseDTO<FullServiceDTO<ADType>> response;
		try {
			FullServiceDTO<ADType> serviceInfo =
					workerService.getServiceInfo(serviceName.getData());
			response = new ResponseDTO<>(serviceInfo);
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}

		deliveryService.deliver(serviceName.getResponseHandlerDescriptor(), response);
		return response;
	}

	@Override
	public <T extends DestinationDescriptor, Y> ResponseDTO<List<ResponseDTO<Void>>>
	registerAutoDetection(
			IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO) {
		logger.info("Received an autodetection registration request");
		ResponseDTO<List<ResponseDTO<Void>>> response;
		try {
			List<ResponseDTO<Void>> result =
					registrationService.register(autoDetectionDTO.getData());
			response = new ResponseDTO<>(result);
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}

		deliveryService.deliver(autoDetectionDTO.getResponseHandlerDescriptor(), response);
		return response;
	}

	@Override
	public <T extends DestinationDescriptor>
	ResponseDTO<List<DeliveryActionsDTO>> getActionsForDelivery(
			IntegratorPacket<Void, T> packet) {
		logger.info("Received a getActionsForDelivery request");
		ResponseDTO<List<DeliveryActionsDTO>> response;
		try {
			List<DeliveryActionsDTO> result = workerService.getAllActionMap();
			response = new ResponseDTO<>(result);
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}

		deliveryService.deliver(packet.getResponseHandlerDescriptor(), response);
		return response;
	}

	@Override
	public <T extends DestinationDescriptor, Y extends ActionDescriptor>
	ResponseDTO<List<ServiceAndActions<Y>>>
	getServicesSupportingActionType(IntegratorPacket<ActionMethod, T> packet) {
		logger.info("Received a getServicesSupportingActionType request");
		ResponseDTO<List<ServiceAndActions<Y>>> response;
		try {
			List<ServiceAndActions<Y>> serviceDTOListMap = workerService.get(packet.getData());
			response = new ResponseDTO<>(serviceDTOListMap);
		} catch (Exception ex) {
			logger.error(ex, ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}

		deliveryService.deliver(packet.getResponseHandlerDescriptor(), response);
		return response;
	}

}
