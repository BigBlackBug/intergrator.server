package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.editor.EditActionDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.DeliverySettingsDTO;

import java.io.IOException;

/**
 * Created by BigBlackBug on 12.05.2014.
 */
public class EditActionDTODeserializer extends JsonDeserializer<EditActionDTO> {

	@Override
	public EditActionDTO deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		ObjectNode treeNode = jp.readValueAsTree();
		IntegratorObjectMapper mapper = new IntegratorObjectMapper();
		JsonNode serviceEndpoint = treeNode.get("actionDescriptor");
		boolean forceChanges = treeNode.get("forceChanges").asBoolean();
		ActionDescriptor actionDescriptor = mapper.readValue(serviceEndpoint.toString(),
		                                                     ActionDescriptor.class);
		DeliverySettingsDTO deliverySettings =
				mapper.readValue(treeNode.get("deliverySettings").toString(),
				                 DeliverySettingsDTO.class);
		String serviceName = treeNode.get("serviceName").asText();
		String newActionName = treeNode.get("newActionName").asText();
		String actionName = treeNode.get("actionName").asText();
		return new EditActionDTO(serviceName, actionName, newActionName, actionDescriptor,
		                         deliverySettings, forceChanges);
	}
}
