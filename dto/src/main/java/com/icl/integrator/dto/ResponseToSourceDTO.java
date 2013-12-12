package com.icl.integrator.dto;

import java.util.UUID;

public class ResponseToSourceDTO {

    private ResponseFromTargetDTO result;

    private String serviceName;

    private String requestID;

    public ResponseToSourceDTO(ResponseFromTargetDTO result, String serviceName,
                               String requestID) {
        this.result = result;
        this.serviceName = serviceName;
        this.requestID = requestID;
    }

    public ResponseToSourceDTO(ResponseFromTargetDTO result) {
        this(result, null, UUID.randomUUID().toString());
    }

    public ResponseToSourceDTO() {
    }

    public String getRequestID() {
        return requestID;
    }

    public ResponseFromTargetDTO getResult() {
        return result;
    }

    public String getServiceName() {
        return serviceName;
    }
}