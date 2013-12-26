package com.icl.integrator.services;

import com.icl.integrator.dto.DestinationDTO;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.SourceDataDTO;
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

    public Map<String, ResponseFromTargetDTO<String>> process(
            SourceDataDTO packet) {
        Map<String, ResponseFromTargetDTO<String>> serviceToRequestID = new
                HashMap<>();
        for (DestinationDTO destination : packet.getDestinations()) {
            ResponseFromTargetDTO<String> response;
            try {
                UUID requestID = deliveryService.deliver(destination, packet);
                response = new ResponseFromTargetDTO<>(requestID.toString(),
                                                       String.class);
            } catch (IntegratorException ex) {
                ErrorDTO error = new ErrorDTO(ex.getMessage(),
                                              ex.getCause().getMessage());
                response = new ResponseFromTargetDTO<>(error);
            }
            serviceToRequestID.put(destination.getServiceName(), response);
        }
        return serviceToRequestID;
    }

}
