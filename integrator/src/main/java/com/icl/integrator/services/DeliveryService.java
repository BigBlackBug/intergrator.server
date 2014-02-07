package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.model.*;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandlerFactory;
import com.icl.integrator.util.IntegratorException;
import com.icl.integrator.util.connectors.EndpointConnector;
import com.icl.integrator.util.connectors.EndpointConnectorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

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
//		EndpointConnector sourceConnector;
//		DestinationDescriptor.DescriptorType descriptorType =
//				sourceService.getDescriptorType();
//		if (descriptorType == DestinationDescriptor.DescriptorType.RAW) {
//			RawDestinationDescriptor realSourceService =
//					(RawDestinationDescriptor) sourceService;
//			logger.info("Scheduling a request to service " +
//					            "defined as source -> " +
//					            realSourceService.getActionDescriptor());
//			sourceConnector = factory.createEndpointConnector(
//					realSourceService.getEndpoint(),
//					realSourceService.getActionDescriptor());
//		} else if (descriptorType ==
//				DestinationDescriptor.DescriptorType.SERVICE) {
//			ServiceDestinationDescriptor realSourceService =
//					(ServiceDestinationDescriptor) sourceService;
//			sourceConnector = factory.createEndpointConnector(
//					realSourceService.getServiceName(),
//					realSourceService.getEndpointType(),
//					realSourceService.getActionName());
//		} else {
//			throw new IntegratorException("У DestinationDescriptor'а неверно " +
//					                              "проставлен тип");
//		}

//		DeliveryCallable<ResponseFromIntegratorDTO<T>>
//				deliveryCallable =
//				new DeliveryCallable<ResponseFromIntegratorDTO<T>, Object>(
//						sourceConnector,
//						new ResponseFromIntegratorDTO<>(packet));
		//TODO доделать.
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

	private <T> UUID executeDelivery(
			final DeliveryCallable<T, ResponseDTO> deliveryCallable,
			ResponseDeliveryInfo responseDeliveryInfo,
			Delivery delivery) {
		UUID requestID = UUID.randomUUID();
		logger.info("Generated an ID for the request: " + quote(
				requestID.toString()));
		TaskCreator<ResponseDTO> deliveryTaskCreator =
				new TaskCreator<>(deliveryCallable);
//		deliveryTaskCreator.setDescriptor(
//				new Descriptor<TaskCreator<ResponseDTO>>() {
//					@Override
//					public String describe(
//							TaskCreator<ResponseDTO> creator) {
//						return "Отправка запроса: " +
//								deliveryCallable.getConnector().toString();
//					}
//				});
		SourceDeliveryCallable<Void> deliveryFailed = null;
		SourceDeliveryCallable<ResponseDTO> deliverySuccess = null;
		if (responseDeliveryInfo != null) {
			deliveryFailed = new SourceDeliveryCallable<>(
					responseDeliveryInfo, delivery);
			deliverySuccess = new SourceDeliveryCallable<>(
					responseDeliveryInfo, delivery);
		}
		deliveryTaskCreator.setCallback(
				new DeliverySuccessCallback(delivery, requestID,
				                            deliverySuccess));

		//статус деливери failed ставим в шедулере
		//TODO register with default
		scheduler.schedule(new Schedulable<>(deliveryTaskCreator,
		                                     delivery,
		                                     DeliverySettings.createDefaultSettings()),
		                   deliveryFailed);
		return requestID;
	}

	public UUID deliver(Delivery delivery,
	                    ResponseDeliveryInfo responseDeliveryInfo) {
		//detached
		AbstractEndpointEntity endpoint = delivery.getEndpoint();
		AbstractActionEntity action = delivery.getAction();

		logger.info("Scheduling a request to target " +
				            endpoint.getServiceName());
		EndpointConnector destinationConnector =
				factory.createEndpointConnector(endpoint, action);
		DeliveryCallable<String, ResponseDTO> deliveryCallable =
				new DeliveryCallable<>(destinationConnector,
				                       delivery.getDeliveryPacket()
						                       .getDeliveryData(),
				                       ResponseDTO.class);

		return executeDelivery(deliveryCallable, responseDeliveryInfo,
		                       delivery);
		//тут пакет нужен для ретрайхендлера.
		//при обломе доставки N раз пишет в базу
//        executeDelivery(deliveryCallable, endpoint, delivery.getDeliveryPacket(),
//                        requestID);
	}

	private class DeliverySuccessCallback implements Callback<ResponseDTO> {

		private final Delivery delivery;

		private final UUID requestID;

		private Callback<ResponseDTO> afterExecution;

		public DeliverySuccessCallback(Delivery delivery, UUID requestID,
		                               Callback<ResponseDTO> afterExecution) {
			this.delivery = delivery;
			this.afterExecution = afterExecution;
			this.requestID = requestID;
		}

		public DeliverySuccessCallback(Delivery delivery, UUID requestID) {
			this.delivery = delivery;
			this.requestID = requestID;
		}

		@Override
		public void execute(ResponseDTO responseDTO) {
			String responseString;
			try {
				responseString = mapper.writeValueAsString(responseDTO);
			} catch (JsonProcessingException e) {
				responseString = "Unable to serialize response from " +
						"target";
			}
			delivery.setDeliveryStatus(DeliveryStatus.DELIVERY_OK);
			delivery.setResponseData(responseString);
			delivery.setResponseDate(new Date());
			persistenceService.merge(delivery);
			if (afterExecution != null) {
				ResponseDTO<ResponseFromTargetDTO>
						data =
						new ResponseDTO<>(new ResponseFromTargetDTO(
								responseDTO,
								delivery.getEndpoint().getServiceName(),
								requestID.toString()));
				afterExecution.execute(data);
			}
		}
	}

	private class SourceDeliveryCallable<T> implements Callback<T> {

		private final ResponseDeliveryInfo responseDeliveryInfo;

		private final Delivery delivery;

		private Log logger = LogFactory.getLog(SourceDeliveryCallable.class);

		public SourceDeliveryCallable(
				ResponseDeliveryInfo responseDeliveryInfo,
				Delivery delivery) {
			this.responseDeliveryInfo = responseDeliveryInfo;
			this.delivery = delivery;
		}

		@Override
		public void execute(T responseToSource) {
			logger.info("Sending response to the source from " +
					            delivery.getEndpoint()
							            .getServiceName());

			AbstractEndpointEntity sourceService =
					responseDeliveryInfo.getSourceService();
			AbstractActionEntity sourceAction =
					responseDeliveryInfo.getSourceAction();
			final EndpointConnector sourceConnector =
					factory.createEndpointConnector(
							sourceService,
							sourceAction);

			Delivery sourceDelivery = new Delivery();
			sourceDelivery.setAction(sourceAction);
			sourceDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
			sourceDelivery.setEndpoint(sourceService);
			persistenceService.merge(sourceDelivery);

			//в случае успеха
			//TODO поменять в интерфейсе
//			ResponseDTO<ResponseFromTargetDTO>
//					data =
//					new ResponseDTO<>(new ResponseFromTargetDTO(
//							responseToSource,
//							delivery.getEndpoint().getServiceName(),
//							requestID.toString()));

			DeliveryCallable<T, Void>
					successCallable =
					new DeliveryCallable<>(sourceConnector, responseToSource,
					                       Void.class);

//				ObjectNode node = mapper.valueToTree(responseFromTarget);
//				String message = "Не смогли вернуть запрос " +
//						"на источник по адресу {0}";
//				DatabaseRetryHandler handler =
//						databaseRetryHandlerFactory.createHandler();
//				TaskLogEntry logEntry = new TaskLogEntry(
//						MessageFormat.format(message,
//						                     sourceConnector.toString()),
//						node);
//				handler.setLogEntry(logEntry);
			TaskCreator<Void> deliveryToSource =
					new TaskCreator<>(successCallable);
//				deliveryToSource
//						.setDescriptor(new Descriptor<TaskCreator<Void>>() {
//							@Override
//							public String describe(TaskCreator<Void> object) {
//								return "Отправка запроса: " +
//										sourceConnector
//												.toString();
//							}
//						});
			deliveryToSource.setCallback(new DeliveryStatusSetter(delivery,
			                                                      DeliveryStatus.DELIVERY_OK));

			scheduler.schedule(
					new Schedulable<>(deliveryToSource, delivery, null/*TODO default*/),
					new DeliveryStatusSetter(delivery,
					                         DeliveryStatus.DELIVERY_FAILED));
		}
	}

	private class DeliveryStatusSetter implements Callback<Void> {

		private final DeliveryStatus deliveryStatus;

		private final Delivery delivery;

		private DeliveryStatusSetter(Delivery delivery,
		                             DeliveryStatus deliveryStatus) {
			this.deliveryStatus = deliveryStatus;
			this.delivery = delivery;
		}

		@Override
		public void execute(Void arg) {
			delivery.setDeliveryStatus(deliveryStatus);
			persistenceService.merge(delivery);
		}
	}

}
