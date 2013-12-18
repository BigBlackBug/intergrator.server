package com.icl.integrator.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;

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
            throws IOException, JsonProcessingException {
        ObjectNode treeNode = jp.readValueAsTree();
        return getEndpointDTO(treeNode);
    }

    private EndpointDTO<EndpointDescriptor> getEndpointDTO(JsonNode treeNode)
            throws IOException {
        JsonNode endpointType = treeNode.get("endpointType");
        if (endpointType == null) {
            throw new JsonMappingException("'endpointType' is not " +
                                                   "specified");
        }
        EndpointType type = EndpointType.valueOf(endpointType.asText());
        JsonNode descriptor = treeNode.get("descriptor");
        if (descriptor == null) {
            throw new JsonMappingException("'descriptor' is not " +
                                                   "specified");
        }
        EndpointDescriptor endpointDescriptor = null;
        try {
            if (type == EndpointType.HTTP) {
                HttpEndpointDescriptorDTO dto = new HttpEndpointDescriptorDTO();
                dto.setHost(descriptor.get("host").asText());
//                dto.setPath(descriptor.get("path").asText());
                dto.setPort(descriptor.get("port").asInt());
                endpointDescriptor = dto;
            } else if (type == EndpointType.JMS) {
                JMSEndpointDescriptorDTO dto = new JMSEndpointDescriptorDTO();
//                QueueDTO queueDTO =
//                        descriptor.get("queueDTO").traverse().
//                                readValueAs(QueueDTO.class);
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
//                Map<String, String> jndiProperties =
//                        descriptor.get("jndiProperties").traverse().
//                                readValueAs(typeRef);
                dto.setConnectionFactory(connectionFactory);
                dto.setJndiProperties(jndiProperties);
//                dto.setQueueDTO(queueDTO);
                endpointDescriptor = dto;
            }
        } catch (NullPointerException npe) {
            throw new JsonMappingException(
                    "One of the fields of " +
                            "the descriptor is not specified", npe);
        }

        EndpointDTO<EndpointDescriptor> endpointDTO = new EndpointDTO<>();
        endpointDTO.setDescriptor(endpointDescriptor);
        endpointDTO.setEndpointType(type);
        return endpointDTO;
    }
}
