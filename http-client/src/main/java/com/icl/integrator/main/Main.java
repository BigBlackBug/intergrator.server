package com.icl.integrator.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.PingDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.registration.ActionRegistrationDTO;
import com.icl.integrator.dto.registration.HttpActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.httpclient.IntegratorHttpClient;
import com.icl.integrator.util.EndpointType;

import java.util.Arrays;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    //TODO if body is null client sends x-www-form-urlencoded
    // and server can't respond
    public static void main(String args[]) {
//        IntegratorHttpClient httpClient = new IntegratorHttpClient
//                ("192.168.83.91", "integrator", 18080);
//        ResponseDTO<List<ServiceDTO>> serviceList = httpClient.getServiceList();
//        System.out.println(serviceList);
        ObjectWriter writer =
                new ObjectMapper().writer().withDefaultPrettyPrinter();
        TargetRegistrationDTO<HttpActionDTO> regDTO =
                new TargetRegistrationDTO<HttpActionDTO>();
        regDTO.setServiceName("NEW_SERVICE");
        //----------------------------------------------------------------------
        EndpointDTO<HttpEndpointDescriptorDTO>
                endpointDTO = new EndpointDTO<HttpEndpointDescriptorDTO>();
        endpointDTO.setEndpointType(EndpointType.HTTP);

        HttpEndpointDescriptorDTO descr = new HttpEndpointDescriptorDTO();
        descr.setHost("192.168.84.142");
        descr.setPort(8080);
        endpointDTO.setDescriptor(descr);

        regDTO.setEndpoint(endpointDTO);
        //----------------------------------------------------------------------
        ActionEndpointDTO<HttpActionDTO> actionDTO =
                new ActionEndpointDTO<HttpActionDTO>();

        HttpActionDTO actionDescriptor = new HttpActionDTO();
        actionDescriptor.setPath("/destination/handleRequest");

        actionDTO.setActionDescriptor(actionDescriptor);
        actionDTO.setActionName("ACTION");
        regDTO.setActionRegistrations(
                Arrays.asList(
                        new ActionRegistrationDTO<HttpActionDTO>(actionDTO,
                                                                 true)));
        try {
            System.out.println(writer.writeValueAsString(regDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


//        ResponseDTO<Map<String, ResponseDTO<Void>>> register =
//                register(httpClient);
//        ResponseDTO<List<ServiceDTO>> serviceList = httpClient.getServiceList();
//        if (serviceList.isSuccess()) {
//            List<ServiceDTO> response = serviceList.responseValue();
//            for (ServiceDTO service : response) {
//                AddActionDTO<HttpActionDTO> actionDTO =
//                        new AddActionDTO<>();
//                actionDTO.setService(service);
//                HttpActionDTO httpActionDTO =
//                        new HttpActionDTO("/LOL_ACTION_PATH");
//                ActionEndpointDTO<HttpActionDTO> actionEndpoint =
//                        new ActionEndpointDTO<>("LOL_ACTION2", httpActionDTO);
//                ActionRegistrationDTO<HttpActionDTO> actionRegDTO =
//                        new ActionRegistrationDTO<>(actionEndpoint, true);
//                actionDTO.setActionRegistrationDTO(actionRegDTO);
//                ResponseDTO responseDTO = httpClient.addAction(actionDTO);
//                System.out.println(responseDTO);
//                System.out.println(service.getServiceName());
//                ResponseDTO<List<String>> supportedActions =
//                        httpClient.getSupportedActions(service);
//                System.out.println(
//                        supportedActions.responseValue());
//                System.out.println("SERVICE_INFO");
//                System.out.println(httpClient.getServiceInfo(service));
//                System.out.println("SERVICE_INFO_END");
//            }
//        }

//        ResponseDTO<List<String>> new_service = httpClient.getSupportedActions(
//                new ServiceDTO("NEW_SERVICE", EndpointType.HTTP));

//        ResponseDTO<Boolean> available =
//                httpClient.isAvailable(new PingDTO("NEW_SERVICE", "ACTION",
////                                                   EndpointType.HTTP));
//        Map<String, ResponseDTO<UUID>> deliver = deliver(httpClient);
//        System.out.print(deliver);
//        System.out.println(available.getResponse().getResponseValue());

    }

    public static ResponseDTO<Boolean> ping(IntegratorHttpClient httpClient) {
        PingDTO pingDTO = new PingDTO();
        pingDTO.setAction("ACTION");
        pingDTO.setServiceName("NEW_SERVICE");
        pingDTO.setEndpointType(EndpointType.HTTP);
        return httpClient.isAvailable(pingDTO);
    }

//    public static ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
//            IntegratorHttpClient httpClient) {
//        DeliveryDTO deliveryDTO = new DeliveryDTO();
//
//        HttpEndpointDescriptorDTO desr = new
//                HttpEndpointDescriptorDTO("192.168.84.142", 8080);
//        EndpointDTO<HttpEndpointDescriptorDTO> endpoint =
//                new EndpointDTO<HttpEndpointDescriptorDTO>(EndpointType.HTTP,
//                                                           desr);
//
////        deliveryDTO.setTargetResponseHandlerDescriptor(
////                new DestinationDescriptorDTO(
////                        endpoint,
////                        new HttpActionDTO("/source/handleResponseFromTarget")
////                ));
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("java.naming.provider.url", "tcp://localhost:61616");
//        map.put("java.naming.factory.initial", "org.apache.activemq.jndi" +
//                ".ActiveMQInitialContextFactory");
//
////        HashMap<String, String> map = new HashMap<>();
////        map.put("java.naming.provider.url", "tcp://localhost:61616");
////        map.put("java.naming.factory.initial", "org.apache.activemq.jndi" +
////                ".ActiveMQInitialContextFactory");
////        deliveryResponseHandler.setEndpoint(
////                new EndpointDTO<>(EndpointType.JMS, new
////                        JMSEndpointDescriptorDTO("ConnectionFactory", map)
////                ));
////        deliveryResponseHandler.setActionDescriptor(new QueueDTO
////                                                            ("SourceQueue"));
//        deliveryDTO.setAction("ACTION");
//        deliveryDTO.setRequestData(new RequestDataDTO(
//                new HashMap<String, Object>() {{
//                    put("a", "b");
//                }}));
//        DestinationDTO destination = new DestinationDTO(
//                "NEW_SERVICE", EndpointType.HTTP);
//        deliveryDTO.setDestinations(Arrays.asList(destination));
//        return httpClient.deliver(
//                new IntegratorPacket<DeliveryDTO>(deliveryDTO,
//                                                  deliveryResponseHandler));
//    }

    public static ResponseDTO<Map<String, ResponseDTO<Void>>> register(
            IntegratorHttpClient httpClient) {
        TargetRegistrationDTO<HttpActionDTO> regDTO =
                new TargetRegistrationDTO<HttpActionDTO>();
        regDTO.setServiceName("NEW_SERVICE");
        //----------------------------------------------------------------------
        EndpointDTO<HttpEndpointDescriptorDTO>
                endpointDTO = new EndpointDTO<HttpEndpointDescriptorDTO>();
        endpointDTO.setEndpointType(EndpointType.HTTP);

        HttpEndpointDescriptorDTO descr = new HttpEndpointDescriptorDTO();
        descr.setHost("192.168.84.142");
        descr.setPort(8080);
        endpointDTO.setDescriptor(descr);

        regDTO.setEndpoint(endpointDTO);
        //----------------------------------------------------------------------
        ActionEndpointDTO<HttpActionDTO> actionDTO =
                new ActionEndpointDTO<HttpActionDTO>();

        HttpActionDTO actionDescriptor = new HttpActionDTO();
        actionDescriptor.setPath("/destination/handleRequest");

        actionDTO.setActionDescriptor(actionDescriptor);
        actionDTO.setActionName("ACTION");
        regDTO.setActionRegistrations(
                Arrays.asList(
                        new ActionRegistrationDTO<HttpActionDTO>(actionDTO,
                                                                 true)));
        //----------------------------------------------------------------------
        return httpClient.registerService(regDTO);
    }
}
