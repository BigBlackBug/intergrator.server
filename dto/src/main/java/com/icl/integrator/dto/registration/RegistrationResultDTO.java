package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.ResponseDTO;

import java.util.Map;

/**
 * Created by BigBlackBug on 3/6/14.
 */
public class RegistrationResultDTO {

	private String serviceName;

	private Map<String, ResponseDTO<Void>> actionRegistrationResponses;

	public RegistrationResultDTO() {

	}

	public RegistrationResultDTO(String serviceName,
	                             Map<String, ResponseDTO<Void>> actionRegistrationResponses) {
		this.serviceName = serviceName;
		this.actionRegistrationResponses = actionRegistrationResponses;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Map<String, ResponseDTO<Void>> getActionRegistrationResponses() {
		return actionRegistrationResponses;
	}

	public void setActionRegistrationResponses(
			Map<String, ResponseDTO<Void>> actionRegistrationResponses) {
		this.actionRegistrationResponses = actionRegistrationResponses;
	}
}
