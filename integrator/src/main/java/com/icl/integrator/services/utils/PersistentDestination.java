package com.icl.integrator.services.utils;

import com.icl.integrator.model.AbstractActionEntity;
import com.icl.integrator.model.AbstractEndpointEntity;

public class PersistentDestination {

	private final AbstractActionEntity action;

	private final AbstractEndpointEntity service;

	public PersistentDestination(AbstractEndpointEntity service,
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