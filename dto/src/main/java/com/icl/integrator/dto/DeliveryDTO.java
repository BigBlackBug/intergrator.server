package com.icl.integrator.dto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:20
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryDTO {

    private DestinationDescriptorDTO targetResponseHandler;

    private List<DestinationDTO> destinations;

    private String action;

    private RequestDataDTO data;

    public DeliveryDTO(DestinationDescriptorDTO targetResponseHandler,
                       String action,
                       List<DestinationDTO> destinations) {
        this.action = action;
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
        if (data != null ? !data.equals(that.data) : that.data != null) {
            return false;
        }
        if (!destinations.equals(that.destinations)) {
            return false;
        }
        if (!targetResponseHandler.equals(that.targetResponseHandler)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetResponseHandler.hashCode();
        result = 31 * result + destinations.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    public DestinationDescriptorDTO getTargetResponseHandler() {
        return targetResponseHandler;
    }

    public void setTargetResponseHandler(
            DestinationDescriptorDTO targetResponseHandler) {
        this.targetResponseHandler = targetResponseHandler;
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

    public RequestDataDTO getData() {
        return data;
    }

    public void setData(RequestDataDTO data) {
        this.data = data;
    }
}
