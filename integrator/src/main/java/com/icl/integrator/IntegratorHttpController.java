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

    @Override
    public void deliver(@RequestBody(required = true) DeliveryDTO packet) {
        logger.info("Received a delivery request");
        PacketProcessor processor = processorFactory.createProcessor();
        processor.process(packet);
    }

    @Override
    public Boolean ping() {
        return true;
    }

    //TODO test
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
        try {
            connector.testConnection();
            return new ResponseDTO<>(Boolean.TRUE, Boolean.class);
        } catch (ConnectionException ex) {
            return new ResponseDTO<>(new ErrorDTO(ex));
        }
    }

    //TODO test
    @Override
    public ResponseDTO<List<ServiceDTO>> getServiceList() {
        ResponseDTO<List<ServiceDTO>> response;
        try {
            List<ServiceDTO> serviceList = integratorService.getServiceList();
            response = new ResponseDTO<>(serviceList);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        return response;
    }

    @Override
    public ResponseDTO<List<String>> getSupportedActions(
            @RequestBody(required = true) ServiceDTO serviceDTO) {
        ResponseDTO<List<String>> response;
        try {
            List<String> actions =
                    integratorService.getSupportedActions(serviceDTO);
            response = new ResponseDTO<>(actions);
        } catch (Exception ex) {
            response = new ResponseDTO<>(new ErrorDTO(ex));
        }
        return response;
    }

}
