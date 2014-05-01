package com.icl.integrator.dto.registration;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 22.01.14
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
public class AddActionDTO<T extends ActionDescriptor> implements Serializable {

    private ActionRegistrationDTO<T> actionRegistration;

    private String serviceName;

    AddActionDTO() {
    }

    public AddActionDTO(String serviceName,
                        ActionRegistrationDTO<T> action) {
        this.serviceName = serviceName;
        this.actionRegistration = action;
    }

    public ActionRegistrationDTO<T> getActionRegistration() {
        return actionRegistration;
    }

    public void setActionRegistration(
            ActionRegistrationDTO<T> actionRegistration) {
        this.actionRegistration = actionRegistration;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AddActionDTO that = (AddActionDTO) o;

		if (!actionRegistration.equals(that.actionRegistration)) {
			return false;
		}
		if (!serviceName.equals(that.serviceName)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = actionRegistration.hashCode();
		result = 31 * result + serviceName.hashCode();
		return result;
	}
}
