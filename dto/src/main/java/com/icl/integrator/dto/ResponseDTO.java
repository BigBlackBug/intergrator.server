package com.icl.integrator.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 19.11.13
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public class ResponseDTO<T> implements Serializable {

	protected T response;

	protected boolean success;

	protected ErrorDTO error;

	public ResponseDTO() {
		this(true);
	}

	public ResponseDTO(T response) {
		this(true);
		this.response = response;
	}

	public ResponseDTO(boolean success) {
		this.success = success;
	}

	public ResponseDTO(ErrorDTO error) {
		this(false);
		this.error = error;
	}

	public ResponseDTO(Throwable error) {
		this(false);
		this.error = new ErrorDTO(error);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ResponseDTO that = (ResponseDTO) o;

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

	public T getResponse() {
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Response: ").append(response).append(" Success: '").append(success)
				.append("' Error: ").append(error);
		return sb.toString();
	}
}
