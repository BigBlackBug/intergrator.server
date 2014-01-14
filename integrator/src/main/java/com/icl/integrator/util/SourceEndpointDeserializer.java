package com.icl.integrator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;

import java.io.IOException;

public final class SourceEndpointDeserializer extends
        JsonDeserializer<ServiceDTO> {

    public SourceEndpointDeserializer() {
    }

    @Override
    public ServiceDTO deserialize(JsonParser jp, DeserializationContext ctx)
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
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setEndpoint(endpointDTO);
        serviceDTO.setSourceResponseAction(sourceResponse);
        serviceDTO.setTargetResponseAction(targetResponse);
        return serviceDTO;
    }

}
