package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.DeliveryType;

import java.util.List;

/**
 * Created by BigBlackBug on 2/12/14.
 */
public class AutoDetectionRegistrationDTO<T> {

	private DeliveryType deliveryType;

	private T referenceObject;

	private List<RegistrationDestinationDescriptor> destinationDescriptors;

	public AutoDetectionRegistrationDTO(
			DeliveryType deliveryType, T referenceObject,
			List<RegistrationDestinationDescriptor> destinationDescriptors) {
		this.deliveryType = deliveryType;
		this.referenceObject = referenceObject;
		this.destinationDescriptors = destinationDescriptors;
	}

	public AutoDetectionRegistrationDTO() {

	}

	public List<RegistrationDestinationDescriptor> getDestinationDescriptors() {
		return destinationDescriptors;
	}

	public void setDestinationDescriptors(
			List<RegistrationDestinationDescriptor> destinationDescriptors) {
		this.destinationDescriptors = destinationDescriptors;
	}

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}

	public T getReferenceObject() {
		return referenceObject;
	}

	public void setReferenceObject(T referenceObject) {
		this.referenceObject = referenceObject;
	}
}
