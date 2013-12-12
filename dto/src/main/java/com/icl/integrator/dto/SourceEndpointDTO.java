package com.icl.integrator.dto;

import com.icl.integrator.dto.source.SourceEndpointDescriptor;
import com.icl.integrator.util.EndpointType;

public class SourceEndpointDTO<T extends SourceEndpointDescriptor> {

    private EndpointType endpointType;

    private T descriptor;

    public SourceEndpointDTO() {
    }

    public SourceEndpointDTO(EndpointType endpointType, T descriptor) {
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