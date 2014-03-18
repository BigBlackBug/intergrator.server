package com.icl.integrator.services;

import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.services.validation.ValidationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Override
    public  <T extends DestinationDescriptor> ResponseDTO<Map<String, ResponseDTO<String>>> deliver(
			IntegratorPacket<DeliveryDTO, T> delivery) {
        logger.info("Received a delivery request");
        ResponseDTO<Map<String, ResponseDTO<String>>> response;
        try {
	        DeliveryDTO packet = delivery.getPacket();
	        validationService.validate(packet.getRequestData());

            Deliveries deliveries =
		            deliveryCreator.createDeliveries(packet,packet.getResponseHandlerDescriptor());
            PacketProcessor processor = processorFactory.createProcessor();
            Map<String, ResponseDTO<String>> serviceToRequestID =
                    processor.process(deliveries.getDeliveries());
            serviceToRequestID.putAll(deliveries.getErrorMap());
            response = new ResponseDTO<>(serviceToRequestID);
        } catch (Exception ex) {
	        logger.error(ex);
	        response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        deliveryService.deliver(delivery.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Boolean> ping(
            IntegratorPacket<Void, T> responseHandler) {
        deliveryService.deliver(responseHandler.getResponseHandlerDescriptor(),
                     Boolean.TRUE);
	    return new ResponseDTO<>(true,Boolean.TYPE.toString());
    }

    @Override
    public <T extends ActionDescriptor, Y extends DestinationDescriptor>
    ResponseDTO<RegistrationResultDTO> registerService(
		    IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO) {
        logger.info("Received a service registration request");
        ResponseDTO<RegistrationResultDTO> response;
        try {
            Map<String, ResponseDTO<Void>> result =
                    registrationService.register(registrationDTO.getPacket());
            response = new ResponseDTO<>(new RegistrationResultDTO(
		            registrationDTO.getPacket().getServiceName(),result));
        } catch (Exception ex) {
	        logger.error(ex);
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
            workerService.pingService(serviceDescriptor.getPacket());
            response = new ResponseDTO<>(Boolean.TRUE, Boolean.class.toString());
        } catch (Exception ex) {
	        logger.error(ex);
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
	        logger.error(ex);
	        response = new ResponseDTO<>(new ErrorDTO(ex));
        }

        deliveryService.deliver(packet.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<ActionEndpointDTO>> getSupportedActions(
            IntegratorPacket<ServiceDTO, T> serviceDTO) {
        ResponseDTO<List<ActionEndpointDTO>> response;
        try {
            List<ActionEndpointDTO> actions = workerService
                    .getSupportedActions(serviceDTO.getPacket());
            response = new ResponseDTO<>(actions);
        } catch (Exception ex) {
	        logger.error(ex);
	        response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        deliveryService.deliver(serviceDTO.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public <T extends DestinationDescriptor,Y extends ActionDescriptor> ResponseDTO<Void> addAction(
            IntegratorPacket<AddActionDTO<Y>, T> actionDTO) {
        ResponseDTO<Void> response;
        try {
            workerService.addAction(actionDTO.getPacket());
            response = new ResponseDTO<>(true);
        } catch (Exception ex) {
	        logger.error(ex);
	        response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        deliveryService.deliver(actionDTO.getResponseHandlerDescriptor(), response);
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
	        logger.error(ex);
	        response = new ResponseDTO<>(new ErrorDTO(ex));
        }

        deliveryService.deliver(serviceDTO.getResponseHandlerDescriptor(), response);
        return response;
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
			logger.error(ex);
			response = new ResponseDTO<>(new ErrorDTO(ex));
		}

		deliveryService.deliver(autoDetectionDTO.getResponseHandlerDescriptor(), response);
		return response;
	}

}
