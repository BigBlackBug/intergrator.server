package com.icl.integrator.task;

import com.icl.integrator.model.TaskLogEntry;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 11.12.13
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
public interface DatabaseRetryHandler extends Callable<Void> {

    public void setLogEntry(TaskLogEntry logEntry);
}
