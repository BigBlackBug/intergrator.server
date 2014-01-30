package com.icl.integrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.DestinationDescriptorDTO;
import com.icl.integrator.dto.EndpointDTO;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.HttpActionDTO;
import com.icl.integrator.dto.registration.QueueDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import com.icl.integrator.model.TaskLogEntry;
import com.icl.integrator.services.EndpointResolverService;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandler;
import com.icl.integrator.task.retryhandler.DatabaseRetryHandlerFactory;
import com.icl.integrator.util.EndpointType;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.WebApplicationContext;

import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Callable;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:integrator/src/main/webapp/WEB-INF/" +
                              "integrator-servlet.xml")
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

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }
    private QueueDTO getQueueDTO(){
        QueueDTO dto = new QueueDTO();
        dto.setUsername("USERNAME");
        dto.setPassword("PASSWORD");
        dto.setQueueName("QUEUENAME");
        return dto;
    }

    private EndpointDTO<EndpointDescriptor> getJMSDTO(){
        JMSEndpointDescriptorDTO d = new JMSEndpointDescriptorDTO();
        d.setConnectionFactory("CONNFACTORY");
        d.setJndiProperties(new HashMap<String,String>(){{
            put("a","1");
        }});
        EndpointDTO<EndpointDescriptor> dto = new EndpointDTO<EndpointDescriptor>();
        dto.setEndpointType(EndpointType.JMS);
        dto.setDescriptor(d);
        return dto;
    }

    private EndpointDTO<EndpointDescriptor> getHttpDTO(){
        HttpEndpointDescriptorDTO d = new HttpEndpointDescriptorDTO();
        d.setHost("HOST");
        d.setPort(10001);
        EndpointDTO<EndpointDescriptor> dto = new EndpointDTO<EndpointDescriptor>();
        dto.setEndpointType(EndpointType.HTTP);
        dto.setDescriptor(d);
        return dto;
    }

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testHttpDeserializer() throws Exception {
        DestinationDescriptorDTO
                serviceDTO = new DestinationDescriptorDTO();
        ActionDescriptor descriptor = new HttpActionDTO("PATH");
        serviceDTO.setActionDescriptor(descriptor);
        serviceDTO.setEndpoint(getHttpDTO());
        String s = mapper.writeValueAsString(serviceDTO);
        DestinationDescriptorDTO
                serviceDTO1 = mapper.readValue(s, DestinationDescriptorDTO.class);
        Assert.assertEquals(serviceDTO,serviceDTO1);
    }
    @Test
    public void testDeserializer() throws Exception {
        DestinationDescriptorDTO
                serviceDTO = new DestinationDescriptorDTO();
        ActionDescriptor descriptor = getQueueDTO();
        serviceDTO.setActionDescriptor(descriptor);
        serviceDTO.setEndpoint(getJMSDTO());
        String s = mapper.writeValueAsString(serviceDTO);
        DestinationDescriptorDTO
                serviceDTO1 = mapper.readValue(s, DestinationDescriptorDTO.class);
        Assert.assertEquals(serviceDTO,serviceDTO1);
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
            new TaskCreator<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT);
                }
            })
            .addExceptionHandler(new Callback<RestClientException>() {
                @Override
                public void execute(RestClientException arg) {
                    Assert.assertTrue(true);
                }
            }, RestClientException.class)
//            .addExceptionHandler(new Callback<IllegalArgumentException>() {
//                @Override
//                public void execute(IllegalArgumentException arg) {
//                    Assert.fail();
//                }
//            }, IllegalArgumentException.class)
            .create();
        runnable.run();
//        Assert.fail();
    }

    @Test
    public void testAddress() throws Exception {
        URL serviceURL = endpointResolverService.getServiceURL("TEST_SERVICE",
                                                             "test_response");
        URL expected = new URL("HTTP", "127.0.0.1",
                               8080,
                               "/api/destination");
        Assert.assertEquals(expected, serviceURL);
    }

    @Test
    public void testRegistrationDeserializer(){

    }

}
