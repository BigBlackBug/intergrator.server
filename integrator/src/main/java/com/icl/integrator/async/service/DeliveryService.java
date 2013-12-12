package com.icl.integrator.async.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.*;
import com.icl.integrator.model.TaskLogEntry;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.DatabaseRetryHandler;
import com.icl.integrator.task.DatabaseRetryHandlerFactory;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.util.EndpointConnector;
import com.icl.integrator.util.EndpointConnectorFactory;
import com.icl.integrator.util.RequestScheduler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.springframework.util.StringUtils.quote;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 12.12.13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
@Service
public class DeliveryService {

    private static Log logger = LogFactory.getLog(DeliveryService.class);

    @Autowired
    protected RequestScheduler scheduler;

    @Autowired
    private EndpointConnectorFactory factory;

    @Autowired
    private DatabaseRetryHandlerFactory databaseRetryHandlerFactory;

    public UUID deliver(DestinationDTO destination,
                        SourceDataDTO packet) {
        logger.info("Scheduling a request to target " +
                            destination.getServiceName());
        EndpointConnector destinationConnector =
                factory.createEndpointConnector(destination,
                                                packet.getAction());
        EndpointConnector sourceConnector =
                factory.createEndpointConnector(packet.getSource());

        return deliver(sourceConnector, destinationConnector, destination,
                       packet);

    }

    private UUID deliver(EndpointConnector sourceConnector,
                                    EndpointConnector destinationConnector,
                                    DestinationDTO destinationDTO,
                                    SourceDataDTO packet){
        UUID requestID = UUID.randomUUID();
        logger.info("Generated an ID for the request: " + quote(
                requestID.toString()));
        //TODO add deliverycallable description
        scheduler.schedule(
                new TaskCreator<>(
                        new DeliveryCallable(destinationConnector, packet))
                        .setCallback(new DeliverySuccessCallback(
                                sourceConnector, destinationDTO, requestID)),
                new DeliveryFailedCallable(
                        sourceConnector, destinationDTO, requestID));
        return requestID;
    }

    private class DeliverySuccessCallback implements
            Callback<ResponseFromTargetDTO> {

        private final Callable<Void> successCallable = new
                Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
            ResponseToSourceDTO data =
                    new ResponseToSourceDTO(
                            responseDTO,
                            destination.getServiceName(),
                            requestID.toString());
            sourceConnector.sendRequest(data,
            /*TODO ask*/ ResponseFromTargetDTO.class);
            return null;
            }
        };

        private final DestinationDTO destination;

        private final UUID requestID;

        private final EndpointConnector sourceConnector;

        //  получаем после выполнения метода
        private ResponseFromTargetDTO responseDTO;

        private DeliverySuccessCallback(EndpointConnector sourceConnector,
                                        DestinationDTO
                                                destination, UUID requestID) {
            this.destination = destination;
            this.sourceConnector = sourceConnector;
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
                    MessageFormat.format(message,
                                         ""/*TODO endpoint toString or w/e*/),
                    node);
            handler.setLogEntry(logEntry);
            //TODO add successCallable description
            scheduler.schedule(
                    new TaskCreator<>(successCallable), handler);
        }
    }
}
