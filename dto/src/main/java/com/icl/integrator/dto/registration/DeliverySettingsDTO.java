package com.icl.integrator.dto.registration;

import java.io.Serializable;

/**
 * Created by BigBlackBug on 2/11/14.
 */
public class DeliverySettingsDTO implements Serializable {

	public static final DeliverySettingsDTO RESET_DELIVERY_SETTINGS =
			new DeliverySettingsDTO(-1, -1);

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DeliverySettingsDTO that = (DeliverySettingsDTO) o;

		if (retryDelay != that.retryDelay) {
			return false;
		}
		if (retryNumber != that.retryNumber) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = retryNumber;
		result = 31 * result + (int) (retryDelay ^ (retryDelay >>> 32));
		return result;
	}
}
