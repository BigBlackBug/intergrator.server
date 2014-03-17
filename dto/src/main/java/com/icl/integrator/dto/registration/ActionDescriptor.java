package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.util.EndpointType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class ActionDescriptor implements Serializable {

	private ActionMethod actionMethod;

	private EndpointType endpointType;

	protected ActionDescriptor(ActionMethod actionMethod,EndpointType endpointType) {
		this.actionMethod = actionMethod;
		this.endpointType = endpointType;
	}

	public EndpointType getEndpointType() {
		return endpointType;
	}

	public ActionMethod getActionMethod() {
		return actionMethod;
	}

	public void setActionMethod(ActionMethod actionMethod) {
		this.actionMethod = actionMethod;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ActionDescriptor that = (ActionDescriptor) o;

		if (actionMethod != that.actionMethod) {
			return false;
		}
		if (endpointType != that.endpointType) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = actionMethod.hashCode();
		result = 31 * result + endpointType.hashCode();
		return result;
	}
}
