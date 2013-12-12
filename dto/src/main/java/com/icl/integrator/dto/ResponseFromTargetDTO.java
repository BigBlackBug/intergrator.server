package com.icl.integrator.dto;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 19.11.13
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public class ResponseFromTargetDTO<T> {

    protected SuccessDTO<T> response;

    protected boolean success;

    protected ErrorDTO error;

    public ResponseFromTargetDTO() {
    }

    public ResponseFromTargetDTO(T response, Class<T> responseClass) {
        this.success = true;
        this.response = new SuccessDTO<T>(responseClass, response);
    }

    public ResponseFromTargetDTO(ErrorDTO error) {
        this.success = false;
        this.error = error;
    }

    public SuccessDTO<T> getResponse() {
        return response;
    }

    public ErrorDTO getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }
}
