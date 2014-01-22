package com.icl.integrator.dto;

import java.util.UUID;

public class ResponseFromTargetDTO {

    private ResponseDTO result;

    private String serviceName;

    private String requestID;

    public ResponseFromTargetDTO(ResponseDTO result, String serviceName,
                                 String requestID) {
        this.result = result;
        this.serviceName = serviceName;
        this.requestID = requestID;
    }

    public ResponseFromTargetDTO(ResponseDTO result) {
        this(result, null, UUID.randomUUID().toString());
    }

    public ResponseFromTargetDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResponseFromTargetDTO that = (ResponseFromTargetDTO) o;

        if (!requestID.equals(that.requestID)) {
            return false;
        }
        if (!result.equals(that.result)) {
            return false;
        }
        if (serviceName != null ? !serviceName
                .equals(that.serviceName) : that.serviceName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = result.hashCode();
        result1 = 31 * result1 + (serviceName != null ? serviceName
                .hashCode() : 0);
        result1 = 31 * result1 + requestID.hashCode();
        return result1;
    }

    public String getRequestID() {
        return requestID;
    }

    public ResponseDTO getResult() {
        return result;
    }

    public String getServiceName() {
        return serviceName;
    }
}