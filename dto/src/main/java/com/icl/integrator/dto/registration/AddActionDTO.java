package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.ServiceDTO;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 22.01.14
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
//TODO add response handler. AS A SEPARATE WRAPPERDTO SHIT
public class AddActionDTO<T extends ActionDescriptor> {

    private ActionRegistrationDTO<T> actionRegistrationDTO;

    private ServiceDTO service;

    public AddActionDTO() {
    }

    public AddActionDTO(ServiceDTO service,
                        ActionRegistrationDTO<T> action) {
        this.service = service;
        this.actionRegistrationDTO = action;
    }

    public ActionRegistrationDTO<T> getActionRegistrationDTO() {
        return actionRegistrationDTO;
    }

    public void setActionRegistrationDTO(
            ActionRegistrationDTO<T> actionRegistrationDTO) {
        this.actionRegistrationDTO = actionRegistrationDTO;
    }

    public ServiceDTO getService() {
        return service;
    }

    public void setService(ServiceDTO service) {
        this.service = service;
    }
}
