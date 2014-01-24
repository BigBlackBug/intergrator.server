package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.RawDestinationDescriptorDTO;
import com.icl.integrator.util.EndpointType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 16.12.13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class TargetRegistrationDTO<T extends ActionDescriptor> {

    private RawDestinationDescriptorDTO integratorResponseHandler;

    private String serviceName;

    private EndpointDTO endpoint;

    private List<ActionRegistrationDTO<T>> actionRegistrations;

    public TargetRegistrationDTO() {
    }

    public TargetRegistrationDTO(String serviceName,
                                 EndpointDTO endpoint,
                                 List<ActionRegistrationDTO<T>> actions) {
        this.serviceName = serviceName;
        this.endpoint = endpoint;
        this.actionRegistrations = actions;
    }

    public TargetRegistrationDTO(String serviceName,
                                 EndpointDTO endpoint,
                                 List<ActionRegistrationDTO<T>> actions,
                                 RawDestinationDescriptorDTO integratorResponseHandler) {
        this.serviceName = serviceName;
        this.endpoint = endpoint;
        this.actionRegistrations = actions;
        this.integratorResponseHandler = integratorResponseHandler;
    }

    public RawDestinationDescriptorDTO getIntegratorResponseHandler() {
        return integratorResponseHandler;
    }

    public void setIntegratorResponseHandler(
            RawDestinationDescriptorDTO integratorResponseHandler) {
        this.integratorResponseHandler = integratorResponseHandler;
    }

    public EndpointType getEndpointType() {
        return endpoint.getEndpointType();
    }

    public EndpointDTO getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointDTO endpoint) {
        this.endpoint = endpoint;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<ActionRegistrationDTO<T>> getActionRegistrations() {
        return actionRegistrations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TargetRegistrationDTO that = (TargetRegistrationDTO) o;

        if (!actionRegistrations.equals(that.actionRegistrations)) {
            return false;
        }
        if (!endpoint.equals(that.endpoint)) {
            return false;
        }
        if (integratorResponseHandler != null ? !integratorResponseHandler
                .equals(that.integratorResponseHandler) : that.integratorResponseHandler != null) {
            return false;
        }
        if (!serviceName.equals(that.serviceName)) {
            return false;
        }

        return true;
    }

    public void setActionRegistrations(
            List<ActionRegistrationDTO<T>> actionRegistrations) {
        this.actionRegistrations = actionRegistrations;
    }

    @Override
    public int hashCode() {
        int result =
                integratorResponseHandler != null ? integratorResponseHandler
                        .hashCode() : 0;
        result = 31 * result + serviceName.hashCode();
        result = 31 * result + endpoint.hashCode();
        result = 31 * result + actionRegistrations.hashCode();
        return result;
    }

}
