package com.icl.integrator.dto;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class RequestDataDTO {

    private Map<String, Object> data;

    private Map<String, Object> additionalData;

    public RequestDataDTO() {
    }

    public RequestDataDTO(Map<String, Object> data) {
        this.data = data;
    }

    public RequestDataDTO(Map<String, Object> data,
                          Map<String, Object> additionalData) {
        this.data = data;
        this.additionalData = additionalData;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
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

        if (additionalData != null ? !additionalData
                .equals(that.additionalData) : that.additionalData != null) {
            return false;
        }
        if (data != null ? !data.equals(that.data) : that.data != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (additionalData != null ? additionalData
                .hashCode() : 0);
        return result;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public boolean empty() {
        return data != null && data.isEmpty() && additionalData != null &&
                additionalData.isEmpty();
    }
}
