package com.icl.integrator.services;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 16.12.13
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class TargetRegistrationException extends RuntimeException {

    public TargetRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetRegistrationException() {
    }

    public TargetRegistrationException(String message) {
        super(message);
    }
}
