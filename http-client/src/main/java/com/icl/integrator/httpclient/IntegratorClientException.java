package com.icl.integrator.httpclient;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public class IntegratorClientException extends RuntimeException {

    public IntegratorClientException() {
    }

    public IntegratorClientException(String message) {
        super(message);
    }

    public IntegratorClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntegratorClientException(Throwable cause) {
        super(cause);
    }
}
