package com.icl.integrator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.DestinationDescriptorDTO;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;

import java.io.IOException;

public final class RawDestinationDescriptorDTODeserializer extends
        JsonDeserializer<DestinationDescriptorDTO> {

    public RawDestinationDescriptorDTODeserializer() {
    }

    @Override
    public DestinationDescriptorDTO deserialize(JsonParser jp,
                                                   DeserializationContext ctx)
            throws IOException {
        ObjectNode treeNode = jp.readValueAsTree();
        IntegratorObjectMapper mapper = new IntegratorObjectMapper();
        EndpointDTO endpointDTO = mapper.readValue
                (treeNode.get("endpoint").toString(), EndpointDTO.class);

        DestinationDescriptorDTO result = null;
        if (endpointDTO == null) {
            return result;
        }
        ActionDescriptor sourceResponse = mapper.parseActionDescriptor(
                treeNode.get("actionDescriptor"),
                endpointDTO.getEndpointType());
        result = new DestinationDescriptorDTO();
        result.setEndpoint(endpointDTO);
        result.setActionDescriptor(sourceResponse);
        return result;
    }

}
