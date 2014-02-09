package com.icl.integrator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by bigblackbug on 2/5/14.
 */
@Entity
@Table(name = "DELIVERY_SETTINGS")
public class DeliverySettings extends AbstractEntity {

	private static final int DEFAULT_DELIVERY_RETRY_DELAY_MILLIS = 5000;

	private static final int DEFAULT_DELIVERY_ATTEMPT_NUMBER = 1;

	@Column(name = "RETRY_NUMBER", nullable = false)
	private int retryNumber;

	@Column(name = "RETRY_DELAY", nullable = false)
	private long retryDelay;

	@OneToOne(mappedBy = "deliverySettings")
	private AbstractEndpointEntity endpoint;

	public DeliverySettings() {
	}

	private DeliverySettings(long retryDelay, int retryNumber) {
		this.retryDelay = retryDelay;
		this.retryNumber = retryNumber;
	}

	public static DeliverySettings createDefaultSettings() {
		return new DeliverySettings(DEFAULT_DELIVERY_RETRY_DELAY_MILLIS,
		                            DEFAULT_DELIVERY_ATTEMPT_NUMBER);
	}

	public int getRetryNumber() {
		return retryNumber;
	}

	public void setRetryNumber(int retryNumber) {
		this.retryNumber = retryNumber;
	}

	public long getRetryDelay() {
		return retryDelay;
	}

	public void setRetryDelay(int retryDelay) {
		this.retryDelay = retryDelay;
	}

	public AbstractEndpointEntity getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(AbstractEndpointEntity endpoint) {
		this.endpoint = endpoint;
	}
}
