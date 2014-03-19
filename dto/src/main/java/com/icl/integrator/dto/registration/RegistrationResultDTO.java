package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ServiceDTO;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by BigBlackBug on 3/6/14.
 */
public class RegistrationResultDTO implements Serializable {

	private ServiceDTO service;

	private Map<String, ResponseDTO<Void>> actionRegistrationResponses;

	RegistrationResultDTO() {

	}

	public RegistrationResultDTO(ServiceDTO service,
	                             Map<String, ResponseDTO<Void>> actionRegistrationResponses) {
		this.service = service;
		this.actionRegistrationResponses = actionRegistrationResponses;
	}

	public ServiceDTO getService() {
		return service;
	}

	public void setService(ServiceDTO service) {
		this.service = service;
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
		if (!service.equals(that.service)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = service.hashCode();
		result = 31 * result + actionRegistrationResponses.hashCode();
		return result;
	}
}
