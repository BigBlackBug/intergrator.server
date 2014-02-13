package com.icl.integrator.main;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
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
//	    register(httpClient);
//	    deliver(httpClient);
		AutoDetectionRegistrationDTO<TestClass> dto = new AutoDetectionRegistrationDTO<>();
		dto.setDeliveryType(DeliveryType.INCIDENT);
		TestClass testClass = new TestClass();
		testClass.setString("AYAYAY");
		dto.setReferenceObject(testClass);
		RegistrationDestinationDescriptor rdd =
				new RegistrationDestinationDescriptor(new ServiceDestinationDescriptor("LOCALHOST","ACTION",EndpointType.HTTP),false);
		dto.setDestinationDescriptors(Arrays.asList(rdd));
		httpClient.registerAutoDetection(dto);
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

		deliveryDTO.setResponseHandlerDescriptor(
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
		deliveryDTO.setRequestData(new RequestDataDTO(DeliveryType.UNDEFINED,
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

	private static class Nested {

		private String string;

		private Integer integer;

		private Nested() {
		}

		public Integer getInteger() {
			return integer;
		}

		public void setInteger(Integer integer) {
			this.integer = integer;
		}

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}
	}

	private static class TestClass {

		private Integer integer;

		private String string;

		private Nested nested;

		private List<Nested> nestedList;

		private TestClass() {
		}

		public Integer getInteger() {
			return integer;
		}

		public void setInteger(Integer integer) {
			this.integer = integer;
		}

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}

		public Nested getNested() {
			return nested;
		}

		public void setNested(Nested nested) {
			this.nested = nested;
		}

		public List<Nested> getNestedList() {
			return nestedList;
		}

		public void setNestedList(List<Nested> nestedList) {
			this.nestedList = nestedList;
		}
	}
}