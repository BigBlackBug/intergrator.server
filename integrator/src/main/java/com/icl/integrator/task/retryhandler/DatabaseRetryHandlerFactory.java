package com.icl.integrator.task.retryhandler;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 09.12.13
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public abstract class DatabaseRetryHandlerFactory {

    public abstract DatabaseRetryHandler createHandler();
}
