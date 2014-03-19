package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.source.EndpointDescriptor;

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
		EndpointDescriptor endpointDTO = mapper.readValue(treeNode.get("endpoint").toString(),
		                                           EndpointDescriptor.class);
		ActionDescriptor sourceResponse = mapper.readValue(
				treeNode.get("actionDescriptor").toString(),ActionDescriptor.class);
		return new RawDestinationDescriptor(endpointDTO,sourceResponse);
	}
}
