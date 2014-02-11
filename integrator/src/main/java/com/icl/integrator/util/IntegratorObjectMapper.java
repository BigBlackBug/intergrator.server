package com.icl.integrator.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.registration.*;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 11.12.13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class IntegratorObjectMapper extends ObjectMapper {

    public static final String MODULE_NAME = "IntegratorObjectMapper";

    public IntegratorObjectMapper() {
        super();
        SimpleModule testModule = new SimpleModule(MODULE_NAME);
        testModule.addDeserializer(DestinationDescriptor.class,
                                   new DestinationDescriptorDeserializer());
        testModule.addDeserializer(TargetRegistrationDTO.class,
                                   new TargetRegistrationDTODeserializer());
        testModule.addDeserializer(EndpointDTO.class,
                                   new EndpointDTODeserializer());
        testModule.addDeserializer(AddActionDTO.class,
                                   new AddActionDTODeserializer());
        testModule.addDeserializer(RawDestinationDescriptor.class,
                                   new RawDestinationDescriptorDeserializer());
	    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
        registerModule(testModule);
    }

    public ActionDescriptor parseActionDescriptor(
            JsonNode treeNode, EndpointType type) throws IOException {
        ActionDescriptor descriptor = null;
        try {
            if (type == EndpointType.HTTP) {
                HttpActionDTO actionDTO = new HttpActionDTO();
                actionDTO.setPath(treeNode.get("path").asText());
                descriptor = actionDTO;
            } else if (type == EndpointType.JMS) {
                QueueDTO queueDTO = new QueueDTO();
                queueDTO.setQueueName(treeNode.get("queueName").asText());
                JsonNode password = treeNode.get("password");
                queueDTO.setPassword(
                        password == null ? null : password.asText());
                JsonNode username = treeNode.get("username");
                queueDTO.setUsername(
                        username == null ? null : username.asText());
                descriptor = queueDTO;
            }
        } catch (NullPointerException npe) {
            throw new JsonMappingException(
                    "One of the fields of " +
                            "the actiondescriptor is not specified", npe);
        }

        return descriptor;
    }

    public <T extends ActionDescriptor> ActionEndpointDTO<T>
    parseActionEndpoint(JsonNode node, EndpointType endpointType)
            throws IOException {
        String actionName = node.get("actionName").asText();
//        boolean forceRegister = node.get("forceRegister").asBoolean();
        ActionDescriptor actionDescriptor = parseActionDescriptor(
                node.get("actionDescriptor"),
                endpointType);
        ActionEndpointDTO actionEndpoint = new
                ActionEndpointDTO<>();
        actionEndpoint.setActionDescriptor(actionDescriptor);
        actionEndpoint.setActionName(actionName);
//        actionEndpoint.setForceRegister(forceRegister);
        return actionEndpoint;
    }

}
