package com.icl.integrator;

import com.icl.integrator.model.TaskLogEntry;
import com.icl.integrator.task.Callback;
import com.icl.integrator.task.DatabaseRetryHandler;
import com.icl.integrator.task.DatabaseRetryHandlerFactory;
import com.icl.integrator.task.TaskCreator;
import com.icl.integrator.util.AddressMappingService;
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
import java.util.concurrent.Callable;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class AppTests {

    private static Log logger =
            LogFactory.getLog(AppTests.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected AddressMappingService addressMappingService;

    @Autowired
    protected DatabaseRetryHandlerFactory factory;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
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
        URL serviceURL = addressMappingService.getServiceURL("TEST_SERVICE",
                                                             "test_response");
        URL expected = new URL("HTTP", "127.0.0.1",
                               8080,
                               "/api/destination");
        Assert.assertEquals(expected, serviceURL);
    }
}
