package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.*;
import com.icl.integrator.model.TaskLogEntry;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.Descriptor;
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
import java.util.Map;
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

    public void deliver(ServiceDTO sourceService,
                        Map<String, ResponseFromTargetDTO<String>> resultMap)
            throws IntegratorException {
        logger.info("Scheduling a request back to service " +
                            "defined as source -> " +
                            sourceService.getSourceResponseAction());
        //TODO sourceResponse may be null
        EndpointConnector sourceConnector = factory.createEndpointConnector
                (sourceService.getEndpoint(),
                 sourceService.getSourceResponseAction());
        DeliveryCallable<Map<String, ResponseFromTargetDTO<String>>>
                deliveryCallable =
                new DeliveryCallable<>(sourceConnector, resultMap);
        scheduler.schedule(new TaskCreator<>(deliveryCallable));
    }

    public UUID deliver(DestinationDTO destination,
                        SourceDataDTO packet) throws IntegratorException {
        logger.info("Scheduling a request to target " +
                            destination.getServiceName());
        EndpointConnector destinationConnector =
                factory.createEndpointConnector(destination,
                                                packet.getAction());
        //---
        RequestToTargetDTO dto = new RequestToTargetDTO();
        dto.setAdditionalData(packet.getAdditionalData());
        dto.setData(packet.getData());
        DeliveryCallable<RequestToTargetDTO> deliveryCallable =
                new DeliveryCallable<>(destinationConnector, dto);
        ServiceDTO sourceService = packet.getSource();
        if (sourceService != null) {
            //TODO targetResponse may be null
            EndpointDTO endpoint = sourceService.getEndpoint();
            EndpointConnector sourceConnector = factory
                    .createEndpointConnector(endpoint,
                                             sourceService.getTargetResponseAction());
            return deliver(deliveryCallable, sourceConnector, destination);
        }
        return deliver(deliveryCallable, destination, packet);
    }

    private <T> UUID deliver(final DeliveryCallable<T> deliveryCallable,
                             DestinationDTO destinationDTO,
                             SourceDataDTO packet) {
        UUID requestID = UUID.randomUUID();
        logger.info("Generated an ID for the request: " + quote(
                requestID.toString()));

        DatabaseRetryHandler handler =
                createRetryHandler(packet, destinationDTO, requestID);

        TaskCreator<ResponseFromTargetDTO> deliveryTaskCreator =
                new TaskCreator<>(deliveryCallable);
        deliveryTaskCreator.setDescriptor(
            new Descriptor<TaskCreator<ResponseFromTargetDTO>>() {
                @Override
                public String describe(
                        TaskCreator<ResponseFromTargetDTO> creator) {
                return "Отправка запроса: " +
                        deliveryCallable.getConnector().toString();
                }
            });
        //
        if (destinationDTO.scheduleRedelivery()) {
            scheduler.schedule(deliveryTaskCreator, handler);
        } else {
            scheduler.schedule(deliveryTaskCreator);
        }
        return requestID;
    }

    private DatabaseRetryHandler createRetryHandler(
            SourceDataDTO packet, DestinationDTO destinationDTO,
            UUID requestID) {
        DatabaseRetryHandler handler =
                databaseRetryHandlerFactory.createHandler();
        RequestToTargetDTO request = new RequestToTargetDTO();
        request.setData(packet.getData());
        request.setAdditionalData(packet.getAdditionalData());
        TaskLogEntry logEntry =
                createTaskLogEntry(request, destinationDTO, requestID);
        handler.setLogEntry(logEntry);
        return handler;
    }

    private TaskLogEntry createTaskLogEntry(RequestToTargetDTO packet,
                                            DestinationDTO destinationDTO,
                                            UUID requestID) {
        TaskLogEntry logEntry = new TaskLogEntry();
        String generalMessage = "Не могу доставить запрос {0} " +
                "на сервис {1}";
        String targetServiceName = destinationDTO.getServiceName();
        generalMessage = MessageFormat.format(generalMessage, requestID,
                                              targetServiceName);
        logEntry.setMessage(generalMessage);

        String dataJson = null;
        try {
            dataJson = serializer.writeValueAsString(packet);
        } catch (JsonProcessingException e) {
            logEntry.setAdditionalMessage(e.getMessage());
            logger.info("Unable to serialize incoming json data");
        }
        logEntry.setDataJson(dataJson);
        return logEntry;
    }

    private <T> UUID deliver(final DeliveryCallable<T> deliveryCallable,
                             EndpointConnector sourceConnector,
                             DestinationDTO destinationDTO) {
        UUID requestID = UUID.randomUUID();
        logger.info("Generated an ID for the request: " + quote(
                requestID.toString()));
        TaskCreator<ResponseFromTargetDTO> deliveryTaskCreator =
                new TaskCreator<>(deliveryCallable);
        deliveryTaskCreator.setDescriptor(
            new Descriptor<TaskCreator<ResponseFromTargetDTO>>() {
                @Override
                public String describe(
                        TaskCreator<ResponseFromTargetDTO> creator) {
                return "Отправка запроса: " +
                        deliveryCallable.getConnector().toString();
                }
            });
        Callable<Void> deliveryFailedCallable = new DeliveryFailedCallable(
                sourceConnector, destinationDTO, requestID);
        deliveryTaskCreator.setCallback(new DeliverySuccessCallback(
                sourceConnector, destinationDTO, requestID));

        if (destinationDTO.scheduleRedelivery()) {
            scheduler.schedule(deliveryTaskCreator, deliveryFailedCallable);
        } else {
            scheduler.schedule(deliveryTaskCreator);
        }
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
                        sourceConnector
                                .sendRequest(data, ResponseFromTargetDTO.class);
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
                    MessageFormat.format(message, sourceConnector.toString()),
                    node);
            handler.setLogEntry(logEntry);
            TaskCreator<Void> taskCreator = new TaskCreator<>(successCallable);
            taskCreator.setDescriptor(
                    new Descriptor<TaskCreator<Void>>() {
                        @Override
                        public String describe(
                                TaskCreator<Void> creator) {
                            return "Отправка запроса: " + sourceConnector
                                    .toString();
                        }
                    });
            scheduler.schedule(taskCreator, handler);
        }
    }
}
