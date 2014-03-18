package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.ResponseDTO;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by BigBlackBug on 3/6/14.
 */
public class RegistrationResultDTO implements Serializable {

	private String serviceName;

	private Map<String, ResponseDTO<Void>> actionRegistrationResponses;

	RegistrationResultDTO() {

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RegistrationResultDTO that = (RegistrationResultDTO) o;

		if (!actionRegistrationResponses.equals(that.actionRegistrationResponses)) {
			return false;
		}
		if (!serviceName.equals(that.serviceName)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = serviceName.hashCode();
		result = 31 * result + actionRegistrationResponses.hashCode();
		return result;
	}
}
