package com.icl.integrator.main;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.registration.ActionEndpointDTO;
import com.icl.integrator.dto.registration.ActionRegistrationDTO;
import com.icl.integrator.dto.registration.HttpActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.httpclient.IntegratorHttpClient;
import com.icl.integrator.util.EndpointType;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String args[])
		    throws MalformedURLException, URISyntaxException {
//        IntegratorHttpClient httpClient = new IntegratorHttpClient
//                ("192.168.83.91", "integrator", 18080);
        IntegratorHttpClient httpClient = new IntegratorHttpClient
                ("localhost", 8080);

	    HttpEndpointDescriptorDTO desr = new
			    HttpEndpointDescriptorDTO("localhost", 8080);
	    EndpointDTO<HttpEndpointDescriptorDTO> endpoint =
			    new EndpointDTO<>(EndpointType.HTTP, desr);

	    RawDestinationDescriptor dd =
			    new RawDestinationDescriptor(
					    endpoint,
					    new HttpActionDTO("/ext_source/handleGetServiceList")
			    );
	    ResponseDTO<List<ServiceDTO>> serviceList = httpClient.getServiceList(
			    new IntegratorPacket<Void, DestinationDescriptor>(dd));
	    System.out.println(serviceList.getResponse().getResponseValue());
//	    RestTemplate restTemplate = new RestTemplate();
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
//	    HttpEntity<String> entity = new HttpEntity<>("{\"QWEQWE\":\"asdasd\"}",headers);
//	    ResponseDTO aVoid = restTemplate.postForObject(
//			    new URL("HTTP", "localhost", 8080, "/integrator/test").toURI(),
//			    entity, ResponseDTO.class);
//	    ResponseEntity<ResponseDTO> voidResponseEntity = restTemplate.postForEntity(
//			    new URL("HTTP", "localhost", 8080, "/integrator/test").toURI(),
//			    entity, ResponseDTO.class);
//	    System.out.println(voidResponseEntity.getBody());


//        ResponseDTO<Map<String, ResponseDTO<Void>>> register =
//                register(httpClient);
//        System.out.println(register);
//        deliver(httpClient);
//        ResponseDTO<List<ServiceDTO>> serviceList = httpClient.getServiceList
//                (new IntegratorPacket<Void, DestinationDescriptor>(
//                        new ServiceDestinationDescriptor(
//                                "ser", EndpointType.HTTP, "actuin")));
//        RawDestinationDescriptor targetResponseHandler =
//                new RawDestinationDescriptor();
//        targetResponseHandler.setEndpoint(
//                new EndpointDTO<>(EndpointType.JMS, new
//                        JMSEndpointDescriptorDTO("ConnectionFactory", null)
//                ));
//        targetResponseHandler.setActionDescriptor(new QueueDTO
//                                                          ("SourceQueue"));
//        httpClient.getServiceList(
//                new IntegratorPacket<Void, DestinationDescriptor>(
//                        targetResponseHandler));
//        System.out.println(serviceList);


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

    public static ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
            IntegratorHttpClient httpClient) {
        DeliveryDTO deliveryDTO = new DeliveryDTO();

        HttpEndpointDescriptorDTO desr = new
                HttpEndpointDescriptorDTO("localhost", 8080);
        EndpointDTO<HttpEndpointDescriptorDTO> endpoint =
                new EndpointDTO<>(EndpointType.HTTP, desr);

        deliveryDTO.setTargetResponseHandlerDescriptor(
                new RawDestinationDescriptor(
                        endpoint,
                        new HttpActionDTO("/source/handleResponseFromTarget")
                ));
//        HashMap<String, String> map = new HashMap<>();
//        map.put("java.naming.provider.url", "tcp://localhost:61616");
//        map.put("java.naming.factory.initial", "org.apache.activemq.jndi" +
//                ".ActiveMQInitialContextFactory");
//        RawDestinationDescriptor targetResponseHandler =
//                new RawDestinationDescriptor();
//        targetResponseHandler.setEndpoint(
//                new EndpointDTO<>(EndpointType.JMS, new
//                        JMSEndpointDescriptorDTO("ConnectionFactory", map)
//                ));
//        targetResponseHandler.setActionDescriptor(new QueueDTO
//                                                          ("SourceQueue"));
//        deliveryDTO.setTargetResponseHandlerDescriptor(targetResponseHandler);
//        RawDestinationDescriptor
//                deliveryResponseHandler = new RawDestinationDescriptor();


//        HashMap<String, String> map = new HashMap<>();
//        map.put("java.naming.provider.url", "tcp://localhost:61616");
//        map.put("java.naming.factory.initial", "org.apache.activemq.jndi" +
//                ".ActiveMQInitialContextFactory");
//        deliveryResponseHandler.setEndpoint(
//                new EndpointDTO<>(EndpointType.JMS, new
//                        JMSEndpointDescriptorDTO("ConnectionFactory", map)
//                ));
//        deliveryResponseHandler.setActionDescriptor(new QueueDTO
//                                                            ("SourceQueue"));
//        deliveryResponseHandler
//                .setEndpoint(endpoint);
//        deliveryResponseHandler.setActionDescriptor(
//                new HttpActionDTO("/ext_source/handleDeliveryResponse"));
        deliveryDTO.setAction("ACTION");
        deliveryDTO.setRequestData(new RequestDataDTO(
                new HashMap<String, Object>() {{
                    put("a", "b");
                }}));
	    ServiceDTO destination = new ServiceDTO(
                "LOCALHOST", EndpointType.HTTP);
        deliveryDTO.setDestinations(Arrays.asList(destination));
        return httpClient.deliver(
                new IntegratorPacket<>(deliveryDTO));
    }

    public static ResponseDTO<Map<String, ResponseDTO<Void>>> register(
            IntegratorHttpClient httpClient) {
        TargetRegistrationDTO<HttpActionDTO> regDTO =
                new TargetRegistrationDTO<>();
        regDTO.setServiceName("LOCALHOST");
        //----------------------------------------------------------------------
        EndpointDTO<HttpEndpointDescriptorDTO>
                endpointDTO = new EndpointDTO<>();
        endpointDTO.setEndpointType(EndpointType.HTTP);

        HttpEndpointDescriptorDTO descr = new HttpEndpointDescriptorDTO();
        descr.setHost("localhost");
        descr.setPort(8080);
        endpointDTO.setDescriptor(descr);

        regDTO.setEndpoint(endpointDTO);
        //----------------------------------------------------------------------
        ActionEndpointDTO<HttpActionDTO> actionDTO = new ActionEndpointDTO<>();

        HttpActionDTO actionDescriptor = new HttpActionDTO();
        actionDescriptor.setPath("/destination/handleRequest");

        actionDTO.setActionDescriptor(actionDescriptor);
        actionDTO.setActionName("ACTION");
        regDTO.setActionRegistrations(
                Arrays.asList(new ActionRegistrationDTO<>(actionDTO, true)));
        //----------------------------------------------------------------------
        return httpClient.registerService(regDTO);
    }
}