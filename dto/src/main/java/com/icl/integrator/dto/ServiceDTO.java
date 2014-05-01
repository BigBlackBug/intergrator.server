package com.icl.integrator.dto;

import com.icl.integrator.dto.util.EndpointType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 21.01.14
 * Time: 12:00
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDTO implements Serializable {

	private String serviceName;

	private EndpointType endpointType;

	private String creator;

	ServiceDTO() {

	}

	public ServiceDTO(String serviceName,
	                  EndpointType endpointType,
	                  String creator) {
		this.serviceName = serviceName;
		this.creator = creator;
		this.endpointType = endpointType;
	}

	public String getCreator() {
		return creator;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public EndpointType getEndpointType() {
		return endpointType;
	}

	public void setEndpointType(EndpointType endpointType) {
		this.endpointType = endpointType;
	}

	@Override
	public String toString() {
		return "type: " + endpointType + " service_name: " + serviceName + " creator: " + creator;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ServiceDTO that = (ServiceDTO) o;

		if (!creator.equals(that.creator)) {
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
		result = 31 * result + endpointType.hashCode();
		result = 31 * result + creator.hashCode();
		return result;
	}
}
