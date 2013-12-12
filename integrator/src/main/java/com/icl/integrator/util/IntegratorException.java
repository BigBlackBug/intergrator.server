package com.icl.integrator.util;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 12.12.13
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class IntegratorException extends RuntimeException {

    public IntegratorException(Throwable cause) {
        super(cause);
    }

    public IntegratorException(String message) {
        super(message);
    }

    public IntegratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
