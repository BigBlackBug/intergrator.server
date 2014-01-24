package com.icl.integrator.dto;

import com.icl.integrator.util.EndpointType;

public class PingDTO {

    private String action;

    private String serviceName;

    private EndpointType endpointType;

    public PingDTO() {

    }

    public PingDTO(String serviceName, String action,
                   EndpointType endpointType) {
        this.action = action;
        this.serviceName = serviceName;
        this.endpointType = endpointType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Service: ").append(serviceName).append(" ").
                append("Action: ").append(action).append(" ").
                append("Type: ").append(endpointType);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PingDTO pingDTO = (PingDTO) o;

        if (!action.equals(pingDTO.action)) {
            return false;
        }
        if (endpointType != pingDTO.endpointType) {
            return false;
        }
        if (!serviceName.equals(pingDTO.serviceName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = action.hashCode();
        result = 31 * result + serviceName.hashCode();
        result = 31 * result + endpointType.hashCode();
        return result;
    }
}