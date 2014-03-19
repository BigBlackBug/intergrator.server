package com.icl.integrator.dto.destination;

import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.source.EndpointDescriptor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public class RawDestinationDescriptor extends DestinationDescriptor implements Serializable {

	private EndpointDescriptor endpoint;

	private ActionDescriptor actionDescriptor;

	RawDestinationDescriptor() {
		super(DescriptorType.RAW);
	}

	public RawDestinationDescriptor(EndpointDescriptor endpoint,
	                                ActionDescriptor actionDescriptor) {
		this();
		this.endpoint = endpoint;
		this.actionDescriptor = actionDescriptor;
	}

	public ActionDescriptor getActionDescriptor() {
		return actionDescriptor;
	}

	public void setActionDescriptor(ActionDescriptor actionDescriptor) {
		this.actionDescriptor = actionDescriptor;
	}

	public EndpointDescriptor getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(EndpointDescriptor endpoint) {
		this.endpoint = endpoint;
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

		RawDestinationDescriptor that = (RawDestinationDescriptor) o;

		if (!actionDescriptor.equals(that.actionDescriptor)) {
			return false;
		}
		if (!endpoint.equals(that.endpoint)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + endpoint.hashCode();
		result = 31 * result + actionDescriptor.hashCode();
		return result;
	}
}
