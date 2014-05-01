package com.icl.integrator.deserializer;

import com.fasterxml.jackson.core.type.TypeReference;
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
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class DeserializerTests {

    private IntegratorObjectMapper mapper;

    @Before
    public void init() {
        this.mapper = new IntegratorObjectMapper();
    }

    @Test
    public void testRawDDDeserializer() throws Exception {
        JMSEndpointDescriptorDTO jmsEndpointDescriptorDTO = new
                JMSEndpointDescriptorDTO("ConnectionFactory",
                                         Collections.<String, String>emptyMap());
        RawDestinationDescriptor targetResponseHandler =
                new RawDestinationDescriptor(jmsEndpointDescriptorDTO
                        , new QueueDTO("SourceQueue",
                                       ActionMethod.HANDLE_AUTO_DETECTION_REGISTRATION_RESPONSE));
        IntegratorPacket<Void, DestinationDescriptor>
                packet =
                new IntegratorPacket<Void, DestinationDescriptor>(
                        targetResponseHandler);
        String expected = mapper.writeValueAsString(packet);
        IntegratorPacket integratorPacket = mapper.readValue(expected, IntegratorPacket.class);
        Assert.assertEquals(packet, integratorPacket);
    }

    @Test
    public void testServiceDDDeserializer() throws Exception {
        IntegratorPacket<Void, DestinationDescriptor>
                packet =
                new IntegratorPacket<Void, DestinationDescriptor>(
                        new ServiceDestinationDescriptor(
                                "ser", "actuin", EndpointType.HTTP)
                );
        String expected = mapper.writeValueAsString(packet);
        IntegratorPacket integratorPacket = mapper.readValue(expected, IntegratorPacket.class);
        Assert.assertEquals(packet, integratorPacket);
    }

    @Test
    public void testServiceDTODes() throws Exception {
        ServiceDTO s = new ServiceDTO("NAME", EndpointType.HTTP,"creator");
        String expected = mapper.writeValueAsString(s);
        ServiceDTO integratorPacket = mapper.readValue(expected, ServiceDTO.class);
        Assert.assertEquals(s, integratorPacket);
    }

    @Test
    public void testRegDeserializer() throws Exception {
        //----------------------------------------------------------------------
        HttpEndpointDescriptorDTO descr = new HttpEndpointDescriptorDTO("192.168.84.142", 8080);

        //----------------------------------------------------------------------
        HttpActionDTO actionDescriptor = new HttpActionDTO("/destination/handleDelivery",
                                                           ActionMethod.HANDLE_DELIVERY);

        ActionEndpointDTO<HttpActionDTO> actionDTO =
                new ActionEndpointDTO<>("ACTION", actionDescriptor);
        List<ActionRegistrationDTO<HttpActionDTO>> actionRegistrationDTOs =
                Arrays.asList(new ActionRegistrationDTO<>(actionDTO, true));
        DeliverySettingsDTO deliverySettingsDTO = new DeliverySettingsDTO(100, 500);
        TargetRegistrationDTO<HttpActionDTO> expected =
                new TargetRegistrationDTO<>("SERVICE", descr, deliverySettingsDTO,
                                            actionRegistrationDTOs);
        String sstring = mapper.writeValueAsString(expected);
        TargetRegistrationDTO result =
                mapper.readValue(sstring, TargetRegistrationDTO.class);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testHttpDeserializer() throws Exception {

        ActionDescriptor descriptor = new HttpActionDTO("PATH", ActionMethod.HANDLE_ADD_ACTION);
        RawDestinationDescriptor
                serviceDTO = new RawDestinationDescriptor(getHttpDTO(), descriptor);
        String s = mapper.writeValueAsString(serviceDTO);
        RawDestinationDescriptor serviceDTO1 = mapper.readValue(s, RawDestinationDescriptor.class);
        Assert.assertEquals(serviceDTO, serviceDTO1);
    }

    @Test
    public void testHttpFSERVDeserializer() throws Exception {
	    HttpEndpointDescriptorDTO endpoint = new HttpEndpointDescriptorDTO("host", 65468);
	    ActionEndpointDTO<HttpActionDTO> actionEndpointDTO =
			    new ActionEndpointDTO<>("actionname",
			                            new HttpActionDTO("path", ActionMethod.HANDLE_ADD_ACTION));
	    DeliverySettingsDTO ds = new DeliverySettingsDTO(456, 456);
	    FullServiceDTO<HttpActionDTO> serviceDTO =
			    new FullServiceDTO<>("SAD", endpoint, ds, "creator", actionEndpointDTO);
	    String s = mapper.writeValueAsString(serviceDTO);
	    FullServiceDTO serviceDTO1 =
			    mapper.readValue(s, new TypeReference<FullServiceDTO<ActionDescriptor>>() {
                });
        Assert.assertEquals(serviceDTO, serviceDTO1);
    }

    @Test
    public void testAddActionDes() throws Exception {
        ActionRegistrationDTO<ActionDescriptor> a =
                new ActionRegistrationDTO<>(
                        new ActionEndpointDTO<ActionDescriptor>(
                                "SOURCE_SERVICE2_ACTION", new HttpActionDTO(
                                "/ext_source2/handleResponseFromTarget",
                                ActionMethod.HANDLE_RESPONSE_FROM_TARGET)
                        ),
                        false
                );
        AddActionDTO<ActionDescriptor> dto = new AddActionDTO<>("SERVICE2", a);
        String s = mapper.writeValueAsString(dto);
        AddActionDTO serviceDTO1 = mapper.readValue(s, AddActionDTO.class);
        Assert.assertEquals(dto, serviceDTO1);
    }

    @Test
    public void testDeserializer() throws Exception {
        RawDestinationDescriptor
                serviceDTO = new RawDestinationDescriptor(getJMSDTO(), getQueueDTO());
        String s = mapper.writeValueAsString(serviceDTO);
        RawDestinationDescriptor serviceDTO1 = mapper.readValue(s, RawDestinationDescriptor.class);
        Assert.assertEquals(serviceDTO, serviceDTO1);
    }

    @Test
    public void testDeserializerSericeDTO() throws Exception {
        ServiceDTO serviceDTO = new ServiceDTO("ser", EndpointType.HTTP,"Creator");
        String s = mapper.writeValueAsString(serviceDTO);
        ServiceDTO serviceDTO1 = mapper.readValue(s, ServiceDTO.class);
        Assert.assertEquals(serviceDTO, serviceDTO1);
    }

    private QueueDTO getQueueDTO() {
        return new QueueDTO("NAME", ActionMethod.HANDLE_ADD_ACTION);
    }

    private EndpointDescriptor getJMSDTO() {
        return new JMSEndpointDescriptorDTO("CONNFACTORY", new HashMap<String, String>() {{
            put("a", "1");
        }});
    }

    private EndpointDescriptor getHttpDTO() {
        return new HttpEndpointDescriptorDTO("host", 1001);
    }

}