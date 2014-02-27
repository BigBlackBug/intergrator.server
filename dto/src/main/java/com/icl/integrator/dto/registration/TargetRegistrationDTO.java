package com.icl.integrator.dto.registration;

import com.icl.integrator.dto.EndpointDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 16.12.13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class TargetRegistrationDTO<T extends ActionDescriptor> {

	private String serviceName;

	private EndpointDTO endpoint;

	private DeliverySettingsDTO deliverySettings;

	private List<ActionRegistrationDTO<T>> actionRegistrations;

	public TargetRegistrationDTO() {
	}

	public TargetRegistrationDTO(String serviceName,
	                             EndpointDTO endpoint,
	                             DeliverySettingsDTO deliverySettings,
	                             List<ActionRegistrationDTO<T>> actions) {
		this(serviceName, endpoint, deliverySettings);
		this.actionRegistrations = actions;
	}

	public TargetRegistrationDTO(String serviceName,
	                             EndpointDTO endpoint,
	                             DeliverySettingsDTO deliverySettings) {
		this.serviceName = serviceName;
		this.endpoint = endpoint;
		this.deliverySettings = deliverySettings;
	}

	public DeliverySettingsDTO getDeliverySettings() {
		return deliverySettings;
	}

	public void setDeliverySettings(DeliverySettingsDTO deliverySettings) {
		this.deliverySettings = deliverySettings;
	}

	public EndpointDTO getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(EndpointDTO endpoint) {
		this.endpoint = endpoint;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<ActionRegistrationDTO<T>> getActionRegistrations() {
		return actionRegistrations;
	}

	public void setActionRegistrations(
			List<ActionRegistrationDTO<T>> actionRegistrations) {
		this.actionRegistrations = actionRegistrations;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TargetRegistrationDTO that = (TargetRegistrationDTO) o;

		if (actionRegistrations != null ? !actionRegistrations
				.equals(that.actionRegistrations) :
				that.actionRegistrations != null) {
			return false;
		}
		if (!endpoint.equals(that.endpoint)) {
			return false;
		}
		if (!serviceName.equals(that.serviceName)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = serviceName.hashCode();
		result = 31 * result + endpoint.hashCode();
		result =
				31 * result + (actionRegistrations != null ? actionRegistrations
						.hashCode() : 0);
		return result;
	}

}
