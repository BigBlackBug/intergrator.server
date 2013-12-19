package com.icl.integrator.dto;

import com.icl.integrator.util.EndpointType;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 10:21
 * To change this template use File | Settings | File Templates.
 */
public class DestinationDTO {

    private String serviceName;

    private EndpointType endpointType;

    public DestinationDTO() {
    }

    public DestinationDTO(String serviceName,
                          EndpointType endpointType) {
        this.serviceName = serviceName;
        this.endpointType = endpointType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DestinationDTO that = (DestinationDTO) o;

        if (endpointType != that.endpointType) {
            return false;
        }
        if (!serviceName.equals(that.serviceName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = serviceName.hashCode();
        result = 31 * result + endpointType.hashCode();
        return result;
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
