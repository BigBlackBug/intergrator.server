package com.icl.integrator.task.retryhandler;

import com.icl.integrator.model.TaskLogEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 06.12.13
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseRetryLimitHandler implements DatabaseRetryHandler {

    private final static Log logger = LogFactory.getLog(
            DatabaseRetryLimitHandler.class);

    @PersistenceContext
    private EntityManager em;

    private TaskLogEntry logEntry;

    public DatabaseRetryLimitHandler() {

    }

    @Override
    @Transactional
    public Void call() throws Exception {
        if (logEntry != null) {
            logger.info("Saving log entry");
            em.persist(logEntry);
        }
        return null;
    }

    @Override
    public void setLogEntry(TaskLogEntry logEntry) {
        this.logEntry = logEntry;
    }
}
