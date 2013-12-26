package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.DestinationDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.ResponseToSourceDTO;
import com.icl.integrator.dto.SourceDataDTO;
import com.icl.integrator.model.TaskLogEntry;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandler;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandlerFactory;
import com.icl.integrator.util.IntegratorException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
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

    @Autowired
    private ObjectMapper serializer;

    public UUID deliver(DestinationDTO destination,
                        SourceDataDTO packet) throws IntegratorException {
        logger.info("Scheduling a request to target " +
                            destination.getServiceName());
        EndpointConnector destinationConnector =
                factory.createEndpointConnector(destination,
                                                packet.getAction());

        DeliveryCallable deliveryCallable =
                new DeliveryCallable(destinationConnector, packet);
        if (packet.getSource() != null) {
            EndpointConnector sourceConnector = factory.createEndpointConnector(
                    packet.getSource());
            return deliver(deliveryCallable, sourceConnector, destination);
        }
        return deliver(deliveryCallable, destination, packet);
    }

    private UUID deliver(DeliveryCallable deliveryCallable,
                         DestinationDTO destinationDTO,
                         SourceDataDTO packet) {
        UUID requestID = UUID.randomUUID();
        logger.info("Generated an ID for the request: " + quote(
                requestID.toString()));

        DatabaseRetryHandler handler =
                databaseRetryHandlerFactory.createHandler();
        TaskLogEntry logEntry = new TaskLogEntry();
        String generalMessage = "Не могу доставить запрос {0} " +
                "на сервис {1}";
        String targetServiceName = destinationDTO.getServiceName();
        generalMessage = MessageFormat.format(generalMessage, requestID,
                                              targetServiceName);
        logEntry.setMessage(generalMessage);
        String dataJson = null;
        try {
            dataJson = serializer.writeValueAsString(packet.getData());
        } catch (JsonProcessingException e) {
            logEntry.setAdditionalMessage(e.getMessage());
            logger.info("Unable to serialize incoming json data");
        }
        logEntry.setDataJson(dataJson);
        handler.setLogEntry(logEntry);

        TaskCreator<ResponseFromTargetDTO> deliveryTaskCreator =
                new TaskCreator<>(deliveryCallable);
        //TODO add deliverycallable description
        scheduler.schedule(deliveryTaskCreator, handler);
        return requestID;
    }

    private UUID deliver(DeliveryCallable deliveryCallable,
                         EndpointConnector sourceConnector,
                         DestinationDTO destinationDTO) {
        UUID requestID = UUID.randomUUID();
        logger.info("Generated an ID for the request: " + quote(
                requestID.toString()));
        //TODO add deliverycallable description
        TaskCreator<ResponseFromTargetDTO> deliveryTaskCreator =
                new TaskCreator<>(deliveryCallable);
        Callable<Void> deliveryFailedCallable = new DeliveryFailedCallable(
                sourceConnector, destinationDTO, requestID);
        deliveryTaskCreator.setCallback(new DeliverySuccessCallback(
                sourceConnector, destinationDTO, requestID));

        scheduler.schedule(deliveryTaskCreator, deliveryFailedCallable);
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
