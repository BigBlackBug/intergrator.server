package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.DeliveryPacketType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BigBlackBug on 2/12/14.
 */
public class AutoDetectionRegistrationDTO<T> implements Serializable {

	private DeliveryPacketType deliveryPacketType;

	private T referenceObject;

	private List<RegistrationDestinationDescriptor> destinationDescriptors;

	public AutoDetectionRegistrationDTO(
			DeliveryPacketType deliveryPacketType, T referenceObject,
			List<RegistrationDestinationDescriptor> destinationDescriptors) {
		this.deliveryPacketType = deliveryPacketType;
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

	public DeliveryPacketType getDeliveryPacketType() {
		return deliveryPacketType;
	}

	public void setDeliveryPacketType(DeliveryPacketType deliveryPacketType) {
		this.deliveryPacketType = deliveryPacketType;
	}

	public T getReferenceObject() {
		return referenceObject;
	}

	public void setReferenceObject(T referenceObject) {
		this.referenceObject = referenceObject;
	}
}
