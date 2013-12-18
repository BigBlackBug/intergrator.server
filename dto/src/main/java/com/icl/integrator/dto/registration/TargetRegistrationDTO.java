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

    public EndpointType getEndpointType() {
        return endpoint.getEndpointType();
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
}
