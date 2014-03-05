package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by BigBlackBug on 2/4/14.
 */
@Entity
@Table(name = "DELIVERY")
public class Delivery extends AbstractEntity {

	@Column(name = "DELIVERY_DATA", nullable = false, updatable = false)
	@Basic(fetch = FetchType.LAZY)
	@Type(type = "org.hibernate.type.StringClobType")
	@Lob
	private String deliveryData;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUEST_DATE", nullable = false, updatable = false)
	private Date requestDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "DELIVERY_STATUS")
	private DeliveryStatus deliveryStatus;

	@ManyToOne
	@Cascade(value = {CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH})
	@JoinColumn(name = "ACTION_ID", nullable = false)
	private AbstractActionEntity action;

	@ManyToOne
	@Cascade(value = {CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH})
	@JoinColumn(name = "ENDPOINT_ID", nullable = false)
	private AbstractEndpointEntity endpoint;

	@Column(name = "RESPONSE_DATA")
	@Type(type = "org.hibernate.type.StringClobType")
	@Lob
	private String responseData;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESPONSE_DATE")
	private Date responseDate;

	@Column(name = "LAST_FAILURE_REASON")
	@Type(type = "org.hibernate.type.StringClobType")
	@Lob
	private String lastFailureReason;

	@JoinColumn(name = "RESPONSE_HANDLER_DESTINATION")
	@ManyToOne
	private DestinationEntity responseHandlerDestination;

	@Column(name = "GENERAL_DELIVERY",nullable = false)
	private Boolean generalDelivery;

	public Delivery() {

	}

	public Boolean getGeneralDelivery() {
		return generalDelivery;
	}

	public void setGeneralDelivery(Boolean generalDelivery) {
		this.generalDelivery = generalDelivery;
	}

	public String getDeliveryData() {
		return deliveryData;
	}

	public void setDeliveryData(String deliveryData) {
		this.deliveryData = deliveryData;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
	}

	public String getLastFailureReason() {
		return lastFailureReason;
	}

	public void setLastFailureReason(String lastFailureReason) {
		this.lastFailureReason = lastFailureReason;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public AbstractActionEntity getAction() {
		return action;
	}

	public void setAction(AbstractActionEntity action) {
		this.action = action;
	}

	public AbstractEndpointEntity getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(AbstractEndpointEntity endpoint) {
		this.endpoint = endpoint;
	}

	public DestinationEntity getResponseHandlerDestination() {
		return responseHandlerDestination;
	}

	public void setResponseHandlerDestination(DestinationEntity responseHandlerDestination) {
		this.responseHandlerDestination = responseHandlerDestination;
	}
}
