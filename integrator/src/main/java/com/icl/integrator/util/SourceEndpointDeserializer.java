package com.icl.integrator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.SourceServiceDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;

import java.io.IOException;

public final class SourceEndpointDeserializer extends
        JsonDeserializer<SourceServiceDTO> {

    public SourceEndpointDeserializer() {
    }

    @Override
    public SourceServiceDTO deserialize(JsonParser jp, DeserializationContext ctx)
            throws IOException {
        ObjectNode treeNode = jp.readValueAsTree();
        MyObjectMapper mapper = new MyObjectMapper();
        EndpointDTO endpointDTO = mapper.readValue
                (treeNode.get("endpoint").toString(), EndpointDTO.class);

        ActionDescriptor sourceResponse = mapper.parseActionDescriptor(
                treeNode.get("sourceResponseAction"),
                endpointDTO.getEndpointType());
        ActionDescriptor targetResponse = mapper.parseActionDescriptor(
                treeNode.get("targetResponseAction"),
                endpointDTO.getEndpointType());
        SourceServiceDTO serviceDTO = new SourceServiceDTO();
        serviceDTO.setEndpoint(endpointDTO);
        serviceDTO.setSourceResponseAction(sourceResponse);
        serviceDTO.setTargetResponseAction(targetResponse);
        return serviceDTO;
    }

}
