package com.icl.integrator.dto;

import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class FullServiceDTO<T extends EndpointDescriptor,
		Y extends ActionDescriptor> implements Serializable {

	private String serviceName;

	private EndpointDTO<T> serviceEndpoint;

	private List<ActionEndpointDTO<Y>> actions;

	public FullServiceDTO() {
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public EndpointDTO<T> getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(EndpointDTO<T> serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	public List<ActionEndpointDTO<Y>> getActions() {
		return actions;
	}

	public void setActions(List<ActionEndpointDTO<Y>> actions) {
		this.actions = actions;
	}

	public void addAction(ActionEndpointDTO<Y> actionEndpointDTO) {
		this.actions.add(actionEndpointDTO);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FullServiceDTO that = (FullServiceDTO) o;

		if (!actions.equals(that.actions)) {
			return false;
		}
		if (!serviceEndpoint.equals(that.serviceEndpoint)) {
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
		result = 31 * result + serviceEndpoint.hashCode();
		result = 31 * result + actions.hashCode();
		return result;
	}
}
