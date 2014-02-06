package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by BigBlackBug on 2/4/14.
 */
@Entity
@Table(name = "DELIVERY")
public class Delivery extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "DELIVERY_PACKET_ID", nullable = false,
                updatable = false)
    private DeliveryPacket deliveryPacket;

    @Column(name = "SCHEDULE_REDELIVERY")
    private boolean scheduleRedelivery;

    @Enumerated(EnumType.STRING)
    @Column(name = "DELIVERY_STATUS")
    private DeliveryStatus deliveryStatus;

    @ManyToOne
    @Cascade(value = {CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH})
    @JoinColumn(name = "ACTION_ID", nullable = false, updatable = false)
    private AbstractActionEntity action;

    @ManyToOne
    @Cascade(value = {CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH})
    @JoinColumn(name = "ENDPOINT_ID", nullable = false, updatable = false)
    private AbstractEndpointEntity endpoint;

    @Column(name = "RESPONSE_DATA")
    @Type(type = "org.hibernate.type.StringClobType")
    @Lob
    private String responseData;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RESPONSE_DATE", updatable = false)
    private Date responseDate;

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public boolean scheduleRedelivery() {
        return scheduleRedelivery;
    }

    public void setScheduleRedelivery(boolean scheduleRedelivery) {
        this.scheduleRedelivery = scheduleRedelivery;
    }

    public DeliveryPacket getDeliveryPacket() {
        return deliveryPacket;
    }

    public void setDeliveryPacket(DeliveryPacket deliveryPacket) {
        this.deliveryPacket = deliveryPacket;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public AbstractActionEntity getAction() {
        return action;
    }

    public void setAction(AbstractActionEntity action) {
        this.action = action;
    }

    public AbstractEndpointEntity getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(AbstractEndpointEntity endpoint) {
        this.endpoint = endpoint;
    }
}
