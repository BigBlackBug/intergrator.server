package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromIntegratorDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.model.*;
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
    protected Scheduler scheduler;

    @Autowired
    private EndpointConnectorFactory factory;

    @Autowired
    private DatabaseRetryHandlerFactory databaseRetryHandlerFactory;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PersistenceService persistenceService;

    public <T> void deliverGeneral(DestinationDescriptor sourceService,
                                   T packet)
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
        } else if (descriptorType ==
                DestinationDescriptor.DescriptorType.SERVICE) {
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
                new DeliveryCallable<>(sourceConnector,
                                       new ResponseFromIntegratorDTO<>(packet));
        //TODO доделать. также вынести в отдельный класс 2 колбека для
        // респонзфейлд
//        scheduler.schedule(new TaskCreator<>(deliveryCallable));
    }

    //	public UUID deliver(DestinationDTO destination,
//	                    DeliveryDTO packet) throws IntegratorException {
//		logger.info("Scheduling a request to target " +
//				            destination.getServiceName());
//		EndpointConnector destinationConnector =
//				factory.createEndpointConnector(destination,
//				                                packet.getAction());
//		DeliveryCallable<RequestDataDTO> deliveryCallable =
//				new DeliveryCallable<>(destinationConnector,
//				                       packet.getRequestData());
//		RawDestinationDescriptor targetResponseHandler =
//				packet.getTargetResponseHandlerDescriptor();
//		if (targetResponseHandler != null) {
//			EndpointDTO endpoint = targetResponseHandler.getEndpoint();
//			ActionDescriptor actionDescriptor = targetResponseHandler
//					.getActionDescriptor();
//			EndpointConnector targetResponseConnector = factory
//					.createEndpointConnector(endpoint, actionDescriptor);
//			return deliver(deliveryCallable, targetResponseConnector,
//			               destination);
//		}
//		return deliver(deliveryCallable, destination, packet);
//	}
//    private <T> void executeDelivery(final DeliveryCallable<T>
//                                             deliveryCallable,
//                                     AbstractEndpointEntity destinationDTO,
//                                     DeliveryPacket packet, UUID requestID) {
//        DatabaseRetryHandler handler =
//                createRetryHandler(packet, destinationDTO, requestID);
//
//        TaskCreator<ResponseDTO> deliveryTaskCreator =
//                new TaskCreator<>(deliveryCallable);
//        deliveryTaskCreator.setDescriptor(
//                new Descriptor<TaskCreator<ResponseDTO>>() {
//                    @Override
//                    public String describe(
//                            TaskCreator<ResponseDTO> creator) {
//                        return "Отправка запроса: " +
//                                deliveryCallable.getConnector().toString();
//                    }
//                });
//        if (destinationDTO.scheduleRedelivery()) {
//            scheduler.schedule(deliveryTaskCreator, handler);
//        } else {
//            scheduler.schedule(deliveryTaskCreator);
//        }
//    }

//    private DatabaseRetryHandler createRetryHandler(
//            DeliveryPacket packet, DestinationDTO destinationDTO,
//            UUID requestID) {
//        DatabaseRetryHandler handler =
//                databaseRetryHandlerFactory.createHandler();
//        TaskLogEntry logEntry =
//                createTaskLogEntry(packet.getDeliveryData(), destinationDTO,
//                                   requestID);
//        handler.setLogEntry(logEntry);
//        return handler;
//    }

//    private TaskLogEntry createTaskLogEntry(String packet,
//                                            DestinationDTO destinationDTO,
//                                            UUID requestID) {
//        TaskLogEntry logEntry = new TaskLogEntry();
//        String generalMessage = "Не могу доставить запрос {0} " +
//                "на сервис {1}";
//        String targetServiceName = destinationDTO.getServiceName();
//        generalMessage = MessageFormat.format(generalMessage, requestID,
//                                              targetServiceName);
//        logEntry.setMessage(generalMessage);
//
//        String dataJson = null;
//        try {
//            dataJson = serializer.writeValueAsString(packet);
//        } catch (JsonProcessingException e) {
//            logEntry.setAdditionalMessage(e.getMessage());
//            logger.info("Unable to serialize incoming json data");
//        }
//        logEntry.setDataJson(dataJson);
//        return logEntry;
//    }

    private <T> UUID executeDelivery(final DeliveryCallable<T> deliveryCallable,
                                     AbstractEndpointEntity sourceService,
                                     AbstractActionEntity sourceAction,
                                     Delivery delivery) {
        UUID requestID = UUID.randomUUID();
        logger.info("Generated an ID for the request: " + quote(
                requestID.toString()));
        //сюда летит ответ
        EndpointConnector sourceConnector = null;
        if (sourceService != null && sourceAction != null) {
            sourceConnector = factory
                    .createEndpointConnector(sourceService, sourceAction);
        }
        TaskCreator<ResponseDTO> deliveryTaskCreator =
                new TaskCreator<>(deliveryCallable);
        deliveryTaskCreator.setDescriptor(
                new Descriptor<TaskCreator<ResponseDTO>>() {
                    @Override
                    public String describe(
                            TaskCreator<ResponseDTO> creator) {
                        return "Отправка запроса: " +
                                deliveryCallable.getConnector().toString();
                    }
                });
        deliveryTaskCreator.setCallback(new DeliverySuccessCallback(
                sourceService, sourceAction,
                sourceConnector, delivery,
                requestID));

        if (delivery.scheduleRedelivery()) {
            //статус деливери ставим в шедулере
            //TODO переписать на манер саксесс
            Callable<Void> retryLimitExceeded = new RetryLimitExceededCallable(
                    sourceConnector, delivery.getEndpoint(), requestID);
//            new TaskCreator<>(retryLimitExceeded).setCallback(new Callback<Void>() {
//                @Override
//                public void execute(Void arg) {
//
//                }
//            });
            //TODO retrylimit handler
            scheduler.schedule(new Schedulable<>(deliveryTaskCreator,delivery));
        } else {
            scheduler.schedule(new Schedulable<>(deliveryTaskCreator,
                                                 delivery,
                                                 new DeliverySettings(5000L,
                                                                      10)));
        }
        return requestID;
    }

    public UUID deliver(Delivery delivery,
                        AbstractEndpointEntity sourceService,
                        AbstractActionEntity sourceAction) {
        //detached
        AbstractEndpointEntity endpoint = delivery.getEndpoint();
        AbstractActionEntity action = delivery.getAction();

        logger.info("Scheduling a request to target " +
                            endpoint.getServiceName());
        EndpointConnector destinationConnector =
                factory.createEndpointConnector(endpoint, action);
        DeliveryCallable<String> deliveryCallable =
                new DeliveryCallable<>(destinationConnector,
                                       delivery.getDeliveryPacket()
                                               .getDeliveryData());

        return executeDelivery(deliveryCallable, sourceService, sourceAction,
                         delivery);
        //тут пакет нужен для ретрайхендлера.
        //при обломе доставки N раз пишет в базу
//        executeDelivery(deliveryCallable, endpoint, delivery.getDeliveryPacket(),
//                        requestID);
    }

    private class RetryLimitExceededCallable implements Callable<Void> {

        private final AbstractEndpointEntity targetDestination;

        private final UUID requestID;

        private final EndpointConnector sourceConnector;

        private RetryLimitExceededCallable(EndpointConnector sourceConnector,
                                           AbstractEndpointEntity targetDestination,
                                           UUID requestID) {
            this.sourceConnector = sourceConnector;
            this.targetDestination = targetDestination;
            this.requestID = requestID;
        }

        @Override
        public Void call() throws Exception {
            String generalMessage = "Не могу доставить запрос {0} " +
                    "на сервис {1}";
            String targetServiceName = targetDestination.getServiceName();
            ErrorDTO errorDTO = new ErrorDTO(MessageFormat.format
                    (generalMessage, requestID, targetServiceName));
            ResponseDTO<Object> responseFromTarget =
                    new ResponseDTO<>(errorDTO);
            ResponseFromTargetDTO requestData = new ResponseFromTargetDTO(
                    responseFromTarget,
                    targetServiceName,
                    requestID.toString());
            /*we're not expecting any response I guess*/
            sourceConnector.sendRequest(requestData, ResponseDTO.class);
            return null;
        }
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
                                        delivery.getEndpoint().getServiceName(),
                                        requestID.toString());
                        sourceConnector
                                .sendRequest(data, ResponseDTO.class);
                        return null;
                    }
                };

        private final Delivery delivery;

        private final UUID requestID;

        private final EndpointConnector sourceConnector;

        private final Delivery sourceDelivery;

        //  получаем после выполнения метода
        private ResponseDTO responseDTO;

        public DeliverySuccessCallback(AbstractEndpointEntity sourceService,
                                       AbstractActionEntity sourceAction,
                                       EndpointConnector sourceConnector,
                                       Delivery delivery, UUID requestID) {
            this.delivery = delivery;
            this.sourceDelivery = new Delivery();
            sourceDelivery.setAction(sourceAction);
            sourceDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
            sourceDelivery.setEndpoint(sourceService);
            sourceDelivery.setScheduleRedelivery(true);
            this.sourceConnector = sourceConnector;
            this.requestID = requestID;
        }

        @Override
        public void execute(ResponseDTO responseDTO) {
            delivery.setDeliveryStatus(DeliveryStatus.DELIVERY_OK);
            String responseString;
            try {
                responseString = mapper.writeValueAsString(responseDTO);
            } catch (JsonProcessingException e) {
                responseString = "Unable to serialize response from " +
                        "target";
            }
            delivery.setResponseData(responseString);
            persistenceService.merge(delivery);
            logger.info("Sending response to the source from " +
                                delivery.getEndpoint()
                                        .getServiceName());
            this.responseDTO = responseDTO;
            if (sourceConnector != null) {
                sendResponseBackToSource();
            }
        }

        private void sendResponseBackToSource() {
            persistenceService.merge(sourceDelivery);
            ObjectNode node = mapper.valueToTree(responseDTO);
            String message = "Не смогли вернуть запрос " +
                    "на источник по адресу {0}";
            DatabaseRetryHandler handler =
                    databaseRetryHandlerFactory.createHandler();
            TaskLogEntry logEntry = new TaskLogEntry(
                    MessageFormat.format(message,
                                         sourceConnector.toString()),
                    node);
            handler.setLogEntry(logEntry);
            TaskCreator<Void> deliveryToSource = new TaskCreator<>(successCallable);
            deliveryToSource.setDescriptor(
                    new Descriptor<TaskCreator<Void>>() {
                        @Override
                        public String describe(
                                TaskCreator<Void> creator) {
                            return "Отправка запроса: " +
                                    sourceConnector
                                            .toString();
                        }
                    });
            //TODO onsuccess, onfailure delete action/endpoint
//            scheduler.schedule(deliveryToSource, handler);
        }
    }
}
