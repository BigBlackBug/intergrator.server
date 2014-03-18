package com.icl.integrator.dto.registration;

import java.io.Serializable;

/**
 * Created by BigBlackBug on 2/11/14.
 */
public class DeliverySettingsDTO implements Serializable {

	private int retryNumber;

	private long retryDelay;

	DeliverySettingsDTO() {
	}

	public DeliverySettingsDTO(int retryNumber, long retryDelay) {
		this.retryNumber = retryNumber;
		this.retryDelay = retryDelay;
	}

	public long getRetryDelay() {
		return retryDelay;
	}

	public void setRetryDelay(long retryDelay) {
		this.retryDelay = retryDelay;
	}

	public int getRetryNumber() {

		return retryNumber;
	}

	public void setRetryNumber(int retryNumber) {
		this.retryNumber = retryNumber;
	}
}
