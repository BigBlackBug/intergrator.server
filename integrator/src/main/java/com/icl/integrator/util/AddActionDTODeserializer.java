package com.icl.integrator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.registration.AddActionDTO;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 22.01.14
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public final class AddActionDTODeserializer
        extends JsonDeserializer<AddActionDTO> {

    @Override
    public AddActionDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        AddActionDTO result = new AddActionDTO();
        ObjectNode treeNode = jp.readValueAsTree();
        IntegratorObjectMapper mapper = new IntegratorObjectMapper();
        ServiceDTO endpoint = mapper.readValue(
                treeNode.get("service").toString(), ServiceDTO.class);
        ActionEndpointDTO action = mapper.parseActionEndpoint(
                treeNode.get("action"), endpoint.getEndpointType());
        return new AddActionDTO(endpoint, action);
    }
}
