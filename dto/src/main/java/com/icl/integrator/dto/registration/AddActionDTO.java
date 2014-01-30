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
}
