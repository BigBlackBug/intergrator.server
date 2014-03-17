package com.icl.integrator.dto;

import com.icl.integrator.dto.destination.DestinationDescriptor;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:20
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryDTO implements Serializable{

	private DestinationDescriptor responseHandlerDescriptor;

	private List<ServiceDTO> destinations;

	private String action;

	private RequestDataDTO requestData;

	public DeliveryDTO(RequestDataDTO requestData) {
		this.requestData = requestData;
	}

	public DeliveryDTO(RequestDataDTO requestData,
	                   DestinationDescriptor responseHandlerDescriptor) {
		this.requestData = requestData;
		this.responseHandlerDescriptor = responseHandlerDescriptor;
	}

	public DeliveryDTO(String action,
	                   List<ServiceDTO> destinations,
	                   RequestDataDTO requestData,
	                   DestinationDescriptor responseHandlerDescriptor) {
		this.destinations = destinations;
		this.requestData = requestData;
		this.action = action;
		this.responseHandlerDescriptor = responseHandlerDescriptor;
	}

	public DeliveryDTO() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DeliveryDTO that = (DeliveryDTO) o;

		if (action != null ? !action.equals(that.action) : that.action != null) {
			return false;
		}
		if (destinations != null ? !destinations.equals(that.destinations) :
				that.destinations != null) {
			return false;
		}
		if (!requestData.equals(that.requestData)) {
			return false;
		}
		if (responseHandlerDescriptor != null ?
				!responseHandlerDescriptor.equals(that.responseHandlerDescriptor) :
				that.responseHandlerDescriptor != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = responseHandlerDescriptor != null ? responseHandlerDescriptor.hashCode() : 0;
		result = 31 * result + (destinations != null ? destinations.hashCode() : 0);
		result = 31 * result + (action != null ? action.hashCode() : 0);
		result = 31 * result + requestData.hashCode();
		return result;
	}

	public DestinationDescriptor getResponseHandlerDescriptor() {
		return responseHandlerDescriptor;
	}

	public void setResponseHandlerDescriptor(DestinationDescriptor responseHandlerDescriptor) {
		this.responseHandlerDescriptor = responseHandlerDescriptor;
	}

	public List<ServiceDTO> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<ServiceDTO> destinations) {
		this.destinations = destinations;
	}

	public void addDestination(ServiceDTO serviceDTO) {
		this.destinations.add(serviceDTO);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public RequestDataDTO getRequestData() {
		return requestData;
	}

	public void setRequestData(RequestDataDTO requestData) {
		this.requestData = requestData;
	}
}
