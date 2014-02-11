package com.icl.integrator.model;

import com.icl.integrator.util.EndpointType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 29.01.14
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "ACTION", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"ACTION_NAME", "ENDPOINT_ID"})
})
@Inheritance
@DiscriminatorColumn(name = "ACTION_TYPE",
                     discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractActionEntity extends AbstractEntity {

	@Column(nullable = false, length = 255, name = "ACTION_NAME")
	private String actionName;

	//TODO add references to delivery
	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE", nullable = false, updatable = false,
	        insertable = false)
	private EndpointType type;

	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
	@JoinColumn(name = "ENDPOINT_ID", nullable = false,
	            updatable = false)
	private AbstractEndpointEntity endpoint;

	@OneToMany(mappedBy = "action", fetch = FetchType.EAGER)
	@Cascade(value = {CascadeType.ALL})
	private Set<Delivery> deliveries = new HashSet<>();

	protected AbstractActionEntity() {

	}

	protected AbstractActionEntity(EndpointType endpointType) {
		this.type = endpointType;
	}

	public void addDelivery(Delivery delivery) {
		this.deliveries.add(delivery);
	}

	public Set<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(Set<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	public AbstractEndpointEntity getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(AbstractEndpointEntity endpoint) {
		this.endpoint = endpoint;
	}

	public EndpointType getType() {
		return type;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
}
