package com.icl.integrator;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.registration.ActionMethod;
import com.icl.integrator.dto.registration.QueueDTO;
import com.icl.integrator.dto.source.JMSEndpointDescriptorDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "transactionManager",
                          defaultRollback = false)
@ContextConfiguration(
		locations = {"classpath:/applicationContext.xml", "classpath:/integrator-servlet.xml"})
public class MvcTests {

	private static Log logger = LogFactory.getLog(MvcTests.class);

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	protected WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Qualifier("integratorService")
	@Autowired
	private IntegratorAPI service;

	@Before
	@Transactional
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(this.wac).build();
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
	public void testService() {
		service.ping(new IntegratorPacket<Void, DestinationDescriptor>());
	}

}
