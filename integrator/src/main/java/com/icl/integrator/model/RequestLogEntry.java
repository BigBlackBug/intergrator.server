package com.icl.integrator.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 29.01.14
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "REQUEST_LOG_ENTRY")
public class RequestLogEntry extends AbstractEntity {

    @Column(name = "REQUEST_TYPE", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Column(name = "REQUEST_DATA", nullable = false, updatable = false)
    @Basic(fetch = FetchType.LAZY)
    @Type(type="org.hibernate.type.StringClobType")
    @Lob
    private String requestData;

    @Column(name = "RESPONSE_DATA", nullable = false, updatable = false)
    @Basic(fetch = FetchType.LAZY)
    @Type(type="org.hibernate.type.StringClobType")
    @Lob
    private String responseData;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQUEST_DATE", nullable = false, updatable = false)
    private Date requestDate;

    public RequestLogEntry() {

    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public static enum RequestType {
        DELIVERY, REGISTRATION
    }
}
