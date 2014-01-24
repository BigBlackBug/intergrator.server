package com.icl.integrator.main;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.httpclient.IntegratorHttpClient;
import com.icl.integrator.util.EndpointType;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    //TODO in body is null client sends x-www-form-urlencoded
    // and server can't respond
    public static void main(String args[]) {
        IntegratorHttpClient httpClient = new IntegratorHttpClient
                ("localhost", 8080);
        register(httpClient);
        ResponseDTO<List<ServiceDTO>> serviceList = httpClient.getServiceList();
        if (serviceList.isSuccess()) {
            List<ServiceDTO> response = serviceList.responseValue();
            for (ServiceDTO service : response) {
                AddActionDTO<HttpActionDTO> actionDTO =
                        new AddActionDTO<>();
                actionDTO.setService(service);
                HttpActionDTO httpActionDTO =
                        new HttpActionDTO("/LOL_ACTION_PATH");
                ActionEndpointDTO<HttpActionDTO> actionEndpoint =
                        new ActionEndpointDTO<>("LOL_ACTION2", httpActionDTO);
                ActionRegistrationDTO<HttpActionDTO> actionRegDTO =
                        new ActionRegistrationDTO<>(actionEndpoint, true);
                actionDTO.setActionRegistrationDTO(actionRegDTO);
                ResponseDTO responseDTO = httpClient.addAction(actionDTO);
                System.out.println(responseDTO);
                System.out.println(service.getServiceName());
                ResponseDTO<List<String>> supportedActions =
                        httpClient.getSupportedActions(service);
                System.out.println(
                        supportedActions.responseValue());
                System.out.println("SERVICE_INFO");
                System.out.println(httpClient.getServiceInfo(service));
                System.out.println("SERVICE_INFO_END");
            }
        }

//        ResponseDTO<List<String>> new_service = httpClient.getSupportedActions(
//                new ServiceDTOWithResponseHandler(
//                        new ServiceDTO("NEW_SERVICE", EndpointType.HTTP)));
//        Map<String, ResponseDTO<UUID>> deliver = deliver(httpClient);
//        System.out.print(deliver);

    }

    public static ResponseDTO<Boolean> ping(IntegratorHttpClient httpClient) {
        PingDTO pingDTO = new PingDTO();
        pingDTO.setAction("ACTION");
        pingDTO.setServiceName("NEW_SERVICE");
        pingDTO.setEndpointType(EndpointType.HTTP);
        return httpClient.isAvailable(pingDTO);
    }

    public static Map<String, ResponseDTO<UUID>> deliver(
            IntegratorHttpClient httpClient) {
        DeliveryDTO deliveryDTO = new DeliveryDTO();
        DestinationDescriptorDTO
                destinationDescriptor = new DestinationDescriptorDTO();
        HttpEndpointDescriptorDTO desr = new
                HttpEndpointDescriptorDTO("192.168.84.142", 8080);
        destinationDescriptor
                .setEndpoint(new EndpointDTO<>(EndpointType.HTTP, desr));
        destinationDescriptor.setActionDescriptor(
                new HttpActionDTO("/api/accept_source_response"));
        deliveryDTO.setTargetResponseHandler(
                new DestinationDescriptorDTO(
                        new EndpointDTO<>(EndpointType.HTTP, desr),
                        new HttpActionDTO("/api/accept_target_response")));
        deliveryDTO.setIntegratorResponseHandler(destinationDescriptor);
        deliveryDTO.setAction("ACTION");
        deliveryDTO.setData(new RequestDataDTO(
                new HashMap<String, Object>() {{
                    put("a", "b");
                }}));
        DestinationDTO destination = new DestinationDTO(
                "NEW_SERVICE", EndpointType.HTTP);
        deliveryDTO.setDestinations(Arrays.asList(destination));
        return httpClient.deliver(deliveryDTO);
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
        regDTO.setActionRegistrations(
                Arrays.asList(new ActionRegistrationDTO<>(actionDTO, true)));
        //----------------------------------------------------------------------
        return httpClient.registerService(regDTO);
    }
}
