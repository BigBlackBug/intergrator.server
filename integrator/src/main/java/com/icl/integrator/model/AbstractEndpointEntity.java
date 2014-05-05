package com.icl.integrator.model;

import com.icl.integrator.dto.util.EndpointType;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
		extends AbstractEntity implements HasCreator {

	@OneToMany(mappedBy = "endpoint", fetch = FetchType.EAGER)
	@Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
	protected Set<AbstractActionEntity> actions = new HashSet<>();

	@Column(unique = true, nullable = false, length = 255,
	        name = "SERVICE_NAME")
	private String serviceName;

	@Enumerated(EnumType.STRING)
	@Column(name = "ENDPOINT_TYPE", nullable = false, updatable = false,
	        insertable = false)
	private EndpointType type;

	//TODO make embeddable
	@OneToOne
	@Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
	private DeliverySettings deliverySettings = DeliverySettings.createDefaultSettings();

	@ManyToOne(optional = false)
	@JoinColumn(name = "CREATOR_ID")
	private IntegratorUser creator;

	protected AbstractEndpointEntity() {

	}

	protected AbstractEndpointEntity(EndpointType endpointType) {
		this.type = endpointType;
	}

	@Override
	public IntegratorUser getCreator() {
		return creator;
	}

	public void setCreator(IntegratorUser creator) {
		this.creator = creator;
	}

	public DeliverySettings getDeliverySettings() {
		return deliverySettings;
	}

	public void setDeliverySettings(DeliverySettings deliverySettings) {
		this.deliverySettings = deliverySettings;
	}

	public Set<AbstractActionEntity> getActions() {
		return actions;
	}

	public abstract void setActions(Set<T> actions);

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

	public boolean isGenerated() {
		return creator == null;
	}
}

