package com.icl.integrator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.registration.*;

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
    public TargetRegistrationDTO deserialize(
            JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectNode treeNode = jp.readValueAsTree();
        IntegratorObjectMapper mapper = new IntegratorObjectMapper();
        TargetRegistrationDTO dto = new TargetRegistrationDTO();
	    dto.setDeliverySettings(
			    mapper.readValue(treeNode.get("deliverySettings").toString(),
			                     DeliverySettingsDTO.class));
	    dto.setServiceName(treeNode.get("serviceName").asText());
        EndpointDTO endpoint =
                mapper.readValue(treeNode.get("endpoint").toString(),
                                 EndpointDTO.class);
        dto.setEndpoint(endpoint);

        List<ActionRegistrationDTO<ActionDescriptor>> actions =
                getActions(treeNode.get("actionRegistrations"), endpoint.getEndpointType());
        dto.setActionRegistrations(actions);
        return dto;
    }

    private <T extends ActionDescriptor>
    List<ActionRegistrationDTO<T>> getActions(
            JsonNode actions, EndpointType endpointType) throws IOException {
        IntegratorObjectMapper mapper = new IntegratorObjectMapper();
        List<ActionRegistrationDTO<T>> result = new ArrayList<>();
        int size = actions.size();
        for (int i = 0; i < size; i++) {
            JsonNode node = actions.get(i);
            boolean forceRegister = node.get("forceRegister").asBoolean();
            ActionEndpointDTO<T> action = mapper.parseActionEndpoint(
                    node.get("action"), endpointType);
//	        DeliveryType deliveryType =
//			        mapper.readValue(node.get("deliveryType").toString(), DeliveryType.class);
	        result.add(new ActionRegistrationDTO<>(action, forceRegister));
        }
        return result;
    }
}
