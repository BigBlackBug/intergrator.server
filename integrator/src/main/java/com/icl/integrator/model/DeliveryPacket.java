package com.icl.integrator.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by BigBlackBug on 2/4/14.
 */
@Entity
@Table(name = "DELIVERY_PACKET")
public class DeliveryPacket extends AbstractEntity {

	@Enumerated(EnumType.STRING)
	private DeliveryStatus deliveryStatus;

	//TODO inheritance
	@Column(name = "DELIVERY_ACTION", nullable = false, length = 255)
	private String action;

	@ManyToMany
	@JoinTable(
			name = "DELIVERY_ENDPOINT_MAPPING",
			joinColumns = {@JoinColumn(name = "DELIVERY_PACKET_ID",
			                           referencedColumnName = "ID")},
			inverseJoinColumns = {@JoinColumn(name = "ENDPOINT_ID",
			                                  referencedColumnName = "ID")})
	private List<AbstractEndpointEntity> destinations;

	@Column(name = "DELIVERY_DATA", nullable = false, updatable = false)
	@Basic(fetch = FetchType.LAZY)
	@Type(type = "org.hibernate.type.StringClobType")
	@Lob
	private String deliveryData;

	@Column(name = "RESPONSE_DATA")
	@Basic(fetch = FetchType.LAZY)
	@Type(type = "org.hibernate.type.StringClobType")
	@Lob
	private String responseData;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUEST_DATE", nullable = false, updatable = false)
	private Date requestDate;

	public DeliveryPacket() {
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	public String getDeliveryData() {
		return deliveryData;
	}

	public void setDeliveryData(String deliveryData) {
		this.deliveryData = deliveryData;
	}

	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<AbstractEndpointEntity> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<AbstractEndpointEntity> destinations) {
		this.destinations = destinations;
	}

	public static enum DeliveryStatus {
		ACCEPTED,IN_PROGRESS, WAITING_FOR_DELIVERY, DELIVERY_OK, DELIVERY_FAILED
	}

}
