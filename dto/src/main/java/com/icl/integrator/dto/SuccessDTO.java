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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SuccessDTO that = (SuccessDTO) o;

        if (!responseClass.equals(that.responseClass)) {
            return false;
        }
        if (!responseValue.equals(that.responseValue)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = responseClass.hashCode();
        result = 31 * result + responseValue.hashCode();
        return result;
    }

    public Class<T> getResponseClass() {
        return responseClass;
    }

    public T getResponseValue() {
        return responseValue;
    }
}
