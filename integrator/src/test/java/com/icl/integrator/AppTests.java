package com.icl.integrator;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.FullServiceDTO;
import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.dto.util.EndpointType;
import com.icl.integrator.model.*;
import com.icl.integrator.services.EndpointResolverService;
import com.icl.integrator.services.JsonMatcher;
import com.icl.integrator.services.PersistenceService;
import com.icl.integrator.services.validation.ValidationService;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandler;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandlerFactory;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.Callable;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "transactionManager",
                          defaultRollback = false)
@ContextConfiguration(
		locations = {"classpath:/applicationContext.xml", "classpath:/integrator-servlet.xml"})
public class AppTests {

	private static Log logger =
			LogFactory.getLog(AppTests.class);

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	protected EndpointResolverService endpointResolverService;

	@Autowired
	protected DatabaseRetryHandlerFactory factory;

	@PersistenceContext
	EntityManager em;

	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PersistenceService persistenceService;

	@Autowired
	private JsonMatcher jsonMatcher;

	@Autowired
	private ValidationService validationService;

	@Before
	@Transactional
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).build();
//        HttpServiceEndpoint ep = new HttpServiceEndpoint();
//        ep.setServiceURL("URL");
//        ep.setServicePort(123);
//        ep.setServiceName("SERNAME");
//        HttpAction a = new HttpAction();
//        a.setActionURL("AURL");
//        a.setEndpoint(ep);
//        a.setActionName("ANAME");
//        ep.addAction(a);
//        persistenceService.persist(ep);
//
//        DeliveryDTO dto = getDelvieryDTO();

//        DeliveryPacket dp = new DeliveryPacket();
//
//        Delivery delivery = new Delivery();
//        delivery.setAction(a);
//        delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
//        delivery.setEndpoint(ep);
//        delivery.setDeliveryPacket(dp);
//
//        dp.setDeliveries(new HashSet<>(Arrays.asList(delivery)));
////		dp.setAction(dto.getAction());
//        dp.setDeliveryData(mapper.writeValueAsString(dto.getRequestData()));
//        dp.setRequestDate(new Date());
////		dp.setDestinations(Arrays.<AbstractEndpointEntity>asList(ep));
//        persistenceService.persist(dp);
	}

	@Test
	public void testPAGeneral() throws Exception {
		TestClass reference = new TestClass();
		reference.string = "STRING";
		JsonNode referenceJson = mapper.valueToTree(reference);

		TestClass data = new TestClass();
		data.integer = 5;
		data.string = "STRING";
		data.nested = new Nested();
		JsonNode dataJson = mapper.valueToTree(data);

		Assert.assertTrue(jsonMatcher.matches(dataJson, referenceJson));

		data.string = "!F#";
		dataJson = mapper.valueToTree(data);
		Assert.assertFalse(jsonMatcher.matches(dataJson, referenceJson));
	}

	@Test
	public void testPANested() throws Exception {
		TestClass reference = new TestClass();
		reference.integer = 415;

		Nested nested = new Nested();
		nested.integer = 100;
		reference.nested = nested;
		JsonNode referenceJson = mapper.valueToTree(reference);

		TestClass data = new TestClass();
		data.integer = 415;
		data.string = "STRING";
		Nested nested2 = new Nested();
		nested2.integer = 100;
		nested2.string = "WHATEVER";
		data.nested = nested2;
		JsonNode dataJson = mapper.valueToTree(data);

		Assert.assertTrue(jsonMatcher.matches(dataJson, referenceJson));

		data.integer = 150;
		dataJson = mapper.valueToTree(data);

		Assert.assertFalse(jsonMatcher.matches(dataJson, referenceJson));
	}

	@Test
	public void testPAList() throws Exception {
		Nested nested = new Nested();
		nested.integer = 100;

		TestClass reference = new TestClass();
		reference.string = "STRING";
		reference.nestedList = new ArrayList<>();
		reference.nestedList.add(nested);
		JsonNode referenceJson = mapper.valueToTree(reference);

		TestClass data = new TestClass();
		data.integer = 500;
		data.string = "STRING";
		data.nested = new Nested();
		data.nestedList = new ArrayList<>();
		data.nestedList.add(nested);
		JsonNode dataJson = mapper.valueToTree(data);

		Assert.assertTrue(jsonMatcher.matches(dataJson, referenceJson));

		Nested nested2 = new Nested();
		nested2.integer = 100;
		nested2.string = "WHATEVER";
		data.nestedList.add(nested2);

		dataJson = mapper.valueToTree(data);

		Assert.assertFalse(jsonMatcher.matches(dataJson, referenceJson));
	}

	@Test
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Ignore
	public void testPers() throws Exception {
		List<AbstractEndpointEntity> resultList =
				em.createQuery("select e from AbstractEndpointEntity e",
				               AbstractEndpointEntity.class).getResultList();
		AbstractEndpointEntity abstractEndpointEntity = resultList.get(0);
		if (abstractEndpointEntity.getType() == EndpointType.HTTP) {
			HttpServiceEndpoint ed =
					(HttpServiceEndpoint) abstractEndpointEntity;
			Set<AbstractActionEntity> actions = ed.getActions();
			AbstractActionEntity actionEntity = actions.iterator().next();
			if (actionEntity.getType() == EndpointType.HTTP) {
				HttpAction a = (HttpAction) actionEntity;
			}
			return;
		}
		Assert.fail();
	}

	@After
	@Transactional
	public void trunc() throws Exception {
//		em.createQuery("delete from AbstractEndpointEntity").executeUpdate();
//		em.createQuery("delete from AbstractActionEntity").executeUpdate();
//		em.createQuery("delete from DeliveryPacket").executeUpdate();
	}

//	private DeliveryDTO getDelvieryDTO() {
//		DeliveryDTO deliveryDTO = new DeliveryDTO();
//
//		HttpEndpointDescriptorDTO desr = new
//				HttpEndpointDescriptorDTO("192.168.84.142", 8080);
//		EndpointDTO<HttpEndpointDescriptorDTO> endpoint =
//				new EndpointDTO<>(EndpointType.HTTP, desr);
//
////        deliveryDTO.setTargetResponseHandlerDescriptor(
////                new DestinationDescriptorDTO(
////                        endpoint,
////                        new HttpActionDTO("/source/handleResponseFromTarget")
////                ));
//		HashMap<String, String> map = new HashMap<>();
//		map.put("java.naming.provider.url", "tcp://localhost:61616");
//		map.put("java.naming.factory.initial", "org.apache.activemq.jndi" +
//				".ActiveMQInitialContextFactory");
//		RawDestinationDescriptor targetResponseHandler =
//				new RawDestinationDescriptor();
//		targetResponseHandler.setEndpoint(
//				new EndpointDTO<>(EndpointType.JMS, new
//						JMSEndpointDescriptorDTO("ConnectionFactory", map)
//				));
//		targetResponseHandler.setActionDescriptor(new QueueDTO
//				                                          ("SourceQueue"));
//		deliveryDTO.setResponseHandlerDescriptor(targetResponseHandler);
//		RawDestinationDescriptor
//				deliveryResponseHandler = new RawDestinationDescriptor();
//
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
//		deliveryResponseHandler
//				.setEndpoint(endpoint);
//		deliveryResponseHandler.setActionDescriptor(
//				new HttpActionDTO("/ext_source/handleDeliveryResponse"));
//		deliveryDTO.setAction("ACTION");
//		deliveryDTO.setRequestData(new RequestDataDTO(DeliveryPacketType.UNDEFINED,
//		                                              new HashMap<String, Object>() {{
//			                                              put("a", "b");
//		                                              }}));
//		ServiceDTO destination = new ServiceDTO(
//				"NEW_SERVICE", EndpointType.HTTP);
//		deliveryDTO.setDestinations(Arrays.asList(destination));
//		return deliveryDTO;
//	}

	private QueueDTO getQueueDTO() {
		QueueDTO dto = new QueueDTO("NAME",ActionMethod.HANDLE_ADD_ACTION);
		return dto;
	}

	private EndpointDescriptor getJMSDTO() {
        EndpointDescriptor d = new JMSEndpointDescriptorDTO("CONNFACTORY",new HashMap<String, String>() {{
            put("a", "1");
        }});
		return d;
	}

	private EndpointDescriptor getHttpDTO() {
        return new HttpEndpointDescriptorDTO("host",1001);
	}

	@Test
	@Ignore
	public void testMvc() throws Exception {
		RawDestinationDescriptor targetResponseHandler =
				new RawDestinationDescriptor(new
                        JMSEndpointDescriptorDTO("ConnectionFactory", null)
                ,new QueueDTO
                        ("SourceQueue",ActionMethod.HANDLE_GET_SERVER_LIST));
		IntegratorPacket<Void, DestinationDescriptor>
				packet =
				new IntegratorPacket<Void, DestinationDescriptor>(
						targetResponseHandler);

		mockMvc.perform(post("/integrator/getServiceList").contentType(
				MediaType.APPLICATION_JSON).content(mapper.writeValueAsString
				(packet))).andExpect(status().is(200));
	}

	@Test
	public void testRawDDDeserializer() throws Exception {
		RawDestinationDescriptor targetResponseHandler =
				new RawDestinationDescriptor(new
                        JMSEndpointDescriptorDTO("ConnectionFactory",
                                                 Collections.<String,
                                                         String>emptyMap())
                ,new QueueDTO
                        ("SourceQueue",ActionMethod.HANDLE_AUTO_DETECTION_REGISTRATION_RESPONSE));
		IntegratorPacket<Void, DestinationDescriptor>
				packet =
				new IntegratorPacket<Void, DestinationDescriptor>(
						targetResponseHandler);
		String expected = mapper.writeValueAsString(packet);
		IntegratorPacket integratorPacket =
				mapper.readValue(expected, IntegratorPacket.class);
		Assert.assertEquals(packet, integratorPacket);
	}

	@Test
	public void testServiceDDDeserializer() throws Exception {
		IntegratorPacket<Void, DestinationDescriptor>
				packet =
				new IntegratorPacket<Void, DestinationDescriptor>(
						new ServiceDestinationDescriptor(
								"ser", "actuin", EndpointType.HTTP));
		String expected = mapper.writeValueAsString(packet);
		IntegratorPacket integratorPacket =
				mapper.readValue(expected, IntegratorPacket.class);
		Assert.assertEquals(packet, integratorPacket);
	}

    @Test
    public void testServicDTOeserializer() throws Exception {
        ServiceDTO s = new ServiceDTO("NAME",EndpointType.HTTP);
        String expected = mapper.writeValueAsString(s);
        ServiceDTO integratorPacket =
                mapper.readValue(expected, ServiceDTO.class);
        Assert.assertEquals(s, integratorPacket);
    }

	@Test
	public void testRegDeserializer() throws Exception {
		//----------------------------------------------------------------------
		HttpEndpointDescriptorDTO descr = new HttpEndpointDescriptorDTO("192.168.84.142",8080);

		//----------------------------------------------------------------------
		HttpActionDTO actionDescriptor = new HttpActionDTO("/destination/handleDelivery",
		                                                   ActionMethod.HANDLE_DELIVERY);

        ActionEndpointDTO<HttpActionDTO> actionDTO = new ActionEndpointDTO<>("ACTION",actionDescriptor);
        List<ActionRegistrationDTO<HttpActionDTO>> actionRegistrationDTOs =
                Arrays.asList(new ActionRegistrationDTO<>(actionDTO, true));
        DeliverySettingsDTO deliverySettingsDTO = new DeliverySettingsDTO(100, 500);
        TargetRegistrationDTO<HttpActionDTO> expected =
                new TargetRegistrationDTO<>("SERVICE",descr,deliverySettingsDTO,
                                            actionRegistrationDTOs);
		String sstring = mapper.writeValueAsString(expected);
		TargetRegistrationDTO result =
				mapper.readValue(sstring, TargetRegistrationDTO.class);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testHttpDeserializer() throws Exception {

		ActionDescriptor descriptor = new HttpActionDTO("PATH",ActionMethod.HANDLE_ADD_ACTION);
        RawDestinationDescriptor
                serviceDTO = new RawDestinationDescriptor(getHttpDTO(),descriptor);
		String s = mapper.writeValueAsString(serviceDTO);
		RawDestinationDescriptor
				serviceDTO1 =
				mapper.readValue(s, RawDestinationDescriptor.class);
		Assert.assertEquals(serviceDTO, serviceDTO1);
	}
    @Test
    public void testHttpFSERVDeserializer() throws Exception {

        FullServiceDTO<HttpActionDTO> serviceDTO = new FullServiceDTO<>();
        serviceDTO.setServiceName("SAD");
        serviceDTO.setEndpoint(new HttpEndpointDescriptorDTO("host",65468));
        ActionEndpointDTO<HttpActionDTO> actionEndpointDTO =
                new ActionEndpointDTO<>("actionname",new HttpActionDTO("path",
                                                                      ActionMethod.HANDLE_ADD_ACTION));
        serviceDTO.setActions(Arrays.asList(actionEndpointDTO));
        String s = mapper.writeValueAsString(serviceDTO);
        FullServiceDTO
                serviceDTO1 =
                mapper.readValue(s, new TypeReference<FullServiceDTO<ActionDescriptor>>(){});
        Assert.assertEquals(serviceDTO, serviceDTO1);
    }

	@Test
	public void testaddActionDes() throws Exception {
        ActionRegistrationDTO<ActionDescriptor> a =
                new ActionRegistrationDTO<>(
                        new ActionEndpointDTO<ActionDescriptor>(
                                "SOURCE_SERVICE2_ACTION", new HttpActionDTO(
                                "/ext_source2/handleResponseFromTarget",
                                ActionMethod.HANDLE_RESPONSE_FROM_TARGET)),
                        false);
        AddActionDTO<ActionDescriptor> dto = new AddActionDTO<>(
                new ServiceDTO("SERVICE2",EndpointType.HTTP),a);
		String s = mapper.writeValueAsString(dto);
		AddActionDTO
				serviceDTO1 =
				mapper.readValue(s, AddActionDTO.class);
		Assert.assertEquals(dto, serviceDTO1);
	}

	@Test
	public void testDeserializer() throws Exception {
        RawDestinationDescriptor
                serviceDTO = new RawDestinationDescriptor(getJMSDTO(),getQueueDTO());
		String s = mapper.writeValueAsString(serviceDTO);
		RawDestinationDescriptor
				serviceDTO1 =
				mapper.readValue(s, RawDestinationDescriptor.class);
		Assert.assertEquals(serviceDTO, serviceDTO1);
	}

    @Test
    public void testDeserializerSericeDTO() throws Exception {
        ServiceDTO serviceDTO= new ServiceDTO("ser",EndpointType.HTTP);
        String s = mapper.writeValueAsString(serviceDTO);
        ServiceDTO
                serviceDTO1 =
                mapper.readValue(s, ServiceDTO.class);
        Assert.assertEquals(serviceDTO, serviceDTO1);
    }

	@Test
	public void testHandler() throws Exception {
		DatabaseRetryHandler handler = factory.createHandler();
		handler.setLogEntry(new TaskLogEntry("OLOLO2"));
		handler.call();
		Assert.assertTrue(true);
	}

	//    @Test
//    public void simple() throws Exception {
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("hello"));
//    }
	@Test
	public void testEx() throws Exception {
		Runnable runnable =
				new TaskCreator<>(new Callable<String>() {
					@Override
					public String call() throws Exception {
						throw new HttpClientErrorException(
								HttpStatus.GATEWAY_TIMEOUT);
					}
				})
						.addExceptionHandler(
								new Callback<RestClientException>() {
									@Override
									public void execute(
											RestClientException arg) {
										Assert.assertTrue(true);
									}
								}, RestClientException.class)
						.addExceptionHandler(new Callback<IllegalArgumentException>() {
							@Override
							public void execute(IllegalArgumentException arg) {
								Assert.fail();
							}
						}, IllegalArgumentException.class)
						.create();
		runnable.run();
//        Assert.fail();
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
