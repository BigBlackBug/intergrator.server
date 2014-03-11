package com.icl.integrator.dto.registration;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class ActionDescriptor {

	private ActionMethod actionMethod;

	protected ActionDescriptor(ActionMethod actionMethod) {
		this.actionMethod = actionMethod;
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

		return true;
	}

	@Override
	public int hashCode() {
		return actionMethod.hashCode();
	}
}
