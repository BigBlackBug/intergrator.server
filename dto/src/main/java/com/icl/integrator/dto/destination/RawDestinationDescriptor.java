package com.icl.integrator.dto.destination;

import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public class RawDestinationDescriptor extends DestinationDescriptor {

    private EndpointDTO endpoint;

    private ActionDescriptor actionDescriptor;

    public RawDestinationDescriptor() {
        super(DescriptorType.RAW);
    }

    public RawDestinationDescriptor(EndpointDTO endpoint,
                                    ActionDescriptor actionDescriptor) {
        this();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RawDestinationDescriptor that = (RawDestinationDescriptor) o;

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
