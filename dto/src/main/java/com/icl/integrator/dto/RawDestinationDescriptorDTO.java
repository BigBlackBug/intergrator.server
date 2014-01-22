package com.icl.integrator.dto;

import com.icl.integrator.dto.registration.ActionDescriptor;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public class RawDestinationDescriptorDTO {

    private EndpointDTO endpoint;

    private ActionDescriptor actionDescriptor;

    public RawDestinationDescriptorDTO() {

    }

    public RawDestinationDescriptorDTO(EndpointDTO endpoint,
                                       ActionDescriptor actionDescriptor) {
        this.endpoint = endpoint;
        this.actionDescriptor = actionDescriptor;
    }

    public ActionDescriptor getActionDescriptor() {
        return actionDescriptor;
    }

    public void setActionDescriptor(ActionDescriptor actionDescriptor) {
        this.actionDescriptor = actionDescriptor;
    }

    public EndpointDTO getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointDTO endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isInitialized() {
        return endpoint != null && actionDescriptor != null;
    }

}
