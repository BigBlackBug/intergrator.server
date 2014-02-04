package com.icl.integrator.dto.destination;

import com.icl.integrator.util.EndpointType;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 30.01.14
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDestinationDescriptor extends DestinationDescriptor {

    private String serviceName;

    private String actionName;

    private EndpointType endpointType;

    public ServiceDestinationDescriptor() {
        super(DescriptorType.SERVICE);
    }

    public ServiceDestinationDescriptor(String serviceName,
                                        EndpointType endpointType,
                                        String actionName) {
        this();
        this.serviceName = serviceName;
        this.actionName = actionName;
        this.endpointType = endpointType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getActionName() {
        return actionName;
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

        ServiceDestinationDescriptor that = (ServiceDestinationDescriptor) o;

        if (!actionName.equals(that.actionName)) {
            return false;
        }
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
        result = 31 * result + actionName.hashCode();
        result = 31 * result + endpointType.hashCode();
        return result;
    }
}
