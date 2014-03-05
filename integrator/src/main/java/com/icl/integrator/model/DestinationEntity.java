package com.icl.integrator.model;

import javax.persistence.*;

/**
 * Created by BigBlackBug on 2/12/14.
 */
@Entity
@Table(name = "DESTINATION_ENTITY")
public class DestinationEntity extends AbstractEntity{

	@OneToOne
	@JoinColumn(name = "ACTION")
	private AbstractActionEntity action;

	@OneToOne
	@JoinColumn(name = "SERVICE")
	private AbstractEndpointEntity service;

	public DestinationEntity() {
	}

	public DestinationEntity(AbstractEndpointEntity service,
			AbstractActionEntity action) {
		this.service = service;
		this.action = action;
	}

	public AbstractActionEntity getAction() {
		return action;
	}

	public void setAction(AbstractActionEntity action) {
		this.action = action;
	}

	public AbstractEndpointEntity getService() {
		return service;
	}

	public void setService(AbstractEndpointEntity service) {
		this.service = service;
	}
}
