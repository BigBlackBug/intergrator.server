package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.dto.util.EndpointType;

import java.io.IOException;

/**
 * Created by e.shahmaev on 19.03.14.
 */
public class EndpointDescriptorDeserializer extends JsonDeserializer<EndpointDescriptor> {

    @Override
    public EndpointDescriptor deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        EndpointDescriptor descriptor = null;
        ObjectNode treeNode = jp.readValueAsTree();
        IntegratorObjectMapper mapper = new IntegratorObjectMapper();
        EndpointType type =
                mapper.readValue(treeNode.get("endpointType").toString(),
                                 EndpointType.class);
        if (type == EndpointType.HTTP) {
            descriptor = mapper.readValue(treeNode.toString(), HttpEndpointDescriptorDTO.class);
        } else if (type == EndpointType.JMS) {
            descriptor = mapper.readValue(treeNode.toString(), JMSEndpointDescriptorDTO.class);
        }
        return descriptor;
    }
}
