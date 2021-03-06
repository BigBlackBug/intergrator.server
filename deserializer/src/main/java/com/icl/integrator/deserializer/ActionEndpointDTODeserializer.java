package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionEndpointDTO;

import java.io.IOException;

/**
 * Created by e.shahmaev on 21.03.14.
 */
public class ActionEndpointDTODeserializer extends JsonDeserializer<ActionEndpointDTO> {

    @Override
    public ActionEndpointDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectNode treeNode = jp.readValueAsTree();
        IntegratorObjectMapper o = new IntegratorObjectMapper();
        String actionName = treeNode.get("actionName").asText();
        TreeNode actionDescriptor = treeNode.get("actionDescriptor");
        ActionDescriptor action = o.readValue(actionDescriptor.toString(),
                                              new TypeReference<ActionDescriptor>() {
                                              });
        return new ActionEndpointDTO(actionName,action);
    }
}
