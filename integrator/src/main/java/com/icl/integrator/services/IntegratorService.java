package com.icl.integrator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.AutoDetectionRegistrationDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.model.AbstractActionEntity;
import com.icl.integrator.model.AbstractEndpointEntity;
import com.icl.integrator.model.Delivery;
import com.icl.integrator.services.validation.ValidationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
@Service
public class IntegratorService implements IntegratorAPI {

    private static Log logger =
            LogFactory.getLog(IntegratorService.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private PacketProcessorFactory processorFactory;

    @Autowired
    private IntegratorWorkerService workerService;

    @Autowired
    private DeliveryService deliveryService;

	@Autowired
	private DeliveryCreator deliveryCreator;

	@Autowired
	private ValidationService validationService;

	@Autowired
	private ObjectMapper mapper;

	@Override
    public  <T extends DestinationDescriptor> ResponseDTO<Map<String,
            ResponseDTO<UUID>>> deliver(
            IntegratorPacket<DeliveryDTO, T> delivery) {
        logger.info("Received a delivery request");
        ResponseDTO<Map<String, ResponseDTO<UUID>>> response;
        try {
	        DeliveryDTO packet = delivery.getPacket();
	        validationService.validate(packet.getRequestData());

            Deliveries deliveries =
		            deliveryCreator.createDeliveries(packet);
            //посылаем в шыну
            PacketProcessor processor = processorFactory.createProcessor();
            Map<String, ResponseDTO<UUID>> serviceToRequestID =
                    processor.process(deliveries.getDeliveries(),
                                      packet.getResponseHandlerDescriptor());
            serviceToRequestID.putAll(deliveries.getErrorMap());
            response = new ResponseDTO<>(serviceToRequestID);
        } catch (Exception ex) {
            response = new ResponseDTO<>(ex);
        }
        sendResponse(delivery.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public <T extends DestinationDescriptor> Boolean ping(
            IntegratorPacket<Void, T> responseHandler) {
        sendResponse(responseHandler.getResponseHandlerDescriptor(),
                     Boolean.TRUE);
        return true;
    }

    @Override
    public <T extends ActionDescriptor, Y extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO) {
        logger.info("Received a service registration request");
        ResponseDTO<Map<String, ResponseDTO<Void>>> response;
        try {
            Map<String, ResponseDTO<Void>> result =
                    registrationService.register(registrationDTO.getPacket());
            response = new ResponseDTO<>(result);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }

        sendResponse(registrationDTO.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
            IntegratorPacket<ServiceDestinationDescriptor, T> serviceDescriptor) {
        logger.info("Received a ping request for " + serviceDescriptor);
        ResponseDTO<Boolean> response;
        try {
            workerService.pingService(serviceDescriptor.getPacket());
            response = new ResponseDTO<>(Boolean.TRUE, Boolean.class);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        sendResponse(serviceDescriptor.getResponseHandlerDescriptor(), response);
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
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }

        sendResponse(packet.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<String>> getSupportedActions(
            IntegratorPacket<ServiceDTO, T> serviceDTO) {
        ResponseDTO<List<String>> response;
        try {
            List<String> actions = workerService
                    .getSupportedActions(serviceDTO.getPacket());
            response = new ResponseDTO<>(actions);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        sendResponse(serviceDTO.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Void> addAction(
            IntegratorPacket<AddActionDTO, T> actionDTO) {
        ResponseDTO<Void> response;
        try {
            workerService.addAction(actionDTO.getPacket());
            response = new ResponseDTO<>(true);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        sendResponse(actionDTO.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public <EDType extends EndpointDescriptor, ADType extends ActionDescriptor,
            DDType extends DestinationDescriptor> ResponseDTO<FullServiceDTO<EDType, ADType>>
    getServiceInfo(
            IntegratorPacket<ServiceDTO, DDType> serviceDTO) {
        ResponseDTO<FullServiceDTO<EDType, ADType>> response;
        try {
            FullServiceDTO<EDType, ADType> serviceInfo =
                    workerService
                            .getServiceInfo(serviceDTO.getPacket());
            response = new ResponseDTO<>(serviceInfo);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }

        sendResponse(serviceDTO.getResponseHandlerDescriptor(), response);
        return response;
    }

	//TODO rename endpoint to service
	private <T> void sendResponse(DestinationDescriptor destinationDescriptor,
	                              T response) {
		if (destinationDescriptor != null) {
			Delivery delivery;
			try {
				PersistentDestination destination = deliveryCreator
						.persistDestination(destinationDescriptor);
				AbstractActionEntity action = destination.getAction();
				AbstractEndpointEntity service = destination.getService();
				delivery = deliveryCreator.createDelivery(service,action,response);
			} catch (Exception ex) {
				logger.error("Ошибка создания конечной точки доставки", ex);
				return;
			}
			try {
				deliveryService.deliver(delivery);
			} catch (Exception ex) {
				logger.error("Не могу отправить ответ на дополнительный " +
						             "сервис", ex);
				return;
			}
		}
	}

	@Override
	public <T extends DestinationDescriptor,Y> ResponseDTO<List<ResponseDTO<Void>>>
	registerAutoDetection(
			IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO) {
		logger.info("Received an autodetection registration request");
		ResponseDTO<List<ResponseDTO<Void>>> response;
		try {
			List<ResponseDTO<Void>> result =
					registrationService.register(autoDetectionDTO.getPacket());
			response = new ResponseDTO<>(result);
		} catch (Exception ex) {
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}

		sendResponse(autoDetectionDTO.getResponseHandlerDescriptor(), response);
		return response;
	}

}
