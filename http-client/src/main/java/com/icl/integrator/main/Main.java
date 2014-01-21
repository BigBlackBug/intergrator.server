package com.icl.integrator.main;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.registration.HttpActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.httpclient.IntegratorHttpClient;
import com.icl.integrator.util.EndpointType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        getServiceList(httpClient);
//        register(httpClient);
//
//        ping(httpClient);
//
//        process(httpClient);
    }


    public static void getServiceList(IntegratorHttpClient httpClient){
        ResponseDTO<List<ServiceDTO>> serviceList = httpClient.getServiceList();
        System.out.println(serviceList);
    }

    public static void ping(IntegratorHttpClient httpClient) {
        PingDTO pingDTO = new PingDTO();
        pingDTO.setAction("ACTION");
        pingDTO.setServiceName("NEW_SERVICE");
        pingDTO.setEndpointType(EndpointType.HTTP);
        ResponseDTO<Boolean> available =
                httpClient.isAvailable(pingDTO);
        System.out.println(available);
    }

    public static void process(IntegratorHttpClient httpClient) {
        DeliveryDTO deliveryDTO = new DeliveryDTO();
        deliveryDTO.setAction("ACTION");
        deliveryDTO.setData(new HashMap<String, Object>() {{
            put("a", "b");
        }});
        DestinationDTO destination = new DestinationDTO(
                "NEW_SERVICE", EndpointType.HTTP);
        deliveryDTO.setDestinations(Arrays.asList(destination));
        httpClient.deliver(deliveryDTO);
    }

    public static void register(IntegratorHttpClient httpClient) {
        TargetRegistrationDTO<HttpActionDTO> regDTO =
                new TargetRegistrationDTO<>();
        regDTO.setServiceName("NEW_SERVICE");
        //----------------------------------------------------------------------
        EndpointDTO<HttpEndpointDescriptorDTO>
                endpointDTO = new EndpointDTO<>();
        endpointDTO.setEndpointType(EndpointType.HTTP);

        HttpEndpointDescriptorDTO descr = new HttpEndpointDescriptorDTO();
        descr.setHost("192.168.84.142");
        descr.setPort(8080);
        endpointDTO.setDescriptor(descr);

        regDTO.setEndpoint(endpointDTO);
        //----------------------------------------------------------------------
        ActionEndpointDTO<HttpActionDTO> actionDTO = new ActionEndpointDTO<>();

        HttpActionDTO actionDescriptor = new HttpActionDTO();
        actionDescriptor.setPath("/api/destination");

        actionDTO.setActionDescriptor(actionDescriptor);
        actionDTO.setActionName("ACTION");
        actionDTO.setForceRegister(true);
        regDTO.setActions(Arrays.asList(actionDTO));
        //----------------------------------------------------------------------
        ResponseDTO<Map<String, ResponseDTO<Void>>> response =
                httpClient.registerService(regDTO);
        System.out.println(response);
    }
}
