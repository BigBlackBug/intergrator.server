package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.model.Delivery;
import com.icl.integrator.model.DestinationEntity;
import com.icl.integrator.services.converters.DefaultDeliverySuccessConverter;
import com.icl.integrator.services.converters.DefaultErrorConverter;
import com.icl.integrator.services.utils.ResponseDeliveryDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public class PacketProcessor {

    private static Log logger = LogFactory.getLog(PacketProcessor.class);

    @Autowired
    private DeliveryService deliveryService;

	@Autowired
	private DeliveryCreator deliveryCreator;

    public Map<String, ResponseDTO<UUID>> process(List<Delivery> deliveries) {
        Map<String, ResponseDTO<UUID>> serviceToRequestID = new HashMap<>();
        for (Delivery delivery : deliveries) {
            ResponseDTO<UUID> response;
	        String serviceName = delivery.getEndpoint().getServiceName();
	        try {
		        DefaultErrorConverter errorConverter = new DefaultErrorConverter();
		        DefaultDeliverySuccessConverter successConverter =
				        new DefaultDeliverySuccessConverter(delivery);
		        deliveryService.deliver(delivery,ResponseDTO.class,
		                                new ResponseDeliveryDescriptor<>(errorConverter,successConverter));
	            response = new ResponseDTO<>(delivery.getId(), UUID.class);
            } catch (Exception ex) {
	            ErrorDTO error = new ErrorDTO(ex);
	            response = new ResponseDTO<>(error);
            }
            serviceToRequestID.put(serviceName, response);
        }
        return serviceToRequestID;
    }
}
