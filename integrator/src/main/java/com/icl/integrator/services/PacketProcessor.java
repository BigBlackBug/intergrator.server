package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.model.AbstractActionEntity;
import com.icl.integrator.model.AbstractEndpointEntity;
import com.icl.integrator.model.Delivery;
import com.icl.integrator.model.DeliveryPacket;
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

    //    public Map<String, ResponseDTO<UUID>> process(DeliveryDTO packet) {
//        Map<String, ResponseDTO<UUID>> serviceToRequestID = new
//                HashMap<>();
//        for (DestinationDTO destination : packet.getDestinations()) {
//            ResponseDTO<UUID> response;
//            try {
//                UUID requestID = deliveryService.deliver(destination, packet);
//                response = new ResponseDTO<>(requestID, UUID.class);
//            } catch (IntegratorException ex) {
//                ErrorDTO error = new ErrorDTO(ex);
//                response = new ResponseDTO<>(error);
//            }
//            serviceToRequestID.put(destination.getServiceName(), response);
//        }
//        return serviceToRequestID;
//    }
    public Map<String, ResponseDTO<UUID>> process(DeliveryPacket deliveryPacket,
                                                  DestinationDescriptor destinationDescriptor) {
        //TODO get realservice or create temporary service
        AbstractActionEntity responseAction = null;
        AbstractEndpointEntity responseService = null;

        Map<String, ResponseDTO<UUID>> serviceToRequestID = new HashMap<>();
        for (Delivery delivery : deliveryPacket.getDeliveries()) {
            ResponseDTO<UUID> response;
            try {
                UUID requestID = deliveryService.deliver(delivery,
                                                         responseService,
                                                         responseAction);
                response = new ResponseDTO<>(requestID, UUID.class);
            } catch (Exception ex) {
                ErrorDTO error = new ErrorDTO(ex);
                response = new ResponseDTO<>(error);
            }
            serviceToRequestID
                    .put(delivery.getEndpoint().getServiceName(), response);
        }
        return serviceToRequestID;
    }
}
