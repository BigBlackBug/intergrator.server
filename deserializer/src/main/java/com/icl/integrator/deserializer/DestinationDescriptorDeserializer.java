package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;

import java.io.IOException;

public class DestinationDescriptorDeserializer extends JsonDeserializer<DestinationDescriptor> {

	public DestinationDescriptorDeserializer() {
	}

	@Override
	public DestinationDescriptor deserialize(JsonParser jp,
                                             DeserializationContext ctx)
            throws IOException {
        ObjectNode treeNode = jp.readValueAsTree();
        IntegratorObjectMapper mapper = new IntegratorObjectMapper();

        DestinationDescriptor.DescriptorType descriptorType =
                mapper.readValue(treeNode.get("descriptorType").toString(),
                                 DestinationDescriptor.DescriptorType.class);
		DestinationDescriptor destinationDescriptor = null;
		if (descriptorType == DestinationDescriptor.DescriptorType.RAW) {
			destinationDescriptor =
					mapper.readValue(treeNode.toString(), RawDestinationDescriptor.class);
		} else if (descriptorType == DestinationDescriptor.DescriptorType.SERVICE) {
            destinationDescriptor =
		            mapper.readValue(treeNode.toString(), ServiceDestinationDescriptor.class);
		}

		return destinationDescriptor;
    }

}
