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

    private SourceServiceDTO sourceService;

    private List<DestinationDTO> destinations;

    private String action;

    private RequestDataDTO data;

    public DeliveryDTO(SourceServiceDTO sourceService, String action,
                       List<DestinationDTO> destinations) {
        this.sourceService = sourceService;
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
        if (!data.equals(that.data)) {
            return false;
        }
        if (!destinations.equals(that.destinations)) {
            return false;
        }
        if (sourceService != null ? !sourceService
                .equals(that.sourceService) : that.sourceService != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = sourceService != null ? sourceService.hashCode() : 0;
        result = 31 * result + destinations.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    public SourceServiceDTO getSourceService() {
        return sourceService;
    }

    public void setSourceService(SourceServiceDTO sourceService) {
        this.sourceService = sourceService;
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
