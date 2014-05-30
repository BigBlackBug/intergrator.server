package com.icl.integrator.dto.registration;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class ActionEndpointDTO<T extends ActionDescriptor> implements Serializable {

    private String actionName;

    private T actionDescriptor;

	private DeliverySettingsDTO deliverySettings;

	ActionEndpointDTO() {
	}

	public ActionEndpointDTO(String actionName, T actionDescriptor,
	                         DeliverySettingsDTO deliverySettings) {
		this.actionName = actionName;
		this.deliverySettings = deliverySettings;
		this.actionDescriptor = actionDescriptor;
	}

	public ActionEndpointDTO(String actionName, T actionDescriptor) {
		this.actionName = actionName;
		this.actionDescriptor = actionDescriptor;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public T getActionDescriptor() {
        return actionDescriptor;
    }

    public void setActionDescriptor(T actionDescriptor) {
        this.actionDescriptor = actionDescriptor;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ActionEndpointDTO that = (ActionEndpointDTO) o;

		if (!actionDescriptor.equals(that.actionDescriptor)) {
			return false;
		}
		if (!actionName.equals(that.actionName)) {
			return false;
		}
		if (deliverySettings != null ? !deliverySettings.equals(that.deliverySettings) :
				that.deliverySettings != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = actionName.hashCode();
		result = 31 * result + actionDescriptor.hashCode();
		result = 31 * result + (deliverySettings != null ? deliverySettings.hashCode() : 0);
		return result;
	}

	public DeliverySettingsDTO getDeliverySettings() {

		return deliverySettings;
	}

	public void setDeliverySettings(DeliverySettingsDTO deliverySettings) {
		this.deliverySettings = deliverySettings;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("actionName: ").append(actionName)
		        .append(" ActionDescriptor: '").append(actionDescriptor).append("'")
				.append(" DeliverySettings: '").append(deliverySettings).append("'");
	    return sb.toString();
    }
}


