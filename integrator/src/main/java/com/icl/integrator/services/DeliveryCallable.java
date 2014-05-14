package com.icl.integrator.services;

import com.icl.integrator.util.connectors.EndpointConnector;

import java.util.concurrent.Callable;

class DeliveryCallable<T, Response> implements Callable<Response> {

	private final T packet;

	private final EndpointConnector connector;

	private final Class<Response> responseClass;

	DeliveryCallable(EndpointConnector connector,
	                 T packet, Class<Response> responseClass) {
		this.connector = connector;
		this.responseClass = responseClass;
		this.packet = packet;
	}

	public EndpointConnector getConnector() {
		return connector;
	}

	@Override
	public Response call() throws Exception {
		connector.testConnection();
		return connector.sendRequest(packet, responseClass);
	}
}