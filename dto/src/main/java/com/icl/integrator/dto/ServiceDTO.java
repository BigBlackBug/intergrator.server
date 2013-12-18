package com.icl.integrator.dto;

import com.icl.integrator.dto.registration.ActionDescriptor;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDTO {

    private EndpointDTO endpoint;

    private ActionDescriptor actionDescriptor;

    public ServiceDTO() {

    }

    public EndpointDTO getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointDTO endpoint) {
        this.endpoint = endpoint;
    }

    public ActionDescriptor getActionDescriptor() {
        return actionDescriptor;
    }

    public void setActionDescriptor(ActionDescriptor actionDescriptor) {
        this.actionDescriptor = actionDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ServiceDTO that = (ServiceDTO) o;

        if (!actionDescriptor.equals(that.actionDescriptor)) {
            return false;
        }
        if (!endpoint.equals(that.endpoint)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = endpoint.hashCode();
        result = 31 * result + actionDescriptor.hashCode();
        return result;
    }
}
