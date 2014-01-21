package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.util.EndpointType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 16.12.13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class TargetRegistrationDTO<T extends ActionDescriptor> {

    private String serviceName;

    private EndpointDTO endpoint;

    private List<ActionEndpointDTO<T>> actions;

    public TargetRegistrationDTO() {
    }

    public TargetRegistrationDTO(String serviceName,
                                 EndpointDTO endpoint,
                                 List<ActionEndpointDTO<T>> actions) {
        this.serviceName = serviceName;
        this.endpoint = endpoint;
        this.actions = actions;
    }

    public EndpointType getEndpointType() {
        return endpoint.getEndpointType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TargetRegistrationDTO that = (TargetRegistrationDTO) o;

        if (!actions.equals(that.actions)) {
            return false;
        }
        if (!endpoint.equals(that.endpoint)) {
            return false;
        }
        if (!serviceName.equals(that.serviceName)) {
            return false;
        }

        return true;
    }

    public EndpointDTO getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointDTO endpoint) {
        this.endpoint = endpoint;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<ActionEndpointDTO<T>> getActions() {
        return actions;
    }

    public void setActions(List<ActionEndpointDTO<T>> actions) {
        this.actions = actions;
    }

    @Override
    public int hashCode() {
        int result = serviceName.hashCode();
        result = 31 * result + endpoint.hashCode();
        result = 31 * result + actions.hashCode();
        return result;
    }
}
