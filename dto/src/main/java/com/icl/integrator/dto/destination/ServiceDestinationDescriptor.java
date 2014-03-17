package com.icl.integrator.dto.destination;

import com.icl.integrator.dto.util.EndpointType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 30.01.14
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDestinationDescriptor extends DestinationDescriptor implements Serializable {

	private String service;

	private String action;

	private EndpointType endpointType;

	public ServiceDestinationDescriptor() {
		super(DescriptorType.SERVICE);
	}

	public ServiceDestinationDescriptor(String service,
	                                    String action,
	                                    EndpointType endpointType) {
		this();
		this.service = service;
		this.action = action;
		this.endpointType = endpointType;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public EndpointType getEndpointType() {
		return endpointType;
	}

	public void setEndpointType(EndpointType endpointType) {
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
		if (!super.equals(o)) {
			return false;
		}

		ServiceDestinationDescriptor that = (ServiceDestinationDescriptor) o;

		if (!action.equals(that.action)) {
			return false;
		}
		if (endpointType != that.endpointType) {
			return false;
		}
		if (!service.equals(that.service)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + service.hashCode();
		result = 31 * result + action.hashCode();
		result = 31 * result + endpointType.hashCode();
		return result;
	}
}
