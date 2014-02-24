package com.icl.integrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.RawDestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.HttpActionDTO;
import com.icl.integrator.dto.source.HttpEndpointDescriptorDTO;
import com.icl.integrator.services.validation.PacketValidationException;
import com.icl.integrator.services.validation.ValidationService;
import com.icl.integrator.util.EndpointType;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by BigBlackBug on 2/24/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager = "transactionManager",
                          defaultRollback = false)
@ContextConfiguration(
		locations = {"classpath:/applicationContext.xml", "classpath:/integrator-servlet.xml"})

public class ValidatorTests {

	@Autowired
	private ValidationService validationService;

	@Autowired
	private ObjectMapper mapper;

	@Test(expected = PacketValidationException.class)
	public void testValidator() throws Exception {
		HttpEndpointDescriptorDTO desr = new
				HttpEndpointDescriptorDTO("localhost", 8080);
		EndpointDTO<HttpEndpointDescriptorDTO> endpoint =
				new EndpointDTO<>(EndpointType.HTTP, desr);
		RawDestinationDescriptor dd =
				new RawDestinationDescriptor(

				);
		dd.setActionDescriptor(
				new HttpActionDTO("/ext_source/handleGetServiceList"));
		IntegratorPacket p = new IntegratorPacket(dd);
		serialize(p);
	}

	@Test
	public void testValidator3() throws Exception {
		HttpEndpointDescriptorDTO desr = new
				HttpEndpointDescriptorDTO("localhost", 8080);
		EndpointDTO<HttpEndpointDescriptorDTO> endpoint =
				new EndpointDTO<>(EndpointType.HTTP, desr);
		RawDestinationDescriptor dd =
				new RawDestinationDescriptor(
						endpoint, new HttpActionDTO("/ext_source/handleGetServiceList")
				);
		ServiceDestinationDescriptor sdd = new ServiceDestinationDescriptor("a",null,EndpointType.HTTP);
		IntegratorPacket p = new IntegratorPacket();
		p.setMethod(IntegratorMethod.ADD_ACTION);
		p.setResponseHandlerDescriptor(dd);

		DeliveryDTO deliveryDTO = new DeliveryDTO();
		deliveryDTO.setAction("ACTION");
		deliveryDTO.setDestinations(null);
		deliveryDTO.setRequestData(new RequestDataDTO(DeliveryType.INCIDENT,desr));
		serialize(p);
		p.setResponseHandlerDescriptor(sdd);
		try{
			serialize(p);
		}catch(PacketValidationException ex){
			return;
		}
		Assert.fail();
	}

	@Test
	public void testValidator2() throws Exception {
		HttpEndpointDescriptorDTO desr = new
				HttpEndpointDescriptorDTO("localhost", 8080);
		EndpointDTO<HttpEndpointDescriptorDTO> endpoint =
				new EndpointDTO<>(EndpointType.HTTP, desr);
		RawDestinationDescriptor dd =
				new RawDestinationDescriptor(
						endpoint, new HttpActionDTO("/ext_source/handleGetServiceList")
				);
		IntegratorPacket p = new IntegratorPacket();
		p.setMethod(null);
		p.setPacket(null);
		p.setResponseHandlerDescriptor(null);
		serialize(p);
	}

	private void serialize(Object p) throws Exception {
		String json = new ObjectMapper().writeValueAsString(p);
		validationService.validateIntegratorPacket(json);
		json = mapper.writeValueAsString(p);
		validationService.validateIntegratorPacket(json);
	}
}
