package com.icl.integrator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;

import java.io.IOException;

public final class SourceEndpointDeserializer extends
        JsonDeserializer<EndpointDTO> {

    public SourceEndpointDeserializer() {
    }

    @Override
    public EndpointDTO deserialize(JsonParser jp,
                                         DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectNode treeNode = jp.readValueAsTree();
        JsonNode endpointType = treeNode.get("endpointType");
        if (endpointType == null) {
            throw new JsonMappingException("'endpointType' is not " +
                                                   "specified");
        }
        EndpointType type = EndpointType.valueOf(endpointType.asText());
        JsonNode descriptor = treeNode.get("descriptor");
        if (descriptor == null) {
            throw new JsonMappingException("'descriptor' is not " +
                                                   "specified");
        }
        EndpointDescriptor dto2 = null;
        try {
            if (type == EndpointType.HTTP) {
                HttpEndpointDescriptorDTO dto = new HttpEndpointDescriptorDTO();
                dto.setHost(descriptor.get("host").asText());
                dto.setPath(descriptor.get("path").asText());
                dto.setPort(descriptor.get("port").asInt());
                dto2 = dto;
            } else {
                //
            }
        } catch (NullPointerException npe) {
            throw new JsonMappingException(
                    "One of the fields of " +
                            "the descriptor is not specified", npe);
        }

        EndpointDTO<EndpointDescriptor>
                endpointDTO =
                new EndpointDTO<>();
        endpointDTO.setDescriptor(dto2);
        endpointDTO.setEndpointType(type);
        return endpointDTO;
    }

}
