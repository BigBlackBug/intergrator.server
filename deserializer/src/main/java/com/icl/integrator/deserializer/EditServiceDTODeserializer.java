package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.editor.EditServiceDTO;
import com.icl.integrator.dto.registration.DeliverySettingsDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;

import java.io.IOException;

/**
 * Created by BigBlackBug on 10.05.2014.
 */
public class EditServiceDTODeserializer extends JsonDeserializer<EditServiceDTO> {

	@Override
	public EditServiceDTO deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		ObjectNode treeNode = jp.readValueAsTree();
		IntegratorObjectMapper mapper = new IntegratorObjectMapper();
		JsonNode serviceEndpoint = treeNode.get("endpointDescriptor");
		EndpointDescriptor endpoint = mapper.readValue(serviceEndpoint.toString(),
		                                               EndpointDescriptor.class);
		String serviceName = treeNode.get("serviceName").asText();
		String newServiceName = treeNode.get("newServiceName").asText();
		JsonNode deliverySettings = treeNode.get("deliverySettings");
		DeliverySettingsDTO deliverySettingsDTO = mapper.readValue(deliverySettings.toString(),
		                                                           DeliverySettingsDTO.class);
		return new EditServiceDTO(serviceName, newServiceName, endpoint, deliverySettingsDTO);
	}
}
