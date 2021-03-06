package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.model.Delivery;
import com.icl.integrator.services.converters.DefaultDeliverySuccessConverter;
import com.icl.integrator.services.converters.DefaultErrorConverter;
import com.icl.integrator.services.utils.ResponseDeliveryDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PacketProcessor {

    private static Log logger = LogFactory.getLog(PacketProcessor.class);

    @Autowired
    private DeliveryService deliveryService;

    public Map<String, ResponseDTO<String>> process(List<Delivery> deliveries) {
        Map<String, ResponseDTO<String>> serviceToRequestID = new HashMap<>();
        for (Delivery delivery : deliveries) {
            ResponseDTO<String> response;
	        String serviceName = delivery.getEndpoint().getServiceName();
	        try {
		        DefaultErrorConverter errorConverter = new DefaultErrorConverter();
		        DefaultDeliverySuccessConverter successConverter =
				        new DefaultDeliverySuccessConverter(delivery);
		        deliveryService.deliver(delivery,ResponseDTO.class,
		                                new ResponseDeliveryDescriptor<>(errorConverter,successConverter));
	            response = new ResponseDTO<>(delivery.getId().toString(), String.class.toString());
            } catch (Exception ex) {
	            ErrorDTO error = new ErrorDTO(ex);
	            response = new ResponseDTO<>(error);
            }
            serviceToRequestID.put(serviceName, response);
        }
        return serviceToRequestID;
    }
}
