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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EndpointDTO that = (EndpointDTO) o;

        if (!descriptor.equals(that.descriptor)) {
            return false;
        }
        if (endpointType != that.endpointType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = endpointType.hashCode();
        result = 31 * result + descriptor.hashCode();
        return result;
    }
}