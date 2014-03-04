package com.icl.integrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.model.AbstractActionEntity;
import com.icl.integrator.model.AbstractEndpointEntity;
import com.icl.integrator.model.Delivery;
import com.icl.integrator.model.DeliveryStatus;
import com.icl.integrator.services.converters.Converter;
import com.icl.integrator.services.utils.PersistentDestination;
import com.icl.integrator.services.utils.ResponseDeliveryDescriptor;
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
	private DeliveryCreator deliveryCreator;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PersistenceService persistenceService;

	//TODO rename endpoint to service
	public <T> void deliver(DestinationDescriptor destinationDescriptor,T packet) {
		if (destinationDescriptor != null) {
			Delivery delivery;
			try {
				PersistentDestination destination = deliveryCreator
						.persistDestination(destinationDescriptor);
				AbstractActionEntity action = destination.getAction();
				AbstractEndpointEntity service = destination.getService();
				delivery = deliveryCreator.createDelivery(service,action, packet);
			} catch (Exception ex) {
				logger.error("Ошибка создания конечной точки доставки", ex);
				return;
			}
			try {
				deliver(delivery,Void.class);
			} catch (Exception ex) {
				logger.error("Не могу отправить пакет на сервис", ex);
				return;
			}
		}
	}

	private <PacketClass,ResponseClass,ТипКоторыйПринимаетСорс> void executeDelivery(
			final DeliveryCallable<PacketClass, ResponseClass> deliveryCallable,
			final Delivery delivery,
			ResponseDeliveryDescriptor<ResponseClass,ТипКоторыйПринимаетСорс> deliveryDescriptor) {
		TaskCreator<ResponseClass> deliveryTaskCreator =
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
		Callback<ResponseClass> deliverySuccess = null;
		if (deliveryDescriptor != null) {
			String targetServiceName = delivery.getEndpoint().getServiceName();
			PersistentDestination persistentDestination =
					deliveryDescriptor.getPersistentDestination();
			Converter<ErrorDTO, ТипКоторыйПринимаетСорс> failedConverter =
					deliveryDescriptor.getFailedConverter();
			Converter<ResponseClass, ТипКоторыйПринимаетСорс> successConverter =
					deliveryDescriptor.getSuccessConverter();
			deliveryFailed = new SourceDeliveryFailedCallback<>(delivery.getId(),
			                                                  persistentDestination,
			                                                  targetServiceName, failedConverter);
			deliverySuccess = new SourceDeliverySuccessCallback<>(delivery.getId(),
			                                                    persistentDestination,
			                                                    targetServiceName, successConverter);
		}/*else{
            deliveryFailed = new Callback<ErrorDTO>() {
                @Override
                public void execute(ErrorDTO arg) {
                delivery.setDeliveryStatus(DeliveryStatus.DELIVERY_FAILED);
                persistenceService.merge(delivery);
                }
            };
        }  */
		//если войд, то всё нулл
		//если не войд, то конвертим
		//сюда надопереадавать продбюсер
//		deliveryTaskCreator.setCallback(new DeliverySuccessCallback(
//				delivery, requestID, deliverySuccess));
		deliveryTaskCreator.setCallback(new DeliverySuccessCallback<>(delivery,deliverySuccess));

		//статус деливери failed ставим в шедулере
        //тут шедуль принимающий ответ
		scheduler.scheduleDelivery(new Schedulable<>(deliveryTaskCreator,
		                                             delivery),
		                           deliveryFailed);
	}

	public <T> void deliver(Delivery delivery,Class<T> responseClass) {
		deliver(delivery, responseClass,null);
	}


	public <ResponseClass,ТипКПС> void deliver(Delivery delivery,
	                    Class<ResponseClass> responseClass,
	                    ResponseDeliveryDescriptor<ResponseClass,ТипКПС>
			responseDeliveryDescriptor) {
		//detached
		AbstractEndpointEntity endpoint = delivery.getEndpoint();
		AbstractActionEntity action = delivery.getAction();

		logger.info("Scheduling a request to target " +
				            endpoint.getServiceName());
		EndpointConnector destinationConnector =
				factory.createEndpointConnector(endpoint, action);
		DeliveryCallable<String, ResponseClass> deliveryCallable =
				new DeliveryCallable<>(destinationConnector,delivery.getDeliveryData(),
				                       responseClass);

		//если воед, то оставляем как есть. new Error
		executeDelivery(deliveryCallable,delivery,responseDeliveryDescriptor);
	}

	private class DeliverySuccessCallback<T> implements Callback<T> {

		private final Delivery delivery;

//		private final UUID requestID;

		private Callback<T> afterExecution;

		public DeliverySuccessCallback(Delivery delivery/*, UUID requestID*/,
		                               Callback<T> afterExecution) {
			this.delivery = delivery;
			this.afterExecution = afterExecution;
//			this.requestID = requestID;
		}

		public DeliverySuccessCallback(Delivery delivery/*, UUID requestID*/) {
			this.delivery = delivery;
//			this.requestID = requestID;
		}

		@Override
		public void execute(T responseDTO) {
			String responseString = null;
			try {
				if (responseDTO != null) {
					responseString = mapper.writeValueAsString(responseDTO);
				}
			} catch (JsonProcessingException e) {
				responseString = "Unable to serialize response from target";
			}
			delivery.setDeliveryStatus(DeliveryStatus.DELIVERY_OK);
			delivery.setResponseData(responseString);
			delivery.setResponseDate(new Date());
			persistenceService.merge(delivery);
			if (afterExecution != null) {
//				ResponseDTO<ResponseFromTargetDTO>
//						data =
//						new ResponseDTO<>(new ResponseFromTargetDTO(
//								responseDTO,
//								delivery.getEndpoint().getServiceName(),
//								requestID.toString()));
				afterExecution.execute(responseDTO);
			}
		}
	}

    private abstract class SourceDeliveryCallback<T,ResponseClass> implements Callback<T>{
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

//	    @Transactional
        protected void deliver(ResponseClass data){
            logger.info("Sending response to the source from " +
		                        targetServiceName);

            AbstractEndpointEntity service =
                    persistentDestination.getService();
            AbstractActionEntity action =
                    persistentDestination.getAction();
            final EndpointConnector sourceConnector =
                    factory.createEndpointConnector(service,action);
	        Delivery sourceDelivery;
	        try {
		        sourceDelivery =
				        deliveryCreator.createDelivery(service, action, data);
	        } catch (JsonProcessingException e) {
		        //will never happen
		        return;
	        }
            DeliveryCallable<ResponseClass, Void>
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

	        scheduler.scheduleGeneral(
			        new Schedulable<>(deliveryToSource, sourceDelivery), null);
        }
    }

	private class SourceDeliverySuccessCallback<TargetResponseClass,ResponseClass> extends
            SourceDeliveryCallback<TargetResponseClass,ResponseClass> {

		private final Converter<TargetResponseClass,ResponseClass> converter;

		public SourceDeliverySuccessCallback(UUID requestID,
                                             PersistentDestination persistentDestination,
                                             String targetServiceName,Converter<TargetResponseClass,ResponseClass> converter) {
            super(requestID, persistentDestination, targetServiceName);
	        this.converter = converter;
        }

		@Override
		public void execute(TargetResponseClass responseToSource) {
            deliver(converter.convert(responseToSource));
		}
	}

    private class SourceDeliveryFailedCallback<ResponseClass> extends
            SourceDeliveryCallback<ErrorDTO,ResponseClass> {

        public SourceDeliveryFailedCallback(UUID requestID,
                                            PersistentDestination persistentDestination,
                                            String targetServiceName,
                                            Converter<ErrorDTO,ResponseClass> converter) {
            super(requestID, persistentDestination, targetServiceName);
	        this.converter = converter;
        }
	    private final Converter<ErrorDTO,ResponseClass> converter;

        @Override
        public void execute(ErrorDTO errorDTO) {
//            deliver(new ResponseDTO<>(errorDTO));
	        deliver(converter.convert(errorDTO));
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
