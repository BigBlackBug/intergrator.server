package com.icl.integrator.dto;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 05.12.13
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
public class SuccessDTO<T> {

    private Class<T> responseClass;

    private T responseValue;

    public SuccessDTO(Class<T> responseClass, T responseValue) {
        this.responseClass = responseClass;
        this.responseValue = responseValue;
    }

    public SuccessDTO() {
    }

    public Class<T> getResponseClass() {
        return responseClass;
    }

    public T getResponseValue() {
        return responseValue;
    }
}
