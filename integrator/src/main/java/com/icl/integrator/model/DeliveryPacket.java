package com.icl.integrator.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by BigBlackBug on 2/4/14.
 */
@Entity
@Table(name = "DELIVERY_PACKET")
public class DeliveryPacket extends AbstractEntity {

    @OneToMany(mappedBy = "deliveryPacket")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    private List<Delivery> deliveries = new ArrayList<>();

    @Column(name = "DELIVERY_DATA", nullable = false, updatable = false)
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "org.hibernate.type.StringClobType")
    @Lob
    private String deliveryData;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REQUEST_DATE", nullable = false, updatable = false)
    private Date requestDate;

    public DeliveryPacket() {
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getDeliveryData() {
        return deliveryData;
    }

    public void setDeliveryData(String deliveryData) {
        this.deliveryData = deliveryData;
    }

    public void addDelivery(Delivery delivery) {
        this.deliveries.add(delivery);
    }
}
