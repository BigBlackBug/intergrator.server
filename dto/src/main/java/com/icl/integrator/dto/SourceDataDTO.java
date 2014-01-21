package com.icl.integrator.dto;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:20
 * To change this template use File | Settings | File Templates.
 */
public class SourceDataDTO {

    private ServiceDTO source;

    private List<DestinationDTO> destinations;

    private String action;

    private Map<String, Object> data;

    private Map<String, Object> additionalData;

    public SourceDataDTO(ServiceDTO source, String action,
                         List<DestinationDTO> destinations) {
        this.source = source;
        this.action = action;
        this.destinations = destinations;
    }

    public SourceDataDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SourceDataDTO that = (SourceDataDTO) o;

        if (!action.equals(that.action)) {
            return false;
        }
        if (additionalData != null ? !additionalData
                .equals(that.additionalData) : that.additionalData != null) {
            return false;
        }
        if (!data.equals(that.data)) {
            return false;
        }
        if (!destinations.equals(that.destinations)) {
            return false;
        }
        if (!source.equals(that.source)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + destinations.hashCode();
        result = 31 * result + action.hashCode();
        result = 31 * result + data.hashCode();
        result = 31 * result + (additionalData != null ? additionalData
                .hashCode() : 0);
        return result;
    }

    public ServiceDTO getSource() {
        return source;
    }

    public void setSource(ServiceDTO source) {
        this.source = source;
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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }
}
