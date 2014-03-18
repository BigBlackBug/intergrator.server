package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.dto.util.EndpointType;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 18.12.13
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class EndpointDTODeserializer extends JsonDeserializer<EndpointDTO> {

	@Override
	public EndpointDTO deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException{
		ObjectNode treeNode = jp.readValueAsTree();
		JsonNode endpointType = treeNode.get("endpointType");
		EndpointType type = EndpointType.valueOf(endpointType.asText());
		JsonNode descriptor = treeNode.get("descriptor");
		EndpointDescriptor endpointDescriptor = null;
		if (type == EndpointType.HTTP) {
            String host = descriptor.get("host").asText();
            int port = descriptor.get("port").asInt();
			HttpEndpointDescriptorDTO dto = new HttpEndpointDescriptorDTO(host,port);
			endpointDescriptor = dto;
		} else if (type == EndpointType.JMS) {
			String connectionFactory =
					descriptor.get("connectionFactory").asText();
			TypeReference<Map<String, String>> typeRef =
					new TypeReference<Map<String, String>>() {
					};
			ObjectMapper mapper = new ObjectMapper();
			String jndiPropsJson = descriptor.
					get("jndiProperties").toString();
			Map<String, String> jndiProperties =
					mapper.readValue(jndiPropsJson, typeRef);
            JMSEndpointDescriptorDTO dto = new JMSEndpointDescriptorDTO(connectionFactory,jndiProperties);
			endpointDescriptor = dto;
		}

		EndpointDTO<EndpointDescriptor> endpointDTO = new EndpointDTO<>(type,endpointDescriptor);
		return endpointDTO;
	}

}
