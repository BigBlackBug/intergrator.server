package com.icl.integrator.services.utils;

public class Modification {

	private final SubjectType subjectType;

	private final ActionType action;

	private final String entityName;

	public Modification(SubjectType subjectType, ActionType action, String entityName) {
		this.subjectType = subjectType;
		this.action = action;
		this.entityName = entityName;
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

	public String getEntityName() {
		return entityName;
	}

	public static enum SubjectType {
		SERVICE, ACTION
	}

	public static enum ActionType {
		ADDED, REMOVED, CHANGED
	}


}
