
package com.icl.integrator.dto;

import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionEndpointDTO;

import java.io.Serializable;
import java.util.List;

public class ServiceAndActions<Y extends ActionDescriptor> implements Serializable {

    private ServiceDTO service;

    private List<ActionEndpointDTO<Y>> actions;

    ServiceAndActions() {
    }

    public ServiceAndActions(ServiceDTO service,
                             List<ActionEndpointDTO<Y>> actions) {
        this.service = service;
        this.actions = actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ServiceAndActions that = (ServiceAndActions) o;

        if (!actions.equals(that.actions)) {
            return false;
        }
        if (!service.equals(that.service)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = service.hashCode();
        result = 31 * result + actions.hashCode();
        return result;
    }

    public ServiceDTO getService() {

        return service;
    }

    public List<ActionEndpointDTO<Y>> getActions() {
        return actions;
    }
}