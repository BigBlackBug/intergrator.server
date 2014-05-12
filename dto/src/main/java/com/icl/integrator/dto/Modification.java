package com.icl.integrator.dto;

import java.io.Serializable;

public class Modification<T> implements Serializable {

	private SubjectType subjectType;

	private ActionType action;

	private T entityName;

	public Modification(SubjectType subjectType, ActionType action, T subject) {
		this.subjectType = subjectType;
		this.action = action;
		this.entityName = subject;
	}

	Modification(){

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Modification modification = (Modification) o;

		if (action != modification.action) {
			return false;
		}
		if (!entityName.equals(modification.entityName)) {
			return false;
		}
		if (subjectType != modification.subjectType) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = subjectType.hashCode();
		result = 31 * result + action.hashCode();
		result = 31 * result + entityName.hashCode();
		return result;
	}

	public SubjectType getSubjectType() {
		return subjectType;
	}

	public ActionType getAction() {
		return action;
	}

	public T getEntityName() {
		return entityName;
	}

	public static enum SubjectType {
		SERVICE, ACTION
	}

	public static enum ActionType {
		ADDED, REMOVED, CHANGED
	}


	public static class ServiceActionPair {

		private String serviceName;

		private String actionName;

		public ServiceActionPair(String serviceName, String actionName) {

			this.serviceName = serviceName;
			this.actionName = actionName;
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

			ServiceActionPair that = (ServiceActionPair) o;

			if (!actionName.equals(that.actionName)) {
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
			return result;
		}

		public String getActionName() {

			return actionName;
		}
	}
}
