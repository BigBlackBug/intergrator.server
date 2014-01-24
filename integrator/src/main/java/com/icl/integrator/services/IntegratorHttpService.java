package com.icl.integrator.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.util.IntegratorObjectMapper;
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
public class IntegratorHttpService implements IntegratorAPI {

    private static Log logger =
            LogFactory.getLog(IntegratorHttpService.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private PacketProcessorFactory processorFactory;

    @Autowired
    private IntegratorService integratorService;

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public Map<String, ResponseDTO<UUID>> deliver(
            IntegratorPacket<DeliveryDTO> delivery) {
        logger.info("Received a delivery request");
        PacketProcessor processor = processorFactory.createProcessor();
        Map<String, ResponseDTO<UUID>> serviceToRequestID =
                processor.process(delivery.getPacket());
        DestinationDescriptorDTO responseDestDesc =
                delivery.getResponseDestinationDescriptor();
        if (responseDestDesc != null) {
            deliveryService.deliver(responseDestDesc, serviceToRequestID);
        }
        return serviceToRequestID;
    }

    @Override
    public Boolean ping(IntegratorPacket<Void> responseHandler) {
        DestinationDescriptorDTO descriptor =
                responseHandler.getResponseDestinationDescriptor();
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
        ResponseDTO<Map<String, ResponseDTO<Void>>> response;
        TypeReference<IntegratorPacket<TargetRegistrationDTO<T>>> ref =
                new TypeReference<IntegratorPacket<TargetRegistrationDTO<T>>>() {
                };
        IntegratorPacket<TargetRegistrationDTO<T>> o = des(registrationDTO,
                                                           ref);
        try {
            Map<String, ResponseDTO<Void>> result =
                    registrationService.register(registrationDTO.getPacket());
            response = new ResponseDTO<>(result);
        } catch (TargetRegistrationException ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        DestinationDescriptorDTO responseDestDesc =
                registrationDTO.getResponseDestinationDescriptor();
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
            integratorService.pingService(pingDTO.getPacket());
            response = new ResponseDTO<>(Boolean.TRUE);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        DestinationDescriptorDTO responseDesc =
                pingDTO.getResponseDestinationDescriptor();
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
            List<ServiceDTO> serviceList = integratorService.getServiceList();
            response = new ResponseDTO<>(serviceList);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }

        DestinationDescriptorDTO descriptor =
                responseHandlerDescriptor.getResponseDestinationDescriptor();
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
            List<String> actions = integratorService
                    .getSupportedActions(serviceDTO.getPacket());
            response = new ResponseDTO<>(actions);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        DestinationDescriptorDTO responseHandler =
                serviceDTO.getResponseDestinationDescriptor();
        if (responseHandler != null) {
            deliveryService.deliver(responseHandler, response);
        }
        return response;
    }

    @Override
    public ResponseDTO addAction(IntegratorPacket<AddActionDTO> actionDTO) {
        ResponseDTO response;
        try {
            integratorService.addAction(actionDTO.getPacket());
            response = new ResponseDTO(true);
        } catch (Exception ex) {
            response = new ResponseDTO(new ErrorDTO(ex));
        }
        DestinationDescriptorDTO responseHandler =
                actionDTO.getResponseDestinationDescriptor();
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
                    integratorService
                            .getServiceInfo(serviceDTO.getPacket());
            response = new ResponseDTO<>(serviceInfo);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }

        DestinationDescriptorDTO responseHandler =
                serviceDTO.getResponseDestinationDescriptor();
        if (responseHandler != null) {
            deliveryService.deliver(responseHandler, response);
        }
        return response;
    }
}
