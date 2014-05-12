package com.icl.integrator.dto.editor;

import com.icl.integrator.dto.registration.ActionDescriptor;

/**
 * Created by BigBlackBug on 12.05.2014.
 */
public class EditActionDTO {

	private String actionName;

	private String serviceName;

	private String newActionName;

	private boolean forceChanges;

	private ActionDescriptor actionDescriptor;

	public EditActionDTO(String serviceName, String actionName, String newActionName,
	                     ActionDescriptor actionDescriptor, boolean forceChanges) {
		this.serviceName = serviceName;
		this.actionName = actionName;
		this.newActionName = newActionName;
		this.forceChanges = forceChanges;

		this.actionDescriptor = actionDescriptor;
	}

	public EditActionDTO(String serviceName, String actionName,
	                     ActionDescriptor actionDescriptor, boolean forceChanges) {
		this.actionName = actionName;
		this.serviceName = serviceName;
		this.forceChanges = forceChanges;
		this.actionDescriptor = actionDescriptor;
	}

	public EditActionDTO(String serviceName, String actionName, String newActionName,
	                     boolean forceChanges) {
		this.actionName = actionName;
		this.forceChanges = forceChanges;
		this.serviceName = serviceName;
		this.newActionName = newActionName;
	}

	EditActionDTO() {
	}

	public boolean isForceChanges() {
		return forceChanges;
	}

	public String getServiceName() {
		return serviceName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		EditActionDTO that = (EditActionDTO) o;

		if (forceChanges != that.forceChanges) {
			return false;
		}
		if (actionDescriptor != null ? !actionDescriptor.equals(that.actionDescriptor) :
				that.actionDescriptor != null) {
			return false;
		}
		if (!actionName.equals(that.actionName)) {
			return false;
		}
		if (newActionName != null ? !newActionName.equals(that.newActionName) :
				that.newActionName != null) {
			return false;
		}
		if (!serviceName.equals(that.serviceName)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = actionName.hashCode();
		result = 31 * result + serviceName.hashCode();
		result = 31 * result + (newActionName != null ? newActionName.hashCode() : 0);
		result = 31 * result + (forceChanges ? 1 : 0);
		result = 31 * result + (actionDescriptor != null ? actionDescriptor.hashCode() : 0);
		return result;
	}

	public String getActionName() {
		return actionName;
	}

	public String getNewActionName() {
		return newActionName;
	}

	public ActionDescriptor getActionDescriptor() {
		return actionDescriptor;
	}
}
