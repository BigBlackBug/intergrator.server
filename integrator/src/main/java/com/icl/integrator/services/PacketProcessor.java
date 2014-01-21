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

    public void process(DeliveryDTO packet) {
        Map<String, ResponseDTO<String>> serviceToRequestID = new
                HashMap<>();
        for (DestinationDTO destination : packet.getDestinations()) {
            ResponseDTO<String> response;
            try {
                UUID requestID = deliveryService.deliver(destination, packet);
                response = new ResponseDTO<>(requestID.toString(),
                                                       String.class);
            } catch (IntegratorException ex) {
                ErrorDTO error = new ErrorDTO(ex);
                response = new ResponseDTO<>(error);
            }
            serviceToRequestID.put(destination.getServiceName(), response);
        }
        SourceServiceDTO sourceService = packet.getSourceService();
        //TODO добавить лок, чтоб сначала выполнялся этот запрос
        if (sourceService != null) {
            deliveryService.deliver(sourceService, serviceToRequestID);
        }
    }

}
