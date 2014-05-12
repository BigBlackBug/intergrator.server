package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.ResponseDTO;

/**
 * Created by BigBlackBug on 28.04.2014.
 */
public class ActionRegistrationResultDTO {

	private String actionName;

	private ResponseDTO<Void> registrationResult;

	ActionRegistrationResultDTO(){

	}

	public ActionRegistrationResultDTO(String actionName,
	                                   ResponseDTO<Void> registrationResult) {
		this.actionName = actionName;
		this.registrationResult = registrationResult;
	}

	public String getActionName() {
		return actionName;
	}

	public ResponseDTO<Void> getRegistrationResult() {
		return registrationResult;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ActionRegistrationResultDTO that = (ActionRegistrationResultDTO) o;

		if (!actionName.equals(that.actionName)) {
			return false;
		}
		if (!registrationResult.equals(that.registrationResult)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = actionName.hashCode();
		result = 31 * result + registrationResult.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "actionName: " + actionName + " response: " + registrationResult;
	}
}
