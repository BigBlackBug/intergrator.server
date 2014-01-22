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

    private ActionEndpointDTO<T> action;

    private ServiceDTO service;

    public AddActionDTO() {
    }

    public AddActionDTO(ServiceDTO service,
                        ActionEndpointDTO<T> action) {
        this.service = service;
        this.action = action;
    }

    public ActionEndpointDTO<T> getAction() {
        return action;
    }

    public void setAction(ActionEndpointDTO<T> action) {
        this.action = action;
    }

    public ServiceDTO getService() {
        return service;
    }

    public void setService(ServiceDTO service) {
        this.service = service;
    }
}
