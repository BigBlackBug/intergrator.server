package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.model.Delivery;
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

    public Map<String, ResponseDTO<UUID>> process(List<Delivery> deliveries,
                                                  DestinationDescriptor destinationDescriptor) {
        //mb null
        PersistentDestination persistentDestination =
		        deliveryCreator.persistDestination(
				        destinationDescriptor);
        Map<String, ResponseDTO<UUID>> serviceToRequestID = new HashMap<String, ResponseDTO<UUID>>();
        for (Delivery delivery : deliveries) {
            ResponseDTO<UUID> response;
            try {
	            UUID requestID = deliveryService.deliver(delivery,
			            persistentDestination);
	            response = new ResponseDTO<UUID>(requestID, UUID.class);
            } catch (Exception ex) {
	            ErrorDTO error = new ErrorDTO(ex);
	            response = new ResponseDTO<UUID>(error);
            }
            serviceToRequestID
                    .put(delivery.getEndpoint().getServiceName(), response);
        }
        return serviceToRequestID;
    }
}
