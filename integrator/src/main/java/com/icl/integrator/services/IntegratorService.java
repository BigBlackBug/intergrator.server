package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.model.RequestLogEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    private PersistenceService persistenceService;

    @Autowired
    private ObjectMapper objectMapper;

    private <Request, Response> RequestLogEntry createLogEntry(
            RequestLogEntry.RequestType requestType, Date requestTime,
            Request requestData, Response responseData) {
        RequestLogEntry entry = new RequestLogEntry();
        try {
            entry.setRequestData(objectMapper.writeValueAsString(requestData));
        } catch (JsonProcessingException e) {
            logger.info("Unable to convert request entity to json");
            return null;
        }
        entry.setRequestDate(requestTime);
        entry.setRequestType(requestType);
        try {
            entry.setResponseData(
                    objectMapper.writeValueAsString(responseData));
        } catch (JsonProcessingException e) {
            logger.info("Unable to convert request entity to json");
            return null;
        }
        return entry;
    }

    @Override
    public ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
            IntegratorPacket<DeliveryDTO> delivery) {
        logger.info("Received a delivery request");
        ResponseDTO<Map<String, ResponseDTO<UUID>>> response;
        try {
            Date requestTime = new Date();
            PacketProcessor processor = processorFactory.createProcessor();
            Map<String, ResponseDTO<UUID>> serviceToRequestID =
                    processor.process(delivery.getPacket());

            response = new ResponseDTO<>(serviceToRequestID);
            RequestLogEntry logEntry =
                    createLogEntry(RequestLogEntry.RequestType.DELIVERY,
                                   requestTime, delivery, serviceToRequestID);
            persistenceService.merge(logEntry);
        } catch (Exception ex) {
            response = new ResponseDTO<>(ex);
        }
        sendResponse(delivery.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public Boolean ping(IntegratorPacket<Void> responseHandler) {
        DestinationDescriptorDTO descriptor =
                responseHandler.getResponseHandlerDescriptor();
        if (descriptor != null && descriptor.isInitialized()) {
            deliveryService.deliver(descriptor, Boolean.TRUE);
        }
        return true;
    }

    @Override
    public <T extends ActionDescriptor>
    ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            IntegratorPacket<TargetRegistrationDTO<T>> registrationDTO) {
        logger.info("Received a service registration request");
        Date requestTime = new Date();
        ResponseDTO<Map<String, ResponseDTO<Void>>> response;
        try {
            Map<String, ResponseDTO<Void>> result =
                    registrationService.register(registrationDTO.getPacket());
            response = new ResponseDTO<>(result);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        RequestLogEntry logEntry =
                createLogEntry(RequestLogEntry.RequestType.REGISTRATION,
                               requestTime, registrationDTO, response);
        persistenceService.merge(logEntry);

        sendResponse(registrationDTO.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public ResponseDTO<Boolean> isAvailable(IntegratorPacket<PingDTO> pingDTO) {
        logger.info("Received a ping request for " + pingDTO);
        ResponseDTO<Boolean> response;
        try {
            workerService.pingService(pingDTO.getPacket());
            response = new ResponseDTO<>(Boolean.TRUE, Boolean.class);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        sendResponse(pingDTO.getResponseHandlerDescriptor(), response);
        return response;
    }

    @Override
    public ResponseDTO<List<ServiceDTO>> getServiceList(
            IntegratorPacket<Void> packet) {
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
    public ResponseDTO<List<String>> getSupportedActions(
            IntegratorPacket<ServiceDTO> serviceDTO) {
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
    public ResponseDTO<Void> addAction(
            IntegratorPacket<AddActionDTO> actionDTO) {
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
    public <T extends EndpointDescriptor, Y extends ActionDescriptor>
    ResponseDTO<FullServiceDTO<T, Y>> getServiceInfo(
            IntegratorPacket<ServiceDTO> serviceDTO) {
        ResponseDTO<FullServiceDTO<T, Y>> response;
        try {
            FullServiceDTO<T, Y> serviceInfo =
                    workerService
                            .getServiceInfo(serviceDTO.getPacket());
            response = new ResponseDTO<>(serviceInfo);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }

        sendResponse(serviceDTO.getResponseHandlerDescriptor(), response);
        return response;
    }

    private <T> void sendResponse(
            DestinationDescriptorDTO responseHandler, T response) {
        if (responseHandler != null) {
            try {
                deliveryService.deliver(responseHandler, response);
            } catch (Exception ex) {
                logger.error("Не могу отправить ответ на дополнительный " +
                                     "эндпоинт", ex);
            }
        }
    }

}
