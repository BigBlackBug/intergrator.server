package com.icl.integrator.model;

import com.icl.integrator.dto.DeliveryPacketType;
import org.hibernate.annotations.Cascade;
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
	@Column(name = "DELIVERY_PACKET_TYPE")
	private DeliveryPacketType deliveryPacketType;

	@Column(name = "REFERENCE_OBJECT")
	@Type(type = "org.hibernate.type.StringClobType")
	@Lob
	private String referenceObject;

	@OneToMany(fetch = FetchType.EAGER)
	@Cascade(value = {
			org.hibernate.annotations.CascadeType.MERGE,
			org.hibernate.annotations.CascadeType.PERSIST,
			org.hibernate.annotations.CascadeType.REFRESH})
	private List<DestinationEntity> destinations = new ArrayList<>();

	public AutoDetectionPacket() {
	}

	public DeliveryPacketType getDeliveryPacketType() {
		return deliveryPacketType;
	}

	public void setDeliveryPacketType(DeliveryPacketType deliveryPacketType) {
		this.deliveryPacketType = deliveryPacketType;
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
