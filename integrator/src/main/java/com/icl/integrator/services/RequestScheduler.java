package com.icl.integrator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.model.TaskLogEntry;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandler;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 05.12.13
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RequestScheduler {

    private static final int THREAD_CORE_POOL_SIZE = 5;

    private static final int DEFAULT_DELIVERY_DELAY_SECONDS = 5;

    private static final int DEFAULT_DELIVERY_ATTEMPT_NUMBER = 10;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat
            ("dd.MM.yyyy HH:mm:ss::SSS");

    private static final ScheduledExecutorService EXECUTOR = Executors
            .newScheduledThreadPool(THREAD_CORE_POOL_SIZE);

    private final static Log logger = LogFactory.getLog(
            RequestScheduler.class);

    @Autowired
    private DatabaseRetryHandlerFactory retryHandlerFactory;

    private final Map<Long, Integer> retryIndexMap = new HashMap<>();

    public <T> void schedule(final TaskCreator<T> taskCreator,
                             final TaskCreator<Void> onRetryLimitExceeded) {
        schedule(taskCreator, onRetryLimitExceeded, true,
                 DEFAULT_DELIVERY_ATTEMPT_NUMBER,
                 TimeUnit.SECONDS,
                 DEFAULT_DELIVERY_DELAY_SECONDS);
    }

    public <T> void schedule(final TaskCreator<T> taskCreator,
                             final Callable<Void> onRetryLimitExceeded) {
        schedule(taskCreator, new TaskCreator<>(onRetryLimitExceeded),
                 true,
                 DEFAULT_DELIVERY_ATTEMPT_NUMBER,
                 TimeUnit.SECONDS,
                 DEFAULT_DELIVERY_DELAY_SECONDS);
    }

    public <T> void schedule(final TaskCreator<T> taskCreator) {
        schedule(taskCreator, null,
                 false,
                 DEFAULT_DELIVERY_ATTEMPT_NUMBER,
                 TimeUnit.SECONDS,
                 DEFAULT_DELIVERY_DELAY_SECONDS);
    }

    public <T> void schedule(final TaskCreator<T> taskCreator,
                             final TaskCreator<Void> onRetryLimitExceeded,
                             boolean scheduleRetries,
                             final int attemptNumber,
                             final TimeUnit timeUnit, final long delay){
        retryIndexMap.put(taskCreator.getTaskID(), 1);
        if(scheduleRetries){
            taskCreator.addExceptionHandler(
                new Callback<RestClientException>() {
                    @Override
                    public void execute(RestClientException exception) {
                    logger.info("Exception when sending request",exception);
                    int retryIndex =
                        retryIndexMap.get(taskCreator.getTaskID());
                    if (retryIndex == attemptNumber) {
                        logger.info("Too many retries to handle exception",
                                    exception);
                        retryIndexMap.remove(taskCreator.getTaskID());
                        if (onRetryLimitExceeded != null) {
                            String originalTaskDescription = taskCreator
                                    .getTaskDescription();
                            String retryTaskDescription =
                                    onRetryLimitExceeded.getTaskDescription();
                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode node = mapper.createObjectNode();
                            node.put("originalTaskDescription",
                                     originalTaskDescription);
                            node.put("retryTaskDescription",
                                     retryTaskDescription);
                            DatabaseRetryHandler handler =
                                    retryHandlerFactory.createHandler();
                            handler.setLogEntry(
                                    new TaskLogEntry(
                                        "Не получилось выполнить " +
                                                "таск-обработчик превышения " +
                                                "допустимого количества " +
                                                "повторов таска",
                                        node));
                            schedule(onRetryLimitExceeded,handler);
                        }
                        return;
                    }
                    long retryInterval = timeUnit.toMillis(delay);
                    long nextRetryInterval = getNextRetryInterval(
                        retryIndex, retryInterval);
                    Date nextRequestDate = new Date(
                            System.currentTimeMillis() + nextRetryInterval);
                    logger.info(
                            "Rescheduling next request to " + DATE_FORMAT
                                    .format(nextRequestDate));
                    EXECUTOR.schedule(taskCreator.create(),
                                      nextRetryInterval,
                                      TimeUnit.MILLISECONDS);
                    retryIndexMap
                            .put(taskCreator.getTaskID(), retryIndex + 1);
                    }
                }, RestClientException.class);
        }
        EXECUTOR.submit(taskCreator.create());
    }

    private long getNextRetryInterval(int retryIndex,
                                      long retryIntervalMillis) {
        //TODO a better function
        return retryIndex * retryIndex * retryIntervalMillis;
    }

}
