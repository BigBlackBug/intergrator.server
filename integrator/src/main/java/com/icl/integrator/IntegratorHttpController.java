package com.icl.integrator;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.services.*;
import com.icl.integrator.springapi.IntegratorHttpAPI;
import com.icl.integrator.util.connectors.ConnectionException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:16
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class IntegratorHttpController implements IntegratorHttpAPI {

    private static Log logger =
            LogFactory.getLog(IntegratorHttpController.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private EndpointConnectorFactory connectorFactory;

    @Autowired
    private PacketProcessorFactory processorFactory;

    @Autowired
    private IntegratorService integratorService;

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public Map<String, ResponseDTO<UUID>> deliver(
            @RequestBody(required = true) DeliveryDTO packet) {
        logger.info("Received a delivery request");
        PacketProcessor processor = processorFactory.createProcessor();
        Map<String, ResponseDTO<UUID>> serviceToRequestID =
                processor.process(packet);
        RawDestinationDescriptorDTO sourceService =
                packet.getIntegratorResponseHandler();
        if (sourceService != null) {
            deliveryService.deliver(sourceService, serviceToRequestID);
        }
        return serviceToRequestID;
    }

    @Override
    public Boolean ping(@RequestBody(required = true)
                        RawDestinationDescriptorDTO responseHandler) {
        if (responseHandler != null && responseHandler.isInitialized()) {
            deliveryService.deliver(responseHandler, Boolean.TRUE);
        }
        return true;
    }

    @Override
    public ResponseDTO<Map<String, ResponseDTO<Void>>>
    registerService(@RequestBody(required = true)
                    TargetRegistrationDTO<?> registrationDTO) {
        logger.info("Received a service registration request");
        ResponseDTO<Map<String, ResponseDTO<Void>>> response;
        try {
            Map<String, ResponseDTO<Void>> result =
                    registrationService.register(registrationDTO);
            response = new ResponseDTO<>(result);
        } catch (TargetRegistrationException ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        RawDestinationDescriptorDTO responseHandler =
                registrationDTO.getIntegratorResponseHandler();
        if (responseHandler != null) {
            deliveryService.deliver(responseHandler, response);
        }
        return response;
    }

    @Override
    public ResponseDTO<Boolean> isAvailable(
            @RequestBody(required = true) PingDTO pingDTO) {
        logger.info("Received a ping request for " + pingDTO);
        EndpointConnector connector = connectorFactory
                .createEndpointConnector(
                        new DestinationDTO(pingDTO.getServiceName(),
                                           pingDTO.getEndpointType()),
                        pingDTO.getAction());
        ResponseDTO<Boolean> responseDTO;
        try {
            connector.testConnection();
            responseDTO =
                    new ResponseDTO<>(Boolean.TRUE, Boolean.class);
        } catch (ConnectionException ex) {
            responseDTO = new ResponseDTO<>(new ErrorDTO(ex));
        }
        RawDestinationDescriptorDTO responseHandler =
                pingDTO.getIntegratorResponseHandler();
        if (responseHandler != null) {
            deliveryService.deliver(responseHandler, responseDTO);
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO<List<ServiceDTO>> getServiceList(
            @RequestBody(required = false)
            RawDestinationDescriptorDTO responseHandler) {
        ResponseDTO<List<ServiceDTO>> response;
        try {
            List<ServiceDTO> serviceList = integratorService.getServiceList();
            response = new ResponseDTO<>(serviceList);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        if (responseHandler != null && responseHandler.isInitialized()) {
            deliveryService.deliver(responseHandler, response);
        }
        return response;
    }

    @Override
    public ResponseDTO<List<String>> getSupportedActions(
            @RequestBody(required = true) ServiceDTOWithResponseHandler serviceDTO) {
        ResponseDTO<List<String>> response;
        try {
            List<String> actions =
                    integratorService
                            .getSupportedActions(serviceDTO.getServiceDTO());
            response = new ResponseDTO<>(actions);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        RawDestinationDescriptorDTO responseHandler =
                serviceDTO.getIntegratorResponseHandler();
        if (responseHandler != null) {
            deliveryService.deliver(responseHandler, response);
        }
        return response;
    }


}
