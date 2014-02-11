package com.icl.integrator.services;

import com.icl.integrator.model.AbstractActionEntity;
import com.icl.integrator.model.AbstractEndpointEntity;

class PersistentDestination {

	private final AbstractActionEntity sourceAction;

	private final AbstractEndpointEntity sourceService;

	PersistentDestination(AbstractEndpointEntity sourceService,
	                      AbstractActionEntity sourceAction) {
		this.sourceAction = sourceAction;
		this.sourceService = sourceService;
	}

	public AbstractActionEntity getAction() {
		return sourceAction;
	}

	public AbstractEndpointEntity getService() {
		return sourceService;
	}

}