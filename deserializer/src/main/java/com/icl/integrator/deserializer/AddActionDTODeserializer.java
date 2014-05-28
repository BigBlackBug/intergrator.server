package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.registration.ActionRegistrationDTO;
import com.icl.integrator.dto.registration.AddActionDTO;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 22.01.14
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class AddActionDTODeserializer extends JsonDeserializer<AddActionDTO> {

    @Override
    public AddActionDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        ObjectNode treeNode = jp.readValueAsTree();
        IntegratorObjectMapper mapper = new IntegratorObjectMapper();
	    String serviceName = treeNode.get("serviceName").asText();
        JsonNode actionRegistrationNode = treeNode.get("actionRegistration");
        boolean forceRegister =
                actionRegistrationNode.get("forceRegister").asBoolean();
	    ActionEndpointDTO action = mapper.readValue(actionRegistrationNode.get("action").toString(),
	                                                   new TypeReference<ActionEndpointDTO>() {
	                                                   });
        ActionRegistrationDTO actionRegistrationDTO = new ActionRegistrationDTO(action, forceRegister);
        return new AddActionDTO<>(serviceName, actionRegistrationDTO);
    }
}
