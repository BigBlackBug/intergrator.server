package com.icl.integrator.services;

import com.icl.integrator.model.AbstractActionEntity;
import com.icl.integrator.model.AbstractEndpointEntity;

class ResponseDeliveryInfo {

	private final AbstractActionEntity sourceAction;

	private final AbstractEndpointEntity sourceService;

	ResponseDeliveryInfo(AbstractEndpointEntity sourceService,
	                     AbstractActionEntity sourceAction) {
		this.sourceAction = sourceAction;
		this.sourceService = sourceService;
	}

	public AbstractActionEntity getSourceAction() {
		return sourceAction;
	}

	public AbstractEndpointEntity getSourceService() {
		return sourceService;
	}

}