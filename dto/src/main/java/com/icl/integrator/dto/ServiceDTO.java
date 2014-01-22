package com.icl.integrator.dto;

import com.icl.integrator.util.EndpointType;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 12:00
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDTO {

    private String serviceName;

    private EndpointType endpointType;

    public ServiceDTO(){

    }

    public ServiceDTO(String serviceName,
                      EndpointType endpointType) {
        this.serviceName = serviceName;
        this.endpointType = endpointType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public EndpointType getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(EndpointType endpointType) {
        this.endpointType = endpointType;
    }
}
