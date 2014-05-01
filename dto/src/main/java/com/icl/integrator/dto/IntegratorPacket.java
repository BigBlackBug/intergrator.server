package com.icl.integrator.dto;

import com.icl.integrator.dto.destination.DestinationDescriptor;

import java.io.Serializable;

/**
 * Универсальный пакет, принимаемый интегратором.
 * Если поле responseHandlerDescriptor установлено,
 * то по указанному в нём адресу будет выслан ответ от интегратора.
 */
public class IntegratorPacket<T, Y extends DestinationDescriptor> implements Serializable {

    private T data;

    private IntegratorMethod method;

    private Y responseHandlerDescriptor;

    public IntegratorPacket() {

    }

    public IntegratorPacket(Y responseHandlerDescriptor) {
        this.responseHandlerDescriptor = responseHandlerDescriptor;
    }

    public IntegratorPacket(T data,
                            Y responseHandlerDescriptor) {
        this.data = data;
        this.responseHandlerDescriptor = responseHandlerDescriptor;
    }

    public IntegratorPacket(T data) {
        this.data = data;
    }

	public IntegratorPacket(IntegratorMethod method, T data, Y responseHandlerDescriptor) {
		this.method = method;
		this.data = data;
		this.responseHandlerDescriptor = responseHandlerDescriptor;
	}

	public IntegratorMethod getMethod() {
        return method;
    }

    public void setMethod(IntegratorMethod method) {
        this.method = method;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Y getResponseHandlerDescriptor() {
        return responseHandlerDescriptor;
    }

    public void setResponseHandlerDescriptor(Y responseHandlerDescriptor) {
        this.responseHandlerDescriptor = responseHandlerDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IntegratorPacket that = (IntegratorPacket) o;

        if (method != that.method) {
            return false;
        }
        if (data != null ? !data
                .equals(that.data) : that.data != null) {
            return false;
        }
        if (responseHandlerDescriptor != null ? !responseHandlerDescriptor
                .equals(that.responseHandlerDescriptor) : that.responseHandlerDescriptor != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result =
                31 * result + (responseHandlerDescriptor != null ? responseHandlerDescriptor
                        .hashCode() : 0);
        return result;
    }
}
