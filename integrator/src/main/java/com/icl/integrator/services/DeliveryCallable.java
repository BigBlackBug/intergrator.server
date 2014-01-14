package com.icl.integrator.services;

import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.util.connectors.EndpointConnector;

import java.util.concurrent.Callable;

class DeliveryCallable<T> implements Callable<ResponseFromTargetDTO> {

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
    public ResponseFromTargetDTO call() throws Exception {
//        RequestToTargetDTO dto = new RequestToTargetDTO();
//        dto.setAdditionalData(packet.getAdditionalData());
//        dto.setData(packet.getData());
        return connector.sendRequest(packet, ResponseFromTargetDTO.class);
    }
}