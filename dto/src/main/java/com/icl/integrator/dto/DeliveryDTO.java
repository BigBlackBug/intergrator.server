package com.icl.integrator.dto;

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

    private RawDestinationDescriptor targetResponseHandlerDescriptor;

    private List<DestinationDTO> destinations;

    private String action;

    private RequestDataDTO requestData;

    public DeliveryDTO(RawDestinationDescriptor targetResponseHandler,
                       String action,
                       List<DestinationDTO> destinations) {
        this.action = action;
        this.targetResponseHandlerDescriptor = targetResponseHandler;
        this.destinations = destinations;
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
        if (requestData != null ? !requestData
                .equals(that.requestData) : that.requestData != null) {
            return false;
        }
        if (!destinations.equals(that.destinations)) {
            return false;
        }
        if (!targetResponseHandlerDescriptor
                .equals(that.targetResponseHandlerDescriptor)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetResponseHandlerDescriptor.hashCode();
        result = 31 * result + destinations.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + (requestData != null ? requestData.hashCode() : 0);
        return result;
    }

    public RawDestinationDescriptor getTargetResponseHandlerDescriptor() {
        return targetResponseHandlerDescriptor;
    }

    public void setTargetResponseHandlerDescriptor(
            RawDestinationDescriptor targetResponseHandlerDescriptor) {
        this.targetResponseHandlerDescriptor =
                targetResponseHandlerDescriptor;
    }

    public List<DestinationDTO> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<DestinationDTO> destinations) {
        this.destinations = destinations;
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
