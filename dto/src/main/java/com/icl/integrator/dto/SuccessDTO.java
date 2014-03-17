package com.icl.integrator.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 05.12.13
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
public class SuccessDTO<T> implements Serializable {

	private String responseClass;

	private T responseValue;

	public SuccessDTO(String responseClass, T responseValue) {
		this.responseClass = responseClass;
		this.responseValue = responseValue;
	}

	public SuccessDTO(T responseValue) {
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

		if (responseClass != null ? !responseClass
				.equals(that.responseClass) : that.responseClass != null) {
			return false;
		}
		if (responseValue != null ? !responseValue
				.equals(that.responseValue) : that.responseValue != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = responseClass != null ? responseClass.hashCode() : 0;
		result = 31 * result + (responseValue != null ? responseValue
				.hashCode() : 0);
		return result;
	}

	public String getResponseClass() {
		return responseClass;
	}

	public T getResponseValue() {
		return responseValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Class: '").append(responseClass).append("' Value: ")
				.append(responseValue);
		return sb.toString();
	}
}
