package com.icl.integrator.dto;

import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 24.01.14
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class FullServiceDTO<Y extends ActionDescriptor> implements Serializable {

	private String serviceName;

	private EndpointDescriptor endpoint;

	private List<ActionEndpointDTO<Y>> actions;

    public FullServiceDTO() {
	}

    public FullServiceDTO(String serviceName, EndpointDescriptor endpoint,
                          List<ActionEndpointDTO<Y>> actions) {
        this.serviceName = serviceName;
        this.endpoint = endpoint;
        this.actions = actions;
    }

    public FullServiceDTO(String serviceName, EndpointDescriptor endpoint,
                          ActionEndpointDTO<Y>... actions) {
        this(serviceName, endpoint, Arrays.asList(actions));
    }

    public FullServiceDTO(String serviceName, EndpointDescriptor endpoint,
                          ActionEndpointDTO<Y> action) {
        this(serviceName, endpoint, Arrays.asList(action));
    }

    public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public EndpointDescriptor getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(EndpointDescriptor endpoint) {
		this.endpoint = endpoint;
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
		if (!endpoint.equals(that.endpoint)) {
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
		result = 31 * result + endpoint.hashCode();
		result = 31 * result + actions.hashCode();
		return result;
	}
}
