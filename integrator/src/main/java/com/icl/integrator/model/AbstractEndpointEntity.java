package com.icl.integrator.model;

import com.icl.integrator.util.EndpointType;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 29.01.14
 * Time: 12:03
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "ENDPOINT")
@Inheritance
@DiscriminatorColumn(name = "ENDPOINT_TYPE",
                     discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractEndpointEntity<T extends AbstractActionEntity>
        extends AbstractEntity {

    @Column(unique = true, nullable = false, length = 255,
            name = "SERVICE_NAME")
    private String serviceName;

    @OneToMany(mappedBy = "endpoint",
               fetch = FetchType.EAGER)
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    protected List<AbstractActionEntity> actions = new ArrayList<>();

    //TODO add references to delivery
    @Enumerated(EnumType.STRING)
    @Column(name = "ENDPOINT_TYPE", nullable = false, updatable = false,
            insertable = false)
    private EndpointType type;

    @OneToOne
    private DeliverySettings deliverySettings;

    protected AbstractEndpointEntity() {

    }

    public DeliverySettings getDeliverySettings() {
        return deliverySettings;
    }

    public void setDeliverySettings(DeliverySettings deliverySettings) {
        this.deliverySettings = deliverySettings;
    }

    protected AbstractEndpointEntity(EndpointType endpointType) {
        this.type = endpointType;
    }

    public List<AbstractActionEntity> getActions() {
        return actions;
    }

    public abstract void setActions(List<T> actions);

    public abstract T getActionByName(String actionName);

    public void addAction(T action) {
        this.actions.add(action);
    }

    public EndpointType getType() {
        return type;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}

