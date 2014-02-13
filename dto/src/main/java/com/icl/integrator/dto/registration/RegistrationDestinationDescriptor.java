package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.destination.DestinationDescriptor;

/**
 * Created by BigBlackBug on 2/13/14.
 */
public class RegistrationDestinationDescriptor {

	private DestinationDescriptor destinationDescriptor;

	private boolean forceRegister;

	public RegistrationDestinationDescriptor() {
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
}
