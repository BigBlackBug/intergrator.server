package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.HttpActionDTO;
import com.icl.integrator.dto.registration.QueueDTO;
import com.icl.integrator.dto.util.EndpointType;

import java.io.IOException;

/**
 * Created by BigBlackBug on 3/12/14.
 */
public class ActionDescriptorDeserializer extends JsonDeserializer<ActionDescriptor> {

	@Override
	public ActionDescriptor deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException {
		ActionDescriptor descriptor = null;
		ObjectNode treeNode = jp.readValueAsTree();
		IntegratorObjectMapper mapper = new IntegratorObjectMapper();
//		ActionMethod actionMethod =
//				mapper.readValue(treeNode.get("actionMethod").toString(), ActionMethod.class);
		EndpointType type =
				mapper.readValue(treeNode.get("endpointType").toString(),
				                 EndpointType.class);
		if (type == EndpointType.HTTP) {
			descriptor = mapper.readValue(treeNode.toString(), HttpActionDTO.class);
//			String path = treeNode.get("path").asText();
//			descriptor = new HttpActionDTO(path, actionMethod);
		} else if (type == EndpointType.JMS) {
			descriptor = mapper.readValue(treeNode.toString(), QueueDTO.class);
		}

		return descriptor;
	}
}
