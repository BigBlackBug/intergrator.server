package com.icl.integrator.httpclient.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public class MethodNotSupportedException extends RuntimeException {

    public MethodNotSupportedException() {
    }

    public MethodNotSupportedException(String message) {
        super(message);
    }
}
