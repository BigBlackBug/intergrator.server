package com.icl.integrator.dto;

import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class FullServiceDTO<T extends EndpointDescriptor,
        Y extends ActionDescriptor> {

    private String serviceName;

    private EndpointDTO<T> serviceEndpoint;

    private List<ActionEndpointDTO<Y>> actions;

    public FullServiceDTO() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public EndpointDTO<T> getServiceEndpoint() {
        return serviceEndpoint;
    }

    public void setServiceEndpoint(EndpointDTO<T> serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public List<ActionEndpointDTO<Y>> getActions() {
        return actions;
    }

    public void setActions(List<ActionEndpointDTO<Y>> actions) {
        this.actions = actions;
    }
}
