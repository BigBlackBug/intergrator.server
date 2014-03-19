package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.FullServiceDTO;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by e.shahmaev on 19.03.14.
 */
public class FullServiceDTODeserializer extends JsonDeserializer<FullServiceDTO> {

    @Override
    public FullServiceDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectNode treeNode = jp.readValueAsTree();
        IntegratorObjectMapper mapper = new IntegratorObjectMapper();
        JsonNode serviceEndpoint = treeNode.get("endpoint");
        EndpointDescriptor endpointDTO = mapper.readValue(serviceEndpoint.toString(),
                                                          EndpointDescriptor.class);
        String serviceName = treeNode.get("serviceName").asText();
        ArrayNode actions = (ArrayNode) treeNode.get("actions");
        List<ActionEndpointDTO> actionList = new ArrayList<>();
        for (JsonNode action : actions) {
            actionList.add(mapper.readValue(action.toString(), ActionEndpointDTO.class));
        }
        return new FullServiceDTO(serviceName, endpointDTO, actionList);
    }
}
