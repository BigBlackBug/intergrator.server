package com.icl.integrator.task.retryhandler;

import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 09.12.13
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
@Component
public abstract class DatabaseRetryHandlerFactory {

    public abstract DatabaseRetryHandler createHandler();
}
