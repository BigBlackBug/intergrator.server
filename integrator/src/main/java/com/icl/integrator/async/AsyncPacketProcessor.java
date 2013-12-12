package com.icl.integrator.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.source.HttpSourceEndpointDTO;
import com.icl.integrator.model.TaskLogEntry;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.DatabaseRetryHandler;
import com.icl.integrator.task.DatabaseRetryHandlerFactory;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.util.EndpointConnector;
import com.icl.integrator.util.EndpointConnectorFactory;
import com.icl.integrator.util.EndpointType;
import com.icl.integrator.util.RequestScheduler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.springframework.util.StringUtils.quote;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public class AsyncPacketProcessor {

    private static Log logger =
            LogFactory.getLog(AsyncPacketProcessor.class);

    @Autowired
    private RequestScheduler scheduler;

    @Autowired
    private EndpointConnectorFactory endpointConnectorFactory;

    @Autowired
    private DatabaseRetryHandlerFactory databaseRetryHandlerFactory;

    public Map<String, String> process(SourceDataDTO packet) {
        Map<String, String> serviceToRequestID = new HashMap<>();
        for (EndpointDTO destination : packet.getDestinations()) {
            UUID requestID = processDestination(destination, packet);
            serviceToRequestID.put(destination.getServiceName(),
                                   requestID.toString());
        }
        return serviceToRequestID;
    }

    private UUID processHttp(final EndpointDTO destination,
                             HttpSourceEndpointDTO sourceEndpoint,
                             SourceDataDTO packet) {
        URI sourceServiceURL = null;
        try {
            sourceServiceURL = new URL("HTTP", sourceEndpoint.getHost(),
                                       sourceEndpoint.getPort(),
                                       sourceEndpoint.getPath()).toURI();
        } catch (MalformedURLException e) {
            e.printStackTrace(); //TODO
            throw new RuntimeException();
        } catch (URISyntaxException e) {
            e.printStackTrace();       //TODO
            throw new RuntimeException();
        }
        UUID requestID = UUID.randomUUID();
        logger.info("Generated an ID for the request: " + quote(
                requestID.toString()));
        logger.info("Scheduling a request to target " +
                            destination.getServiceName());
        //TODO add deliverycallable description
        scheduler.schedule(
                new TaskCreator<>(new DeliveryCallable(destination, packet))
                        .setCallback(new DeliverySuccessCallback(
                                sourceServiceURL, destination, requestID)),
                new DeliveryFailedCallable(
                        sourceServiceURL, destination, requestID));
        return requestID;
    }

    private UUID processDestination(final EndpointDTO destination,
                                    final SourceDataDTO packet) {
        SourceEndpointDTO source = packet.getSource();
        EndpointType endpointType = source.getEndpointType();
        if (endpointType == EndpointType.HTTP) {
            HttpSourceEndpointDTO descriptor =
                    (HttpSourceEndpointDTO) source.getDescriptor();
            return processHttp(destination, descriptor, packet);
        } else {
            throw new RuntimeException("Not yet implemented");
        }
    }

    private class DeliveryFailedCallable implements Callable<Void> {

        private final URI sourceServiceURL;

        private final EndpointDTO targetDestination;

        private final UUID requestID;

        private DeliveryFailedCallable(URI sourceServiceURL,
                                       EndpointDTO targetDestination, UUID requestID) {
            this.sourceServiceURL = sourceServiceURL;
            this.targetDestination = targetDestination;
            this.requestID = requestID;
        }

        @Override
        public Void call() throws Exception {
            RestTemplate template = new RestTemplate();
            String generalMessage = "Не могу доставить запрос {0} " +
                    "на сервис {1}";
            String targetServiceName = targetDestination.getServiceName();
            ErrorDTO errorDTO = new ErrorDTO(MessageFormat.format
                    (generalMessage,requestID, targetServiceName));
            ResponseFromTargetDTO<Object> responseDTO =
                    new ResponseFromTargetDTO<>(errorDTO);
            template.postForObject(
                    sourceServiceURL,
                    new ResponseToSourceDTO(
                            responseDTO,
                            targetServiceName,
                            requestID.toString()),
                    ResponseFromTargetDTO.class);
            return null;
        }
    }

    private class DeliveryCallable implements Callable<ResponseFromTargetDTO> {

        private final EndpointDTO destination;

        private final SourceDataDTO packet;

        private DeliveryCallable(EndpointDTO destination, SourceDataDTO packet) {
            this.destination = destination;
            this.packet = packet;
        }

        @Override
        public ResponseFromTargetDTO call() throws Exception {
            EndpointConnector connector = endpointConnectorFactory
                    .createEndpointConnector(
                            destination,
                            packet.getAction());
            return connector.sendRequest(packet);
        }
    }

    private class DeliverySuccessCallback implements
            Callback<ResponseFromTargetDTO> {

        private final Callable<Void> successCallable = new
                Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
            RestTemplate template = new RestTemplate();
            template.postForObject(
                    sourceServiceURL,
                    new ResponseToSourceDTO(
                            responseDTO,
                            destination.getServiceName(),
                            requestID.toString()),
                    ResponseFromTargetDTO.class);
            return null;
            }
        };

        private final URI sourceServiceURL;

        private final EndpointDTO destination;

        private final UUID requestID;

        private ResponseFromTargetDTO responseDTO;

        private DeliverySuccessCallback(URI sourceServiceURL,
                                        EndpointDTO destination, UUID requestID) {
            this.sourceServiceURL = sourceServiceURL;
            this.destination = destination;
            this.requestID = requestID;
        }

        @Override
        public void execute(ResponseFromTargetDTO responseDTO) {
            logger.info("Sending response to the source from " + destination
                    .getServiceName());
            this.responseDTO = responseDTO;
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.valueToTree(responseDTO);
            String message = "Не смогли вернуть запрос " +
                    "на источник по адресу {0}";
            DatabaseRetryHandler handler =
                    databaseRetryHandlerFactory.createHandler();
            TaskLogEntry logEntry = new TaskLogEntry(
                    MessageFormat.format(message,sourceServiceURL),
                                     node);
            handler.setLogEntry(logEntry);
            //TODO add successCallable description
            scheduler.schedule(
                    new TaskCreator<>(successCallable), handler);
        }
    }

}
