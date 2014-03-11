package com.icl.integrator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.ServiceDTO;
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
public final class AddActionDTODeserializer
        extends JsonDeserializer<AddActionDTO> {

    @Override
    public AddActionDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        ObjectNode treeNode = jp.readValueAsTree();
        IntegratorObjectMapper mapper = new IntegratorObjectMapper();
        ServiceDTO endpoint = mapper.readValue(
                treeNode.get("service").toString(), ServiceDTO.class);
        JsonNode actionRegistrationNode = treeNode.get("actionRegistration");
        boolean forceRegister =
                actionRegistrationNode.get("forceRegister").asBoolean();
        ActionEndpointDTO action = mapper.parseActionEndpoint(
                actionRegistrationNode.get("action"),
                endpoint.getEndpointType());
//	    ActionMethod deliveryType =
//			    mapper.readValue(actionRegistrationNode.get("deliveryType").toString(), ActionMethod.class);
        ActionRegistrationDTO actionRegistrationDTO = new ActionRegistrationDTO(action, forceRegister);
        return new AddActionDTO<>(endpoint, actionRegistrationDTO);
    }
}
