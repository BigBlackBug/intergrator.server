package com.icl.integrator.model;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

/**
 * Created by BigBlackBug on 2/12/14.
 */
@Embeddable
public class DestinationEntity {

	@OneToOne
	private AbstractActionEntity action;

	@OneToOne
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
