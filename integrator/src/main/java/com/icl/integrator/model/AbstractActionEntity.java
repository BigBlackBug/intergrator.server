package com.icl.integrator.model;

import com.icl.integrator.util.EndpointType;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

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

	protected AbstractActionEntity() {

	}

	protected AbstractActionEntity(EndpointType endpointType) {
		this.type = endpointType;
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
