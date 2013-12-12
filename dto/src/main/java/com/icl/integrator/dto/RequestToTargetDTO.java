package com.icl.integrator.dto;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class RequestToTargetDTO {

	private Map<String, Object> data;

	private Map<String, Object> additionalData;

	public Map<String, Object> getData() {
		return data;
	}

    public RequestToTargetDTO() {
    }

    public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Map<String, Object> getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(Map<String, Object> additionalData) {
		this.additionalData = additionalData;
	}
}
