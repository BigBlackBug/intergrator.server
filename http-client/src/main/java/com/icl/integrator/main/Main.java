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
        ResponseDTO<List<ServiceDTO>> serviceList = httpClient.getServiceList();
        if (serviceList.isSuccess()) {
            List<ServiceDTO> response = serviceList.responseValue();
            for (ServiceDTO service : response) {
                System.out.println(service.getServiceName());
                ResponseDTO<List<String>> supportedActions =
                        httpClient.getSupportedActions(service);
                System.out.println(
                        supportedActions.responseValue());
            }
        }
        deliver(httpClient);
    }

    public static ResponseDTO<Boolean> ping(IntegratorHttpClient httpClient) {
        PingDTO pingDTO = new PingDTO();
        pingDTO.setAction("ACTION");
        pingDTO.setServiceName("NEW_SERVICE");
        pingDTO.setEndpointType(EndpointType.HTTP);
        return httpClient.isAvailable(pingDTO);
    }

    public static void deliver(IntegratorHttpClient httpClient) {
        DeliveryDTO deliveryDTO = new DeliveryDTO();
        SourceServiceDTO sourceServiceDTO = new SourceServiceDTO();
        HttpEndpointDescriptorDTO desr = new
                HttpEndpointDescriptorDTO("192.168.84.142", 8080);
        sourceServiceDTO
                .setEndpoint(new EndpointDTO<>(EndpointType.HTTP, desr));
        sourceServiceDTO.setSourceResponseAction(
                new HttpActionDTO("/api/accept_source_response"));
        sourceServiceDTO.setTargetResponseAction(new HttpActionDTO
                                                         ("/api/accept_target_response"));
        deliveryDTO.setSourceService(sourceServiceDTO);
        deliveryDTO.setAction("ACTION");
        deliveryDTO.setData(new RequestDataDTO(
                new HashMap<String, Object>() {{
                    put("a", "b");
                }}));
        DestinationDTO destination = new DestinationDTO(
                "NEW_SERVICE", EndpointType.HTTP);
        deliveryDTO.setDestinations(Arrays.asList(destination));
        httpClient.deliver(deliveryDTO);
    }

    public static ResponseDTO<Map<String, ResponseDTO<Void>>> register(
            IntegratorHttpClient httpClient) {
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
        return httpClient.registerService(regDTO);
    }
}
