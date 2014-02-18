package com.icl.integrator.dto;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class RequestDataDTO {

	private DeliveryType deliveryType;

	private Object data;

	public RequestDataDTO() {
	}

	public RequestDataDTO(DeliveryType deliveryType, Object data) {
		this.deliveryType = deliveryType;
		this.data = data;
	}

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RequestDataDTO that = (RequestDataDTO) o;

		if (data != null ? !data.equals(that.data) : that.data != null) {
			return false;
		}
		if (deliveryType != that.deliveryType) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = deliveryType.hashCode();
		result = 31 * result + (data != null ? data.hashCode() : 0);
		return result;
	}
}
