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

import java.util.HashMap;
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
    public void process(SourceDataDTO packet) {
//        logger.info(MessageFormat.format("Received a request from source " +
//                                                 "({0}:{1,number,#})",
//                                         request.getRemoteHost(),
//                                         request.getRemotePort()));
        PacketProcessor processor = processorFactory.createProcessor();
        processor.process(packet);
    }

    @Override
    public Map<String, String> ping() {
        return new HashMap<String, String>() {{
            put("result", "true");
        }};
    }

    @Override
    public ResponseFromTargetDTO<Map>
    registerTarget(TargetRegistrationDTO registrationDTO) {
//        logger.info(MessageFormat.format("Received a registration request " +
//                                                 "from source " +
//                                                 "({0}:{1,number,#})",
//                                         request.getRemoteHost(),
//                                         request.getRemotePort()));
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
    public ResponseFromTargetDTO<Boolean> isAvailable(PingDTO pingDTO) {
//        logger.info(MessageFormat.format("Received a registration request " +
//                                                 "from source " +
//                                                 "({0}:{1,number,#})",
//                                         request.getRemoteHost(),
//                                         request.getRemotePort()));
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
