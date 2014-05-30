package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.registration.DeliverySettingsDTO;

import java.io.IOException;

/**
 * Created by e.shahmaev on 21.03.14.
 */
public class ActionEndpointDTODeserializer extends JsonDeserializer<ActionEndpointDTO> {

	@Override
	public ActionEndpointDTO deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		ObjectNode treeNode = jp.readValueAsTree();
		IntegratorObjectMapper mapper = new IntegratorObjectMapper();
		String actionName = treeNode.get("actionName").asText();
		TreeNode actionDescriptor = treeNode.get("actionDescriptor");
		DeliverySettingsDTO deliverySettings =
				mapper.readValue(treeNode.get("deliverySettings").toString(),
				                 DeliverySettingsDTO.class);
		ActionDescriptor action = mapper.readValue(actionDescriptor.toString(),
		                                           new TypeReference<ActionDescriptor>() {
		                                      }
		);
		return new ActionEndpointDTO(actionName, action, deliverySettings);
	}
}
