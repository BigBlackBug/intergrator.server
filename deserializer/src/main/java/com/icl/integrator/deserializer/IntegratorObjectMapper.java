package com.icl.integrator.deserializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.icl.integrator.dto.FullServiceDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.editor.EditActionDTO;
import com.icl.integrator.dto.editor.EditServiceDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;

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
        testModule.addDeserializer(EndpointDescriptor.class,
                                   new EndpointDescriptorDeserializer());
	    testModule.addDeserializer(ActionDescriptor.class,
	                               new ActionDescriptorDeserializer());
        testModule.addDeserializer(AddActionDTO.class,
                                   new AddActionDTODeserializer());
        testModule.addDeserializer(RawDestinationDescriptor.class,
                                   new RawDestinationDescriptorDeserializer());
        testModule.addDeserializer(FullServiceDTO.class,
                                   new FullServiceDTODeserializer());
        testModule.addDeserializer(ActionEndpointDTO.class,
                                   new ActionEndpointDTODeserializer());
	    testModule.addDeserializer(EditServiceDTO.class,
	                               new EditServiceDTODeserializer());
	    testModule.addDeserializer(EditActionDTO.class,
	                               new EditActionDTODeserializer());
	    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
        registerModule(testModule);
    }

}
