package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.model.RequestLogEntry;
import com.icl.integrator.util.IntegratorObjectMapper;
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
    public Map<String, ResponseDTO<UUID>> deliver(
            IntegratorPacket<DeliveryDTO> delivery) {
        logger.info("Received a delivery request");
        Date requestTime = new Date();
        PacketProcessor processor = processorFactory.createProcessor();
        Map<String, ResponseDTO<UUID>> serviceToRequestID =
                processor.process(delivery.getPacket());

        RequestLogEntry logEntry =
                createLogEntry(RequestLogEntry.RequestType.DELIVERY,
                               requestTime, delivery, serviceToRequestID);
        persistenceService.save(logEntry);
        DestinationDescriptorDTO responseDestDesc =
                delivery.getResponseHandlerDescriptor();
        if (responseDestDesc != null) {
            deliveryService.deliver(responseDestDesc, serviceToRequestID);
        }
        return serviceToRequestID;
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

    private <Result, Req> Result des(Req value, TypeReference<Result> ref) {
        IntegratorObjectMapper objectMapper =
                new IntegratorObjectMapper();

        return objectMapper.convertValue(value, ref);
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
        persistenceService.save(logEntry);

        DestinationDescriptorDTO responseDestDesc =
                registrationDTO.getResponseHandlerDescriptor();
        if (responseDestDesc != null) {
            deliveryService.deliver(responseDestDesc, response);
        }
        return response;
    }

    @Override
    public ResponseDTO<Boolean> isAvailable(IntegratorPacket<PingDTO> pingDTO) {
        logger.info("Received a ping request for " + pingDTO);
        ResponseDTO<Boolean> response;
        try {
            workerService.pingService(pingDTO.getPacket());
            response = new ResponseDTO<>(Boolean.TRUE);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        DestinationDescriptorDTO responseDesc =
                pingDTO.getResponseHandlerDescriptor();
        if (responseDesc != null) {
            deliveryService.deliver(responseDesc, response);
        }
        return response;
    }

    @Override
    public ResponseDTO<List<ServiceDTO>> getServiceList(
            IntegratorPacket<Void> responseHandlerDescriptor) {
        ResponseDTO<List<ServiceDTO>> response;
        try {
            List<ServiceDTO> serviceList = workerService.getServiceList();
            response = new ResponseDTO<>(serviceList);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }

        DestinationDescriptorDTO descriptor =
                responseHandlerDescriptor.getResponseHandlerDescriptor();
        if (descriptor != null && descriptor.isInitialized()) {
            deliveryService.deliver(descriptor, response);
        }
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
        DestinationDescriptorDTO responseHandler =
                serviceDTO.getResponseHandlerDescriptor();
        if (responseHandler != null) {
            deliveryService.deliver(responseHandler, response);
        }
        return response;
    }

    @Override
    public ResponseDTO addAction(IntegratorPacket<AddActionDTO> actionDTO) {
        ResponseDTO response;
        try {
            workerService.addAction(actionDTO.getPacket());
            response = new ResponseDTO(true);
        } catch (Exception ex) {
            response = new ResponseDTO(new ErrorDTO(ex));
        }
        DestinationDescriptorDTO responseHandler =
                actionDTO.getResponseHandlerDescriptor();
        if (responseHandler != null) {
            deliveryService.deliver(responseHandler, response);
        }
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

        DestinationDescriptorDTO responseHandler =
                serviceDTO.getResponseHandlerDescriptor();
        if (responseHandler != null) {
            deliveryService.deliver(responseHandler, response);
        }
        return response;
    }
}
