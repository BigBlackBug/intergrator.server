package com.icl.integrator;

import com.icl.integrator.api.IntegratorHttpAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.services.PacketProcessor;
import com.icl.integrator.services.PacketProcessorFactory;
import com.icl.integrator.services.RegistrationService;
import com.icl.integrator.services.TargetRegistrationException;
import com.icl.integrator.util.connectors.ConnectionException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:16
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MainController implements IntegratorHttpAPI {

    private static Log logger = LogFactory.getLog(MainController.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private EndpointConnectorFactory connectorFactory;

    @Autowired
    private PacketProcessorFactory processorFactory;

    @Override
    public void deliver(@RequestBody(required = true) SourceDataDTO packet) {
        logger.info("Received a delivery request");
        PacketProcessor processor = processorFactory.createProcessor();
        processor.process(packet);
    }

    @Override
    public Boolean ping() {
        return true;
    }

    @Override
    public ResponseFromTargetDTO<Map>
    registerService(@RequestBody(required = true)
                    TargetRegistrationDTO registrationDTO) {
        logger.info("Received a service registration request");
        ResponseFromTargetDTO<Map> response;
        try {
            Map result = registrationService.register(registrationDTO);
            response = new ResponseFromTargetDTO<>(result, Map.class);
        } catch (TargetRegistrationException ex) {
            response = new ResponseFromTargetDTO<>(new ErrorDTO(ex));
        }
        return response;
    }

    @Override
    public ResponseFromTargetDTO<Boolean> isAvailable(
            @RequestBody(required = true) PingDTO pingDTO) {
        logger.info("Received a ping request for " + pingDTO);
        EndpointConnector connector = connectorFactory
                .createEndpointConnector(
                        new DestinationDTO(pingDTO.getServiceName(),
                                           pingDTO.getEndpointType()),
                        pingDTO.getAction());
        try {
            connector.testConnection();
            return new ResponseFromTargetDTO<>(Boolean.TRUE, Boolean.class);
        } catch (ConnectionException ex) {
            return new ResponseFromTargetDTO<>(new ErrorDTO(ex));
        }
    }

}
