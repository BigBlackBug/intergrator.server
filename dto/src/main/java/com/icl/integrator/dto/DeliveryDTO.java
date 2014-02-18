package com.icl.integrator.dto;

import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:20
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryDTO {

	private DestinationDescriptor responseHandlerDescriptor;

	private List<ServiceDTO> destinations;

	private String action;

	private RequestDataDTO requestData;

	public DeliveryDTO(String action,
	                   RequestDataDTO requestData) {
		this.action = action;
		this.requestData = requestData;
	}

	public DeliveryDTO(String action,
	                   RequestDataDTO requestData,
	                   DestinationDescriptor responseHandlerDescriptor) {
		this.action = action;
		this.requestData = requestData;
		this.responseHandlerDescriptor = responseHandlerDescriptor;
	}

	public DeliveryDTO(
			List<ServiceDTO> destinations,
			RequestDataDTO requestData, String action,
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

		if (!action.equals(that.action)) {
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
				!responseHandlerDescriptor
						.equals(that.responseHandlerDescriptor) :
				that.responseHandlerDescriptor != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = responseHandlerDescriptor != null ?
				responseHandlerDescriptor.hashCode() : 0;
		result = 31 * result +
				(destinations != null ? destinations.hashCode() : 0);
		result = 31 * result + action.hashCode();
		result = 31 * result + requestData.hashCode();
		return result;
	}

	public DestinationDescriptor getResponseHandlerDescriptor() {
		return responseHandlerDescriptor;
	}

	public void setResponseHandlerDescriptor(
			RawDestinationDescriptor responseHandlerDescriptor) {
		this.responseHandlerDescriptor =
				responseHandlerDescriptor;
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
