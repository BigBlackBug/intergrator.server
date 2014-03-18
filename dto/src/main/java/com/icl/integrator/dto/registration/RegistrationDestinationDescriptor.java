package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.destination.DestinationDescriptor;

import java.io.Serializable;

/**
 * Created by BigBlackBug on 2/13/14.
 */
public class RegistrationDestinationDescriptor implements Serializable {

	private DestinationDescriptor destinationDescriptor;

	private boolean forceRegister;

	RegistrationDestinationDescriptor() {
	}

	public RegistrationDestinationDescriptor(
			DestinationDescriptor destinationDescriptor,
			boolean forceRegister) {
		this.destinationDescriptor = destinationDescriptor;
		this.forceRegister = forceRegister;
	}

	public DestinationDescriptor getDestinationDescriptor() {
		return destinationDescriptor;
	}

	public void setDestinationDescriptor(
			DestinationDescriptor destinationDescriptor) {
		this.destinationDescriptor = destinationDescriptor;
	}

	public boolean isForceRegister() {
		return forceRegister;
	}

	public void setForceRegister(boolean forceRegister) {
		this.forceRegister = forceRegister;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RegistrationDestinationDescriptor that = (RegistrationDestinationDescriptor) o;

		if (forceRegister != that.forceRegister) {
			return false;
		}
		if (!destinationDescriptor.equals(that.destinationDescriptor)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = destinationDescriptor.hashCode();
		result = 31 * result + (forceRegister ? 1 : 0);
		return result;
	}
}
