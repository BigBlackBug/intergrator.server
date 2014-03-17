package com.icl.integrator.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 22.01.14
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class ResponseFromIntegratorDTO<T> implements Serializable {

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ResponseFromIntegratorDTO that = (ResponseFromIntegratorDTO) o;

		if (!response.equals(that.response)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return response.hashCode();
	}
}
