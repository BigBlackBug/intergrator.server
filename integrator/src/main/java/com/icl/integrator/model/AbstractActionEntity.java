package com.icl.integrator.model;

import com.icl.integrator.dto.registration.ActionMethod;
import com.icl.integrator.dto.util.EndpointType;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

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
public abstract class AbstractActionEntity extends AbstractEntity implements HasCreator {

	@Column(nullable = false, length = 255, name = "ACTION_NAME")
	private String actionName;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE", nullable = false, updatable = false,
	        insertable = false)
	private EndpointType type;

	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
	@JoinColumn(name = "ENDPOINT_ID", nullable = false)
	private AbstractEndpointEntity endpoint;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_METHOD", nullable = false, updatable = false)
	private ActionMethod actionMethod;

	@ManyToOne(optional = false)
	@JoinColumn(name = "CREATOR_ID")
	private IntegratorUser creator;

	@Embedded
	private DeliverySettings deliverySettings;

	protected AbstractActionEntity() {

	}

	protected AbstractActionEntity(EndpointType endpointType) {
		this.type = endpointType;
	}

	public DeliverySettings getDeliverySettings() {
		return deliverySettings;
	}

	public void setDeliverySettings(DeliverySettings deliverySettings) {
		this.deliverySettings = deliverySettings;
	}

	public ActionMethod getActionMethod() {
		return actionMethod;
	}

	public void setActionMethod(ActionMethod actionMethod) {
		this.actionMethod = actionMethod;
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

	@Override
	public IntegratorUser getCreator() {
		return creator;
	}

	public void setCreator(IntegratorUser creator) {
		this.creator = creator;
	}

	public boolean isGenerated() {
		return creator == null;
	}
}
