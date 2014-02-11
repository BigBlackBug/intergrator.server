package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.model.*;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.TaskCreator;
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
@Service
public class DeliveryService {

	private static Log logger = LogFactory.getLog(DeliveryService.class);

	@Autowired
	protected Scheduler scheduler;

	@Autowired
	private EndpointConnectorFactory factory;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PersistenceService persistenceService;

	private <T> UUID executeDelivery(
			final DeliveryCallable<T, ResponseDTO> deliveryCallable,
			PersistentDestination persistentDestination,
			final Delivery delivery) {
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
        //accepts real response
		Callback<ErrorDTO> deliveryFailed = null;
        //accepts raw response. must be wrapped
        SourceDeliveryCallback<ResponseDTO> deliverySuccess = null;
		if (persistentDestination != null) {
			String targetServiceName = delivery.getEndpoint().getServiceName();
			deliveryFailed = new SourceDeliveryFailedCallback(requestID,
			                                                  persistentDestination, targetServiceName);
			deliverySuccess = new SourceDeliverySuccessCallback(requestID,
			                                                    persistentDestination, targetServiceName);
		}/*else{
            deliveryFailed = new Callback<ErrorDTO>() {
                @Override
                public void execute(ErrorDTO arg) {
                delivery.setDeliveryStatus(DeliveryStatus.DELIVERY_FAILED);
                persistenceService.merge(delivery);
                }
            };
        }  */
		deliveryTaskCreator.setCallback(new DeliverySuccessCallback(
				delivery, requestID, deliverySuccess));

		//статус деливери failed ставим в шедулере
        //тут шедуль принимающий ответ
		//TODO remove default
		scheduler.scheduleDelivery(new Schedulable<>(deliveryTaskCreator,
		                                     delivery,
		                                     DeliverySettings.createDefaultSettings()),
		                           deliveryFailed);
		return requestID;
	}

	public UUID deliver(Delivery delivery, String data) {
		return deliver(delivery, data, null);
	}

	public UUID deliver(Delivery delivery, String data,
	                    PersistentDestination persistentDestination) {
		//detached
		AbstractEndpointEntity endpoint = delivery.getEndpoint();
		AbstractActionEntity action = delivery.getAction();

		logger.info("Scheduling a request to target " +
				            endpoint.getServiceName());
		EndpointConnector destinationConnector =
				factory.createEndpointConnector(endpoint, action);
		DeliveryCallable<String, ResponseDTO> deliveryCallable =
				new DeliveryCallable<>(destinationConnector,data,
				                       ResponseDTO.class);

		return executeDelivery(deliveryCallable, persistentDestination,
		                       delivery);
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
			String responseString = null;
			try {
				if (responseDTO != null) {
					responseString = mapper.writeValueAsString(responseDTO);
				}
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

    private abstract class SourceDeliveryCallback<T> implements Callback<T>{
        protected final PersistentDestination persistentDestination;

        protected final String targetServiceName;

        protected final UUID requestID;

        private Log logger = LogFactory.getLog(SourceDeliveryCallback.class);

        public SourceDeliveryCallback(UUID requestID,
                                      PersistentDestination persistentDestination,
                                      String targetServiceName) {
            this.requestID = requestID;
            this.persistentDestination = persistentDestination;
            this.targetServiceName = targetServiceName;
        }

        protected void deliver(ResponseDTO data){
            logger.info("Sending response to the source from " +
		                        targetServiceName);

            AbstractEndpointEntity service =
                    persistentDestination.getService();
            AbstractActionEntity action =
                    persistentDestination.getAction();
            final EndpointConnector sourceConnector =
                    factory.createEndpointConnector(
                            service,
                            action);

            Delivery sourceDelivery = new Delivery();
            sourceDelivery.setAction(action);
	        action.addDelivery(sourceDelivery);
            sourceDelivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
            sourceDelivery.setEndpoint(service);
	        service.addDelivery(sourceDelivery);
	        sourceDelivery = persistenceService.merge(sourceDelivery);

            DeliveryCallable<ResponseDTO, Void>
                    successCallable =
                    new DeliveryCallable<>(sourceConnector, data,
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
            deliveryToSource.setCallback(new DeliveryStatusSetter(
		            sourceDelivery, DeliveryStatus.DELIVERY_OK));

            scheduler.scheduleGeneral(    //TODO remove settings
                    new Schedulable<>(deliveryToSource, sourceDelivery,
                                      DeliverySettings.createDefaultSettings()),null);
        }
    }

	//TODO убрать нафиг
	private class SourceDeliverySuccessCallback extends
            SourceDeliveryCallback<ResponseDTO> {

        public SourceDeliverySuccessCallback(UUID requestID,
                                             PersistentDestination persistentDestination,
                                             String targetServiceName) {
            super(requestID, persistentDestination, targetServiceName);
        }

		@Override
		public void execute(ResponseDTO responseToSource) {
            deliver(responseToSource);
		}
	}

    private class SourceDeliveryFailedCallback extends
            SourceDeliveryCallback<ErrorDTO> {

        public SourceDeliveryFailedCallback(UUID requestID,
                                            PersistentDestination persistentDestination,
                                            String targetServiceName) {
            super(requestID, persistentDestination, targetServiceName);
        }

        @Override
        public void execute(ErrorDTO errorDTO) {
            deliver(new ResponseDTO<>(errorDTO));
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
