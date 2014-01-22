package com.icl.integrator.dto;

import com.icl.integrator.util.EndpointType;

public class PingDTO {

    private RawDestinationDescriptorDTO integratorResponseHandler;

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

    public PingDTO(String serviceName, String action,
                   EndpointType endpointType,
                   RawDestinationDescriptorDTO integratorResponseHandler) {
        this.serviceName = serviceName;
        this.action = action;
        this.endpointType = endpointType;
        this.integratorResponseHandler = integratorResponseHandler;
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
        if (integratorResponseHandler != null ? !integratorResponseHandler
                .equals(pingDTO.integratorResponseHandler) : pingDTO.integratorResponseHandler != null) {
            return false;
        }
        if (!serviceName.equals(pingDTO.serviceName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result =
                integratorResponseHandler != null ? integratorResponseHandler
                        .hashCode() : 0;
        result = 31 * result + action.hashCode();
        result = 31 * result + serviceName.hashCode();
        result = 31 * result + endpointType.hashCode();
        return result;
    }

    public RawDestinationDescriptorDTO getIntegratorResponseHandler() {

        return integratorResponseHandler;
    }

    public void setIntegratorResponseHandler(
            RawDestinationDescriptorDTO integratorResponseHandler) {
        this.integratorResponseHandler = integratorResponseHandler;
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
}