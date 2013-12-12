package com.icl.integrator.services;

import com.icl.integrator.dto.RequestToTargetDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.SourceDataDTO;
import com.icl.integrator.util.connectors.EndpointConnector;

import java.util.concurrent.Callable;

class DeliveryCallable implements Callable<ResponseFromTargetDTO> {

    private final SourceDataDTO packet;

    private final EndpointConnector connector;

    DeliveryCallable(EndpointConnector connector,
                               SourceDataDTO packet) {
        this.connector = connector;
        this.packet = packet;
    }

    @Override
    public ResponseFromTargetDTO call() throws Exception {
        RequestToTargetDTO dto = new RequestToTargetDTO();
        dto.setAdditionalData(packet.getAdditionalData());
        dto.setData(packet.getData());
        return connector.sendRequest(dto, ResponseFromTargetDTO.class);
    }
}