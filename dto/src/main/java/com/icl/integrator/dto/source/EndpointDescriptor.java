package com.icl.integrator.dto.source;

import com.icl.integrator.dto.util.EndpointType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 11.12.13
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
public abstract class EndpointDescriptor implements Serializable {

    private EndpointType endpointType;

    protected EndpointDescriptor(EndpointType endpointType) {
        this.endpointType = endpointType;
    }

    public EndpointType getEndpointType() {
        return endpointType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EndpointDescriptor that = (EndpointDescriptor) o;

        if (endpointType != that.endpointType) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return endpointType.hashCode();
    }
}
