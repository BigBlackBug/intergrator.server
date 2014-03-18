package com.icl.integrator.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class RequestDataDTO implements Serializable {

	private DeliveryPacketType deliveryPacketType;

	private Map<String,Object> data;

	RequestDataDTO() {
	}

	public RequestDataDTO(DeliveryPacketType deliveryPacketType, Map<String,Object> data) {
		this.deliveryPacketType = deliveryPacketType;
		this.data = data;
	}

	public DeliveryPacketType getDeliveryPacketType() {
		return deliveryPacketType;
	}

	public void setDeliveryPacketType(DeliveryPacketType deliveryPacketType) {
		this.deliveryPacketType = deliveryPacketType;
	}

	public Object getData() {
		return data;
	}

	public void setData(Map<String,Object> data) {
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
		if (deliveryPacketType != that.deliveryPacketType) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = deliveryPacketType.hashCode();
		result = 31 * result + (data != null ? data.hashCode() : 0);
		return result;
	}
}
