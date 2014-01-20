package com.icl.integrator;

import com.icl.integrator.dto.DestinationDTO;
import com.icl.integrator.dto.SourceDataDTO;
import com.icl.integrator.util.EndpointType;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String args[]) {
        IntegratorHttpClient httpClient = new IntegratorHttpClient
                ("localhost", 8080);
//        Map<String, String> ping = httpClient.ping();
        SourceDataDTO sourceDataDTO = new SourceDataDTO();
        sourceDataDTO.setAction("test_response");
        sourceDataDTO.setData(new HashMap<String, Object>() {{
            put("a", "b");
        }});
        DestinationDTO destination = new DestinationDTO(
                "LOCALHOST_SERVICE", EndpointType.HTTP);
        sourceDataDTO.setDestinations(Arrays.asList(destination));
        httpClient.process(sourceDataDTO);
//        System.out.println(ping);
    }
}
