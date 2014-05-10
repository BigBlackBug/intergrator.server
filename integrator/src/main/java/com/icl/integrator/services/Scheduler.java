package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.model.Delivery;
import com.icl.integrator.model.DeliverySettings;
import com.icl.integrator.model.DeliveryStatus;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.util.connectors.ConnectionException;
import com.icl.integrator.util.connectors.EndpointConnectorExceptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.icl.integrator.util.ExceptionUtils.getStackTraceAsString;

/**
 * Created by bigblackbug on 2/5/14.
 */
@Service
public class Scheduler {

    private static final int THREAD_CORE_POOL_SIZE = 15;

    private static final ScheduledExecutorService EXECUTOR = Executors
            .newScheduledThreadPool(THREAD_CORE_POOL_SIZE);

	private static final SimpleDateFormat DATE_FORMAT =
			new SimpleDateFormat("dd.MM.yyyy HH:mm:ss::SSS");

    private final static Log logger = LogFactory.getLog(Scheduler.class);

    private Map<Long, Integer> scheduleMap = new HashMap<>();

    @Autowired
    private PersistenceService persistenceService;

    private <T> void schedule(final TaskCreator<T> taskCreator, Delivery delivery) {
        scheduleMap.put(taskCreator.getTaskID(), 0);
	    final Callback<T> odlCallback = taskCreator.getCallback();
	    Callback<T> newCallback = new Callback<T>() {
		    @Override
		    public void execute(T arg) {
			    scheduleMap.remove(taskCreator.getTaskID());
			    odlCallback.execute(arg);
		    }
	    };
	    taskCreator.setCallback(newCallback);
        delivery.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);
        persistenceService.merge(delivery);
        EXECUTOR.submit(taskCreator.create());
    }

    public <T> void scheduleGeneral(final Schedulable<T> deliverySchedulable,
                                    final Callback<Void> retryLimitHandler) {
        final TaskCreator<T> taskCreator = deliverySchedulable.getTaskCreator();
        final Delivery delivery = deliverySchedulable.getDelivery();
        //не прошёл пинг
        taskCreator.addExceptionHandler(
		        new ExceptionHandlerGeneral<T, ConnectionException>(
				        deliverySchedulable, retryLimitHandler),
		        ConnectionException.class);

        taskCreator.addExceptionHandler(
                new ExceptionHandlerGeneral<T, EndpointConnectorExceptions.JMSConnectorException>
                        (deliverySchedulable, retryLimitHandler),
                EndpointConnectorExceptions.JMSConnectorException.class);

        taskCreator.addExceptionHandler(
                new ExceptionHandlerGeneral<T, EndpointConnectorExceptions.HttpConnectorException>
                        (deliverySchedulable, retryLimitHandler),
                EndpointConnectorExceptions.HttpConnectorException.class);
        schedule(taskCreator, delivery);
    }

    public <T> void scheduleDelivery(final Schedulable<T> deliverySchedulable,
                                     final Callback<ErrorDTO> retryLimitHandler) {
        final TaskCreator<T> taskCreator = deliverySchedulable.getTaskCreator();
        final Delivery delivery = deliverySchedulable.getDelivery();
        //не прошёл пинг
        taskCreator.addExceptionHandler(new ExceptionHandlerError<T,
		        ConnectionException>(deliverySchedulable,
                                     retryLimitHandler),
                                        ConnectionException.class);

	    taskCreator.addExceptionHandler(new ExceptionHandlerError<T,
			    EndpointConnectorExceptions.JMSConnectorException>
			                                    (deliverySchedulable,
			                                     retryLimitHandler),
	                                    EndpointConnectorExceptions.JMSConnectorException.class);

	    taskCreator.addExceptionHandler(new ExceptionHandlerError<T,
			    EndpointConnectorExceptions.HttpConnectorException>
			                                    (deliverySchedulable,
			                                     retryLimitHandler),
	                                    EndpointConnectorExceptions.HttpConnectorException.class);
        schedule(taskCreator, delivery);
    }

    private class ExceptionHandlerGeneral<T, E extends Exception> extends
            ExceptionHandler<T, Void, E> {

        public ExceptionHandlerGeneral(Schedulable<T> deliverySchedulable,
                Callback<Void> retryLimitHandler) {
            super(deliverySchedulable);
	        if(retryLimitHandler!=null){
                setCallback(new CallbackGeneral(retryLimitHandler));
	        }
        }

        protected class CallbackGeneral implements Callback<E> {

	        private Callback <Void> callback;

	        public CallbackGeneral(Callback<Void> callback) {
		        this.callback = callback;
	        }
            @Override
            public void execute(E arg) {
	            callback.execute(null);
            }
        }
    }

	private class ExceptionHandlerError<T, E extends Exception> extends
			ExceptionHandler<T, ErrorDTO, E> {

		public ExceptionHandlerError(Schedulable<T> deliverySchedulable,
		                             Callback<ErrorDTO> retryLimitHandler) {
			super(deliverySchedulable);
			if (retryLimitHandler != null) {
				setCallback(new CallbackError(retryLimitHandler));
			}
		}

		protected class CallbackError implements Callback<E> {

	        private Callback <ErrorDTO> callback;

	        public CallbackError(Callback<ErrorDTO> callback) {
		        this.callback = callback;
	        }

	        @Override
            public void execute(E exception) {
		        String developerMessage = "Последнее исключение - \n" +
				        getStackTraceAsString(exception);
		        String errorMessage = "Не могу доставить запрос на таргет";
		        ErrorDTO errorDTO = new ErrorDTO(errorMessage, developerMessage);
		        callback.execute(errorDTO);
            }
        }
    }

    private abstract class ExceptionHandler<T, Y, E extends Exception>
            implements Callback<E> {

        protected final TaskCreator<T> taskCreator;

        protected final Delivery delivery;

        protected final DeliverySettings deliverySettings;

        private Callback<E> callback;

        protected ExceptionHandler(Schedulable<T> deliverySchedulable) {
            taskCreator = deliverySchedulable.getTaskCreator();
            delivery = deliverySchedulable.getDelivery();
            deliverySettings = deliverySchedulable.getDeliverySettings();
        }

        protected void setCallback(Callback<E> callback) {
            this.callback = callback;
        }

        @Override
        public void execute(E exception) {
            logger.info("Exception when sending request", exception);
            int retryIndex = scheduleMap.get(taskCreator.getTaskID());
            if (retryIndex == deliverySettings.getRetryNumber()) {
                logger.info("Too many retries to handle exception",
                            exception);
                scheduleMap.remove(taskCreator.getTaskID());
                delivery.setDeliveryStatus(DeliveryStatus.DELIVERY_FAILED);
	            delivery.setLastFailureReason(getStackTraceAsString(exception));
                persistenceService.merge(delivery);
                //обычно посылатель домой
                //но также мб просто сменщик статуса
                //например когда мы шедулим реквест на исходник
                //обрабочик облома
                if (callback != null) {
                    callback.execute(exception);
                }
	            return;
            }

            Date nextRequestDate = new Date(System.currentTimeMillis() +
                                                    deliverySettings
                                                            .getRetryDelay());
            logger.info("Rescheduling next request to " +
                                DATE_FORMAT.format(nextRequestDate));
            EXECUTOR.schedule(taskCreator.create(),
                              deliverySettings.getRetryDelay(),
                              TimeUnit.MILLISECONDS);
            scheduleMap.put(taskCreator.getTaskID(),
                            retryIndex + 1);
        }

    }

}

