package com.icl.integrator.services;

import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.util.connectors.EndpointConnector;

import java.util.concurrent.Callable;

class DeliveryCallable<T> implements Callable<ResponseDTO> {

    private final T packet;

    private final EndpointConnector connector;

    DeliveryCallable(EndpointConnector connector,
                     T packet) {
        this.connector = connector;
        this.packet = packet;
    }

    public EndpointConnector getConnector() {
        return connector;
    }

    @Override
    public ResponseDTO call() throws Exception {
        connector.testConnection();
        return connector.sendRequest(packet, ResponseDTO.class);
    }
}