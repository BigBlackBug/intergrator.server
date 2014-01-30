package com.icl.integrator.services;

import com.icl.integrator.dto.*;
import com.icl.integrator.util.IntegratorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
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

    public Map<String, ResponseDTO<UUID>> process(DeliveryDTO packet) {
        Map<String, ResponseDTO<UUID>> serviceToRequestID = new
                HashMap<String, ResponseDTO<UUID>>();
        for (DestinationDTO destination : packet.getDestinations()) {
            ResponseDTO<UUID> response;
            try {
                UUID requestID = deliveryService.deliver(destination, packet);
                response = new ResponseDTO<UUID>(requestID, UUID.class);
            } catch (IntegratorException ex) {
                ErrorDTO error = new ErrorDTO(ex);
                response = new ResponseDTO<UUID>(error);
            }
            serviceToRequestID.put(destination.getServiceName(), response);
        }
        return serviceToRequestID;
    }

}
