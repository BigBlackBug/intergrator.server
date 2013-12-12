package com.icl.integrator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.icl.integrator.dto.SourceEndpointDTO;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 11.12.13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class MyObjectMapper extends ObjectMapper {

    public MyObjectMapper(){
        super();
        SimpleModule testModule =
                new SimpleModule("MyModule")
                        .addDeserializer(SourceEndpointDTO.class,
                                         new SourceEndpointDeserializer());
        registerModule(testModule);
    }

}
