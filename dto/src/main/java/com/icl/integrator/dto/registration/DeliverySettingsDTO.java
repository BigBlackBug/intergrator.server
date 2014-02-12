package com.icl.integrator.dto.registration;

/**
 * Created by BigBlackBug on 2/11/14.
 */
public class DeliverySettingsDTO {

	private int retryNumber;

	private long retryDelay;

	public DeliverySettingsDTO() {
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
