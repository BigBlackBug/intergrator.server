package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.registration.ActionDescriptor;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 03.02.14
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
public class RawDestinationDescriptorDeserializer
		extends JsonDeserializer<RawDestinationDescriptor> {

	@Override
	public RawDestinationDescriptor deserialize(JsonParser jp,
	                                            DeserializationContext ctxt)
			throws IOException {
		JsonNode treeNode = jp.readValueAsTree();
		IntegratorObjectMapper mapper = new IntegratorObjectMapper();
		EndpointDTO endpointDTO = mapper.readValue(treeNode.get("endpoint").toString(),
		                                           EndpointDTO.class);

//		if (endpointDTO == null) {
//			return result;
//		}
		ActionDescriptor sourceResponse = mapper.readValue(
				treeNode.get("actionDescriptor").toString(),ActionDescriptor.class);
		RawDestinationDescriptor result = new RawDestinationDescriptor();
		result.setEndpoint(endpointDTO);
		result.setActionDescriptor(sourceResponse);
		return result;
	}
}
