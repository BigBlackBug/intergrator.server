package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.ServiceDTO;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 22.01.14
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
public class AddActionDTO<T extends ActionDescriptor> {

    private ActionRegistrationDTO<T> actionRegistration;

    private ServiceDTO service;

    public AddActionDTO() {
    }

    public AddActionDTO(ServiceDTO service,
                        ActionRegistrationDTO<T> action) {
        this.service = service;
        this.actionRegistration = action;
    }

    public ActionRegistrationDTO<T> getActionRegistration() {
        return actionRegistration;
    }

    public void setActionRegistration(
            ActionRegistrationDTO<T> actionRegistration) {
        this.actionRegistration = actionRegistration;
    }

    public ServiceDTO getService() {
        return service;
    }

    public void setService(ServiceDTO service) {
        this.service = service;
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
		if (!service.equals(that.service)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = actionRegistration.hashCode();
		result = 31 * result + service.hashCode();
		return result;
	}
}
