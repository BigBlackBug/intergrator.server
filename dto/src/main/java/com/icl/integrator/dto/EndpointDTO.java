package com.icl.integrator.dto;

import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.util.EndpointType;

public class EndpointDTO<T extends EndpointDescriptor> {

    private EndpointType endpointType;

    private T descriptor;

    public EndpointDTO() {
    }

    public EndpointDTO(EndpointType endpointType, T descriptor) {
        this.endpointType = endpointType;
        this.descriptor = descriptor;
    }

    public EndpointType getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(EndpointType endpointType) {
        this.endpointType = endpointType;
    }

    public T getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(T descriptor) {
        this.descriptor = descriptor;
    }
}