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
        this(true);
        this.response = new SuccessDTO<T>(responseClass, response);
    }

    public ResponseFromTargetDTO(boolean success) {
        this.success = success;
    }

    public ResponseFromTargetDTO(ErrorDTO error) {
        this(false);

        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResponseFromTargetDTO that = (ResponseFromTargetDTO) o;

        if (success != that.success) {
            return false;
        }
        if (error != null ? !error.equals(that.error) : that.error != null) {
            return false;
        }
        if (response != null ? !response
                .equals(that.response) : that.response != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = response != null ? response.hashCode() : 0;
        result = 31 * result + (success ? 1 : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
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

    public void setSuccess(boolean success) {
        this.success = success;
    }
}