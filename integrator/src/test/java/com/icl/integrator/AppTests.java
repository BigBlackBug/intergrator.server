package com.icl.integrator;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.model.*;
import com.icl.integrator.services.EndpointResolverService;
import com.icl.integrator.services.PersistenceService;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandler;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandlerFactory;
import com.icl.integrator.util.EndpointType;
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
@ContextConfiguration(locations = {"classpath:/integrator-servlet.xml"})
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

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PersistenceService persistenceService;

    @Before
    @Transactional
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(this.wac).build();
        HttpServiceEndpoint ep = new HttpServiceEndpoint();
        ep.setServiceURL("URL");
        ep.setServicePort(123);
        ep.setServiceName("SERNAME");
        HttpAction a = new HttpAction();
        a.setActionURL("AURL");
        a.setEndpoint(ep);
        a.setActionName("ANAME");
        ep.addAction(a);
        persistenceService.persist(ep);

        DeliveryDTO dto = getDelvieryDTO();

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

    @PersistenceContext
    EntityManager em;
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

    private DeliveryDTO getDelvieryDTO() {
        DeliveryDTO deliveryDTO = new DeliveryDTO();

        HttpEndpointDescriptorDTO desr = new
                HttpEndpointDescriptorDTO("192.168.84.142", 8080);
        EndpointDTO<HttpEndpointDescriptorDTO> endpoint =
                new EndpointDTO<>(EndpointType.HTTP, desr);

//        deliveryDTO.setTargetResponseHandlerDescriptor(
//                new DestinationDescriptorDTO(
//                        endpoint,
//                        new HttpActionDTO("/source/handleResponseFromTarget")
//                ));
        HashMap<String, String> map = new HashMap<>();
        map.put("java.naming.provider.url", "tcp://localhost:61616");
        map.put("java.naming.factory.initial", "org.apache.activemq.jndi" +
                ".ActiveMQInitialContextFactory");
        RawDestinationDescriptor targetResponseHandler =
                new RawDestinationDescriptor();
        targetResponseHandler.setEndpoint(
                new EndpointDTO<>(EndpointType.JMS, new
                        JMSEndpointDescriptorDTO("ConnectionFactory", map)
                ));
        targetResponseHandler.setActionDescriptor(new QueueDTO
                                                          ("SourceQueue"));
        deliveryDTO.setResponseHandlerDescriptor(targetResponseHandler);
        RawDestinationDescriptor
                deliveryResponseHandler = new RawDestinationDescriptor();


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
        deliveryResponseHandler
                .setEndpoint(endpoint);
        deliveryResponseHandler.setActionDescriptor(
                new HttpActionDTO("/ext_source/handleDeliveryResponse"));
        deliveryDTO.setAction("ACTION");
        deliveryDTO.setRequestData(new RequestDataDTO(PacketType.UNDEFINED,
                new HashMap<String, Object>() {{
                    put("a", "b");
                }}));
	    ServiceDTO destination = new ServiceDTO(
                "NEW_SERVICE", EndpointType.HTTP);
        deliveryDTO.setDestinations(Arrays.asList(destination));
        return deliveryDTO;
    }

    private QueueDTO getQueueDTO() {
        QueueDTO dto = new QueueDTO();
        dto.setUsername("USERNAME");
        dto.setPassword("PASSWORD");
        dto.setQueueName("QUEUENAME");
        return dto;
    }

    private EndpointDTO<EndpointDescriptor> getJMSDTO() {
        JMSEndpointDescriptorDTO d = new JMSEndpointDescriptorDTO();
        d.setConnectionFactory("CONNFACTORY");
        d.setJndiProperties(new HashMap<String, String>() {{
            put("a", "1");
        }});
        EndpointDTO<EndpointDescriptor> dto = new EndpointDTO<>();
        dto.setEndpointType(EndpointType.JMS);
        dto.setDescriptor(d);
        return dto;
    }

    private EndpointDTO<EndpointDescriptor> getHttpDTO() {
        HttpEndpointDescriptorDTO d = new HttpEndpointDescriptorDTO();
        d.setHost("HOST");
        d.setPort(10001);
        EndpointDTO<EndpointDescriptor> dto = new EndpointDTO<>();
        dto.setEndpointType(EndpointType.HTTP);
        dto.setDescriptor(d);
        return dto;
    }

    @Test
    @Ignore
    public void testMvc() throws Exception {
        RawDestinationDescriptor targetResponseHandler =
                new RawDestinationDescriptor();
        targetResponseHandler.setEndpoint(
                new EndpointDTO<>(EndpointType.JMS, new
                        JMSEndpointDescriptorDTO("ConnectionFactory", null)
                ));
        targetResponseHandler.setActionDescriptor(new QueueDTO
                                                          ("SourceQueue"));
        IntegratorPacket<Void, DestinationDescriptor>
                packet =
                new IntegratorPacket<Void, DestinationDescriptor>(
                        targetResponseHandler);

        mockMvc.perform(post("/integrator/getServiceList").contentType(
                MediaType.APPLICATION_JSON).content(mapper.writeValueAsString
                (packet))).andExpect(status().is(200));
    }

    @Test
    @Ignore
    public void testPingMVC() throws Exception {
        RawDestinationDescriptor targetResponseHandler =
                new RawDestinationDescriptor();
        targetResponseHandler.setEndpoint(
                new EndpointDTO<>(EndpointType.JMS, new
                        JMSEndpointDescriptorDTO("ConnectionFactory", null)
                ));
        targetResponseHandler.setActionDescriptor(new QueueDTO
                                                          ("SourceQueue"));
        IntegratorPacket<PingDTO, DestinationDescriptor>
                packet =
                new IntegratorPacket<PingDTO, DestinationDescriptor>(
                        new RawDestinationDescriptor());
        mockMvc.perform(post("/integrator/ping").contentType(
                MediaType.APPLICATION_JSON).content(mapper.writeValueAsString
                (packet))).andExpect(status().is(200));
    }

    @Test
    public void testRawDDDeserializer() throws Exception {
        RawDestinationDescriptor targetResponseHandler =
                new RawDestinationDescriptor();
        targetResponseHandler.setEndpoint(
                new EndpointDTO<>(EndpointType.JMS, new
                        JMSEndpointDescriptorDTO("ConnectionFactory",
                                                 Collections.<String,
                                                         String>emptyMap())
                ));
        targetResponseHandler.setActionDescriptor(new QueueDTO
                                                          ("SourceQueue"));
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
                                "ser", EndpointType.HTTP, "actuin"));
        String expected = mapper.writeValueAsString(packet);
        IntegratorPacket integratorPacket =
                mapper.readValue(expected, IntegratorPacket.class);
        Assert.assertEquals(packet, integratorPacket);
    }

    @Test
    public void testRegDeserializer() throws Exception {
        TargetRegistrationDTO<HttpActionDTO> expected =
                new TargetRegistrationDTO<>();
        expected.setServiceName("NEW_SERVICE");
        //----------------------------------------------------------------------
        EndpointDTO<HttpEndpointDescriptorDTO>
                endpointDTO = new EndpointDTO<>();
        endpointDTO.setEndpointType(EndpointType.HTTP);

        HttpEndpointDescriptorDTO descr = new HttpEndpointDescriptorDTO();
        descr.setHost("192.168.84.142");
        descr.setPort(8080);
        endpointDTO.setDescriptor(descr);

        expected.setEndpoint(endpointDTO);
        //----------------------------------------------------------------------
        ActionEndpointDTO<HttpActionDTO> actionDTO = new ActionEndpointDTO<>();

        HttpActionDTO actionDescriptor = new HttpActionDTO();
        actionDescriptor.setPath("/destination/handleRequest");

        actionDTO.setActionDescriptor(actionDescriptor);
        actionDTO.setActionName("ACTION");
        expected.setActionRegistrations(
                Arrays.asList(new ActionRegistrationDTO<>(actionDTO, true)));

        String sstring = mapper.writeValueAsString(expected);
        TargetRegistrationDTO result =
                mapper.readValue(sstring, TargetRegistrationDTO.class);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testHttpDeserializer() throws Exception {
        RawDestinationDescriptor
                serviceDTO = new RawDestinationDescriptor();
        ActionDescriptor descriptor = new HttpActionDTO("PATH");
        serviceDTO.setActionDescriptor(descriptor);
        serviceDTO.setEndpoint(getHttpDTO());
        String s = mapper.writeValueAsString(serviceDTO);
        RawDestinationDescriptor
                serviceDTO1 =
                mapper.readValue(s, RawDestinationDescriptor.class);
        Assert.assertEquals(serviceDTO, serviceDTO1);
    }

    @Test
    public void testDeserializer() throws Exception {
        RawDestinationDescriptor
                serviceDTO = new RawDestinationDescriptor();
        ActionDescriptor descriptor = getQueueDTO();
        serviceDTO.setActionDescriptor(descriptor);
        serviceDTO.setEndpoint(getJMSDTO());
        String s = mapper.writeValueAsString(serviceDTO);
        RawDestinationDescriptor
                serviceDTO1 =
                mapper.readValue(s, RawDestinationDescriptor.class);
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

}
