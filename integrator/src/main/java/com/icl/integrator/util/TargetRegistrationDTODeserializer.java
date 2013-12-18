package com.icl.integrator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class TargetRegistrationDTODeserializer extends
        JsonDeserializer<TargetRegistrationDTO> {

    @Override
    public TargetRegistrationDTO deserialize(JsonParser jp,
                                             DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectNode treeNode = jp.readValueAsTree();
        MyObjectMapper mapper = new MyObjectMapper();
        TargetRegistrationDTO dto = new TargetRegistrationDTO();
        dto.setServiceName(treeNode.get("serviceName").asText());
        EndpointDTO endpoint =
                mapper.readValue(treeNode.get("endpoint").toString(),
                                 EndpointDTO.class);
        dto.setEndpoint(endpoint);
        List<ActionEndpointDTO<ActionDescriptor>> actions =
                getActions(treeNode.get
                        ("actions"), endpoint.
                        getEndpointType());
        dto.setActions(actions);
        return dto;
    }

    private <T extends ActionDescriptor>
    List<ActionEndpointDTO<T>> getActions(JsonNode actions,
                                          EndpointType endpointType)
            throws IOException {
        MyObjectMapper mapper = new MyObjectMapper();
        List<ActionEndpointDTO<T>> result = new ArrayList<>();
        int size = actions.size();
        for (int i = 0; i < size; i++) {
            JsonNode node = actions.get(i);

            String actionName = node.get("actionName").asText();
            boolean forceRegister = node.get("forceRegister").asBoolean();
            ActionDescriptor actionDescriptor = mapper.parseActionDescriptor(
                    node.get("actionDescriptor"),
                    endpointType);
            ActionEndpointDTO actionEndpoint = new
                    ActionEndpointDTO<>();
            actionEndpoint.setActionDescriptor(actionDescriptor);
            actionEndpoint.setActionName(actionName);
            actionEndpoint.setForceRegister(forceRegister);
            result.add(actionEndpoint);
        }
        return result;
    }
}
