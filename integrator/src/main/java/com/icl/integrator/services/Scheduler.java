package com.icl.integrator.services;

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

/**
 * Created by bigblackbug on 2/5/14.
 */
@Service
public class Scheduler {

    private static final int THREAD_CORE_POOL_SIZE = 15;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat
            ("dd.MM.yyyy HH:mm:ss::SSS");

    private static final ScheduledExecutorService EXECUTOR = Executors
            .newScheduledThreadPool(THREAD_CORE_POOL_SIZE);

    private final static Log logger = LogFactory.getLog(
            RequestScheduler.class);

    private static final DeliverySettings DEFAULT_DELIVERY_SETTINGS =
            new DeliverySettings();

    private static final int DEFAULT_DELIVERY_RETRY_DELAY_MILLIS = 15000;

    private static final int DEFAULT_DELIVERY_ATTEMPT_NUMBER = 10;

    static {
        DEFAULT_DELIVERY_SETTINGS.setRetryDelay(
                DEFAULT_DELIVERY_RETRY_DELAY_MILLIS);
        DEFAULT_DELIVERY_SETTINGS
                .setRetryNumber(DEFAULT_DELIVERY_ATTEMPT_NUMBER);
    }

    private Map<Long, Integer> scheduleMap = new HashMap<>();

    @Autowired
    private PersistenceService persistenceService;

    public <T> void schedule(final Schedulable<T> deliverySchedulable) {
        schedule(deliverySchedulable, null);
    }

    public <T> void schedule(final Schedulable<T> deliverySchedulable,
                             final Schedulable<T> retryLimitScheduleable) {
        final TaskCreator<T> taskCreator = deliverySchedulable.getTaskCreator();
        final Delivery delivery = deliverySchedulable.getDelivery();
        final DeliverySettings deliverySettings =
                deliverySchedulable.getDeliverySettings();
        scheduleMap.put(taskCreator.getTaskID(), 0);
        taskCreator.addExceptionHandler(new Callback<ConnectionException>() {
            @Override
            public void execute(
                    ConnectionException exception) {
                logger.info("Exception when sending request",
                            exception);
                int retryIndex =
                        scheduleMap.get(taskCreator.getTaskID());
                if (retryIndex == deliverySettings.getRetryNumber()) {
                    logger.info("Too many retries to handle exception",
                                exception);
                    scheduleMap.remove(taskCreator.getTaskID());
                    delivery.setDeliveryStatus(DeliveryStatus.DELIVERY_FAILED);
                    persistenceService.merge(delivery);
                    //обычно посылатель домой
                    if (retryLimitScheduleable != null) {
                        schedule(retryLimitScheduleable, null/*TODO*/);
//                        String originalTaskDescription = taskCreator
//                                .getTaskDescription();
//                        String retryTaskDescription =
//                                onRetryLimitExceeded
//                                        .getTaskDescription();
//                        ObjectMapper mapper = new ObjectMapper();
//                        ObjectNode node = mapper.createObjectNode();
//                        node.put("originalTaskDescription",
//                                 originalTaskDescription);
//                        node.put("retryTaskDescription",
//                                 retryTaskDescription);
//                        DatabaseRetryHandler handler =
//                                retryHandlerFactory.createHandler();
//                        handler.setLogEntry(
//                                new TaskLogEntry(
//                                        "Не получилось выполнить " +
//                                                "таск-обработчик превышения " +
//                                                "допустимого количества " +
//                                                "повторов таска",
//                                        node));
//                        schedule(onRetryLimitExceeded, handler);
                    }
                    return;
                }
                Date nextRequestDate = new Date(
                        System.currentTimeMillis() +
                                deliverySettings.getRetryDelay());
                logger.info(
                        "Rescheduling next request to " +
                                DATE_FORMAT
                                        .format(nextRequestDate));
                delivery.setDeliveryStatus(DeliveryStatus.WAITING_FOR_DELIVERY);
                persistenceService.merge(delivery);
                EXECUTOR.schedule(taskCreator.create(),
                                  deliverySettings.getRetryDelay(),
                                  TimeUnit.MILLISECONDS);
                scheduleMap.put(taskCreator.getTaskID(),
                                retryIndex + 1);
            }
        }, ConnectionException.class);

        taskCreator.addExceptionHandler(
                new Callback<EndpointConnectorExceptions.JMSIntegratorException>() {
                    @Override
                    public void execute(
                            EndpointConnectorExceptions.JMSIntegratorException exception) {
                        Throwable cause = exception.getCause();
                        //ошибка jms лалала
                        //решедулим
                        //hey
                    }
                },
                EndpointConnectorExceptions.JMSIntegratorException.class);

        taskCreator.addExceptionHandler(
                new Callback<EndpointConnectorExceptions
                        .HttpIntegratorException>() {
                    @Override
                    public void execute(
                            EndpointConnectorExceptions
                                    .HttpIntegratorException exception) {
                        Throwable cause = exception.getCause();
                        //ошибка http лалала
                        //решедулим
                        //hey
                    }
                },
                EndpointConnectorExceptions.HttpIntegratorException.class);

        delivery.setDeliveryStatus(DeliveryStatus.IN_PROGRESS);
        persistenceService.merge(delivery);
        EXECUTOR.submit(taskCreator.create());
    }

}
