package com.icl.integrator.model;

import com.icl.integrator.dto.DeliveryType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BigBlackBug on 2/12/14.
 */
@Entity
@Table(name = "AUTO_DETECTION")
public class AutoDetectionPacket extends AbstractEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "DELIVERY_TYPE")
	private DeliveryType deliveryType;

	@Column(name = "REFERENCE_OBJECT")
	@Type(type = "org.hibernate.type.StringClobType")
	@Lob
	private String referenceObject;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<DestinationEntity> destinations = new ArrayList<DestinationEntity>();

	public AutoDetectionPacket() {
	}

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getReferenceObject() {
		return referenceObject;
	}

	public void setReferenceObject(String referenceObject) {
		this.referenceObject = referenceObject;
	}

	public List<DestinationEntity> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<DestinationEntity> destinations) {
		this.destinations = destinations;
	}

	public void addDestination(DestinationEntity destination){
		this.destinations.add(destination);
	}
}
