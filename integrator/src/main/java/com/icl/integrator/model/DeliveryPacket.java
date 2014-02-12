package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by BigBlackBug on 2/4/14.
 */
@Entity
@Table(name = "DELIVERY_PACKET")
public class DeliveryPacket extends AbstractEntity {

	@OneToMany(mappedBy = "deliveryPacket",fetch = FetchType.EAGER)
	@Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
	private Set<Delivery> deliveries = new HashSet<>();

	@Column(name = "DELIVERY_DATA", nullable = false, updatable = false)
	@Basic(fetch = FetchType.LAZY)
	@Type(type = "org.hibernate.type.StringClobType")
	@Lob
	private String deliveryData;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUEST_DATE", nullable = false, updatable = false)
	private Date requestDate;

	public DeliveryPacket() {
	}

	public Set<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(Set<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getDeliveryData() {
		return deliveryData;
	}

	public void setDeliveryData(String deliveryData) {
		this.deliveryData = deliveryData;
	}

	public void addDelivery(Delivery delivery) {
		this.deliveries.add(delivery);
	}
}
