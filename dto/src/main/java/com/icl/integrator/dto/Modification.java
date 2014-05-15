package com.icl.integrator.dto;

import java.io.Serializable;

public class Modification implements Serializable {

	private ActionType action;

	private Subject subject;

	public Modification(ActionType action, Subject subject) {
		this.action = action;
		this.subject = subject;
	}

	Modification() {

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
		if (!subject.equals(modification.subject)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = action.hashCode();
		result = 31 * result + subject.hashCode();
		return result;
	}

	public ActionType getAction() {
		return action;
	}

	public Subject getSubject() {
		return subject;
	}

	public static enum SubjectType {
		SERVICE, ACTION
	}

	public static enum ActionType {
		ADDED, REMOVED, CHANGED
	}

	public static abstract class Subject {

		private final SubjectType subjectType;

		protected Subject(SubjectType subjectType) {
			this.subjectType = subjectType;
		}

		public SubjectType getSubjectType() {
			return subjectType;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			Subject subject = (Subject) o;

			if (subjectType != subject.subjectType) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return subjectType.hashCode();
		}
	}

	public static class ServiceSubject extends Subject {

		private String serviceName;

		public ServiceSubject(String serviceName) {
			super(SubjectType.SERVICE);
			this.serviceName = serviceName;
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

			ServiceSubject that = (ServiceSubject) o;

			if (!serviceName.equals(that.serviceName)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = super.hashCode();
			result = 31 * result + serviceName.hashCode();
			return result;
		}

		public String getServiceName() {
			return serviceName;
		}

	}

	public static class ActionSubject extends Subject {

		private String serviceName;

		private String actionName;

		public ActionSubject(String serviceName, String actionName) {
			super(SubjectType.ACTION);
			this.serviceName = serviceName;
			this.actionName = actionName;
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

			ActionSubject that = (ActionSubject) o;

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
			int result = super.hashCode();
			result = 31 * result + serviceName.hashCode();
			result = 31 * result + actionName.hashCode();
			return result;
		}

		public String getServiceName() {
			return serviceName;
		}

		public String getActionName() {
			return actionName;
		}
	}
}
