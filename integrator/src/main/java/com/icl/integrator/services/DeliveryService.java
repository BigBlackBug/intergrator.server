package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.ActionDescriptor;
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
//TODO Подумать, а нахера все эти дженерики?
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

    public <T> void deliver(DestinationDescriptor sourceService, T packet)
            throws IntegratorException {
        EndpointConnector sourceConnector;
        DestinationDescriptor.DescriptorType descriptorType =
                sourceService.getDescriptorType();
        if (descriptorType == DestinationDescriptor.DescriptorType.RAW) {
            RawDestinationDescriptor realSourceService =
                    (RawDestinationDescriptor) sourceService;
            logger.info("Scheduling a request to service " +
                                "defined as source -> " +
                                realSourceService.getActionDescriptor());
            sourceConnector = factory.createEndpointConnector(
                    realSourceService.getEndpoint(),
                    realSourceService.getActionDescriptor());
        } else if (descriptorType == DestinationDescriptor.DescriptorType.SERVICE) {
            ServiceDestinationDescriptor realSourceService =
                    (ServiceDestinationDescriptor) sourceService;
            sourceConnector = factory.createEndpointConnector(
                    realSourceService.getServiceName(),
                    realSourceService.getEndpointType(),
                    realSourceService.getActionName());
        } else {
            throw new IntegratorException("У DestinationDescriptor'а неверно " +
                                                  "проставлен тип");
        }

        DeliveryCallable<ResponseFromIntegratorDTO<T>>
                deliveryCallable =
                new DeliveryCallable<ResponseFromIntegratorDTO<T>>(sourceConnector,
                                       new ResponseFromIntegratorDTO<T>(packet));
        scheduler.schedule(new TaskCreator<ResponseDTO>(deliveryCallable));
    }

    public UUID deliver(DestinationDTO destination,
                        DeliveryDTO packet) throws IntegratorException {
        logger.info("Scheduling a request to target " +
                            destination.getServiceName());
        EndpointConnector destinationConnector =
                factory.createEndpointConnector(destination,
                                                packet.getAction());
        DeliveryCallable<RequestDataDTO> deliveryCallable =
                new DeliveryCallable<RequestDataDTO>(destinationConnector,
                                       packet.getRequestData());
        RawDestinationDescriptor targetResponseHandler =
                packet.getTargetResponseHandlerDescriptor();
        if (targetResponseHandler != null) {
            EndpointDTO endpoint = targetResponseHandler.getEndpoint();
            ActionDescriptor actionDescriptor = targetResponseHandler
                    .getActionDescriptor();
            EndpointConnector targetResponseConnector = factory
                    .createEndpointConnector(endpoint, actionDescriptor);
            return deliver(deliveryCallable, targetResponseConnector,
                           destination);
        }
        return deliver(deliveryCallable, destination, packet);
    }

    private <T> UUID deliver(final DeliveryCallable<T> deliveryCallable,
                             DestinationDTO destinationDTO,
                             DeliveryDTO packet) {
        UUID requestID = UUID.randomUUID();
        logger.info("Generated an ID for the request: " + quote(
                requestID.toString()));

        DatabaseRetryHandler handler =
                createRetryHandler(packet, destinationDTO, requestID);

        TaskCreator<ResponseDTO> deliveryTaskCreator =
                new TaskCreator<ResponseDTO>(deliveryCallable);
        deliveryTaskCreator.setDescriptor(
                new Descriptor<TaskCreator<ResponseDTO>>() {
                    @Override
                    public String describe(
                            TaskCreator<ResponseDTO> creator) {
                        return "Отправка запроса: " +
                                deliveryCallable.getConnector().toString();
                    }
                });
        if (destinationDTO.scheduleRedelivery()) {
            scheduler.schedule(deliveryTaskCreator, handler);
        } else {
            scheduler.schedule(deliveryTaskCreator);
        }
        return requestID;
    }

    private DatabaseRetryHandler createRetryHandler(
            DeliveryDTO packet, DestinationDTO destinationDTO,
            UUID requestID) {
        DatabaseRetryHandler handler =
                databaseRetryHandlerFactory.createHandler();
        TaskLogEntry logEntry =
                createTaskLogEntry(packet.getRequestData(), destinationDTO,
                                   requestID);
        handler.setLogEntry(logEntry);
        return handler;
    }

    private TaskLogEntry createTaskLogEntry(RequestDataDTO packet,
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
                             EndpointConnector targetResponseConnector,
                             DestinationDTO destinationDTO) {
        UUID requestID = UUID.randomUUID();
        logger.info("Generated an ID for the request: " + quote(
                requestID.toString()));
        TaskCreator<ResponseDTO> deliveryTaskCreator =
                new TaskCreator<ResponseDTO>(deliveryCallable);
        deliveryTaskCreator.setDescriptor(
                new Descriptor<TaskCreator<ResponseDTO>>() {
                    @Override
                    public String describe(
                            TaskCreator<ResponseDTO> creator) {
                        return "Отправка запроса: " +
                                deliveryCallable.getConnector().toString();
                    }
                });
        Callable<Void> deliveryFailedCallable = new DeliveryFailedCallable(
                targetResponseConnector, destinationDTO, requestID);
        deliveryTaskCreator.setCallback(new DeliverySuccessCallback(
                targetResponseConnector, destinationDTO, requestID));

        if (destinationDTO.scheduleRedelivery()) {
            scheduler.schedule(deliveryTaskCreator, deliveryFailedCallable);
        } else {
            scheduler.schedule(deliveryTaskCreator);
        }
        return requestID;
    }

    private class DeliverySuccessCallback implements
            Callback<ResponseDTO> {

        private final Callable<Void> successCallable = new
                Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ResponseFromTargetDTO data =
                                new ResponseFromTargetDTO(
                                        responseDTO,
                                        destination.getServiceName(),
                                        requestID.toString());
                        targetResponseConnector
                                .sendRequest(data, ResponseDTO.class);
                        return null;
                    }
                };

        private final DestinationDTO destination;

        private final UUID requestID;

        private final EndpointConnector targetResponseConnector;

        //  получаем после выполнения метода
        private ResponseDTO responseDTO;

        private DeliverySuccessCallback(
                EndpointConnector targetResponseConnector,
                DestinationDTO
                        destination, UUID requestID) {
            this.destination = destination;
            this.targetResponseConnector = targetResponseConnector;
            this.requestID = requestID;
        }

        @Override
        public void execute(ResponseDTO responseDTO) {
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
                                         targetResponseConnector.toString()),
                    node);
            handler.setLogEntry(logEntry);
            TaskCreator<Void> taskCreator = new TaskCreator<Void>(successCallable);
            taskCreator.setDescriptor(
                    new Descriptor<TaskCreator<Void>>() {
                        @Override
                        public String describe(
                                TaskCreator<Void> creator) {
                            return "Отправка запроса: " + targetResponseConnector
                                    .toString();
                        }
                    });
            scheduler.schedule(taskCreator, handler);
        }
    }
}
