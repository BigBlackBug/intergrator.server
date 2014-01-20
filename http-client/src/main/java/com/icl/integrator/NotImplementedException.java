package com.icl.integrator;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public class NotImplementedException extends RuntimeException {

    public NotImplementedException() {
    }

    public NotImplementedException(String message) {
        super(message);
    }
}
