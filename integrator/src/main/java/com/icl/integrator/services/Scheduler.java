package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.model.Delivery;
import com.icl.integrator.model.DeliverySettings;
import com.icl.integrator.model.DeliveryStatus;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.util.Utils;
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

/**
 * Created by bigblackbug on 2/5/14.
 */
@Service
public class Scheduler {

    private static final int THREAD_CORE_POOL_SIZE = 15;

    private static final ScheduledExecutorService EXECUTOR = Executors
            .newScheduledThreadPool(THREAD_CORE_POOL_SIZE);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat
            ("dd.MM.yyyy HH:mm:ss::SSS");

    private final static Log logger = LogFactory.getLog(
            Scheduler.class);

    private Map<Long, Integer> scheduleMap = new HashMap<>();

    @Autowired
    private PersistenceService persistenceService;

    private <T> void schedule(final TaskCreator<T> taskCreator, Delivery delivery) {
        scheduleMap.put(taskCreator.getTaskID(), 0);
        taskCreator.setCallback(new Callback<T>() {
            @Override
            public void execute(T arg) {
                scheduleMap.remove(taskCreator.getTaskID());
                Callback<T> callback = taskCreator.getCallback();
                callback.execute(arg);
            }
        });
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
                new ExceptionHandlerGeneral<T, ConnectionException>
                        (deliverySchedulable, retryLimitHandler),
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
                ConnectionException>
                                                (deliverySchedulable,
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

        public ExceptionHandlerGeneral(
                Schedulable<T> deliverySchedulable,
                Callback<Void> retryLimitHandler) {
            super(deliverySchedulable, retryLimitHandler);
            setCallback(new CallbackGeneral());
        }

        protected class CallbackGeneral implements Callback<E> {

            @Override
            public void execute(E arg) {
                retryLimitHandler.execute(null);
            }
        }
    }

    private class ExceptionHandlerError<T, E extends Exception> extends
            ExceptionHandler<T, ErrorDTO, E> {

        public ExceptionHandlerError(Schedulable<T> deliverySchedulable,
                                     Callback<ErrorDTO> retryLimitHandler) {
            super(deliverySchedulable, retryLimitHandler);
            setCallback(new CallbackError());
        }

        protected class CallbackError implements Callback<E> {

            @Override
            public void execute(E exception) {
                ErrorDTO errorDTO = new ErrorDTO();
                errorDTO.setErrorMessage("Не могу доставить запрос на таргет");
                errorDTO.setDeveloperMessage("Последнее исключение - \n" +
                                                     Utils.getStackTraceAsString(
                                                             exception));
//                delivery - таргет деливери
                retryLimitHandler.execute(errorDTO);
            }
        }
    }

    private abstract class ExceptionHandler<T, Y, E extends Exception>
            implements Callback<E> {

        protected final TaskCreator<T> taskCreator;

        protected final Delivery delivery;

        protected final DeliverySettings deliverySettings;

        protected final Callback<Y> retryLimitHandler;

        private Callback<E> callback;

        protected ExceptionHandler(Schedulable<T> deliverySchedulable,
                                   Callback<Y> retryLimitHandler) {
            taskCreator = deliverySchedulable.getTaskCreator();
            this.retryLimitHandler = retryLimitHandler;
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
                persistenceService.merge(delivery);
                //обычно посылатель домой
                //но также мб просто сменщик статуса
                //например когда мы шедулим реквест на исходник
                //обрабочик облома
                if (retryLimitHandler != null) {
                    callback.execute(exception);
                    return;
                }

            }
            Date nextRequestDate = new Date(System.currentTimeMillis() +
                                                    deliverySettings
                                                            .getRetryDelay());
            logger.info("Rescheduling next request to " +
                                DATE_FORMAT.format(nextRequestDate));
            delivery.setDeliveryStatus(DeliveryStatus.WAITING_FOR_DELIVERY);
            persistenceService.merge(delivery);
            EXECUTOR.schedule(taskCreator.create(),
                              deliverySettings.getRetryDelay(),
                              TimeUnit.MILLISECONDS);
            scheduleMap.put(taskCreator.getTaskID(),
                            retryIndex + 1);
        }

    }

}
