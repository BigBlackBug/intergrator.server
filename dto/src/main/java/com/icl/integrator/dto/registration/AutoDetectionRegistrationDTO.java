package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.DeliveryType;
import com.icl.integrator.dto.destination.DestinationDescriptor;

import java.util.List;

/**
 * Created by BigBlackBug on 2/12/14.
 */
public class AutoDetectionRegistrationDTO<T> {

	private DeliveryType deliveryType;

	private T referenceObject;

	private List<DestinationDescriptor> destinationDescriptors;

	public AutoDetectionRegistrationDTO(
			DeliveryType deliveryType, T referenceObject,
			List<DestinationDescriptor> destinationDescriptors) {
		this.deliveryType = deliveryType;
		this.referenceObject = referenceObject;
		this.destinationDescriptors = destinationDescriptors;
	}

	public AutoDetectionRegistrationDTO() {

	}

	public List<DestinationDescriptor> getDestinationDescriptors() {
		return destinationDescriptors;
	}

	public void setDestinationDescriptors(
			List<DestinationDescriptor> destinationDescriptors) {
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
