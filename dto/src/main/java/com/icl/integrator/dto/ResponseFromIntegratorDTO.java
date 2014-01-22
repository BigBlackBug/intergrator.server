package com.icl.integrator.dto;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 22.01.14
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class ResponseFromIntegratorDTO<T> {

    private T response;

    public ResponseFromIntegratorDTO() {

    }

    public ResponseFromIntegratorDTO(T response) {
        this.response = response;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
