package com.icl.integrator.services;

import com.icl.integrator.model.AbstractActionEntity;
import com.icl.integrator.model.AbstractEndpointEntity;

class PersistentDestination {

	private final AbstractActionEntity action;

	private final AbstractEndpointEntity service;

	PersistentDestination(AbstractEndpointEntity service,
	                      AbstractActionEntity action) {
		this.action = action;
		this.service = service;
	}

	public AbstractActionEntity getAction() {
		return action;
	}

	public AbstractEndpointEntity getService() {
		return service;
	}

}