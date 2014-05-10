package com.icl.integrator.dto.editor;

import com.icl.integrator.dto.registration.DeliverySettingsDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;

/**
 * Created by BigBlackBug on 10.05.2014.
 */
public class EditServiceDTO {

	private String serviceName;

	private String newServiceName;

	private EndpointDescriptor endpointDescriptor;

	private DeliverySettingsDTO deliverySettings;

	EditServiceDTO() {
	}

	public EditServiceDTO(String serviceName, String newServiceName) {
		this.serviceName = serviceName;
		this.newServiceName = newServiceName;
	}

	public EditServiceDTO(String serviceName, String newServiceName,
	                      EndpointDescriptor endpointDescriptor) {
		this.serviceName = serviceName;
		this.newServiceName = newServiceName;
		this.endpointDescriptor = endpointDescriptor;
	}

	public EditServiceDTO(String serviceName, String newServiceName,
	                      EndpointDescriptor endpointDescriptor,
	                      DeliverySettingsDTO deliverySettings) {
		this.serviceName = serviceName;
		this.newServiceName = newServiceName;
		this.endpointDescriptor = endpointDescriptor;
		this.deliverySettings = deliverySettings;
	}

	public EditServiceDTO(String serviceName,
	                      EndpointDescriptor endpointDescriptor) {
		this.serviceName = serviceName;
		this.endpointDescriptor = endpointDescriptor;
	}

	public EditServiceDTO(String serviceName,
	                      DeliverySettingsDTO deliverySettings,
	                      EndpointDescriptor endpointDescriptor) {
		this.serviceName = serviceName;
		this.deliverySettings = deliverySettings;
		this.endpointDescriptor = endpointDescriptor;
	}

	public EditServiceDTO(String serviceName,
	                      DeliverySettingsDTO deliverySettings) {
		this.serviceName = serviceName;
		this.deliverySettings = deliverySettings;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		EditServiceDTO that = (EditServiceDTO) o;

		if (deliverySettings != null ? !deliverySettings.equals(that.deliverySettings) :
				that.deliverySettings != null) {
			return false;
		}
		if (endpointDescriptor != null ? !endpointDescriptor.equals(that.endpointDescriptor) :
				that.endpointDescriptor != null) {
			return false;
		}
		if (newServiceName != null ? !newServiceName.equals(that.newServiceName) :
				that.newServiceName != null) {
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
		result = 31 * result + (newServiceName != null ? newServiceName.hashCode() : 0);
		result = 31 * result + (endpointDescriptor != null ? endpointDescriptor.hashCode() : 0);
		result = 31 * result + (deliverySettings != null ? deliverySettings.hashCode() : 0);
		return result;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getNewServiceName() {
		return newServiceName;
	}

	public EndpointDescriptor getEndpointDescriptor() {
		return endpointDescriptor;
	}

	public DeliverySettingsDTO getDeliverySettings() {
		return deliverySettings;
	}

}
