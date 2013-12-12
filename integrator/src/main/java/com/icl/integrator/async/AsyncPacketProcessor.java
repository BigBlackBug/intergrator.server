package com.icl.integrator.async;

import com.icl.integrator.async.service.DeliveryService;
import com.icl.integrator.dto.DestinationDTO;
import com.icl.integrator.dto.SourceDataDTO;
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
public class AsyncPacketProcessor {

    private static Log logger = LogFactory.getLog(AsyncPacketProcessor.class);

    @Autowired
    private DeliveryService deliveryService;

    public Map<String, String> process(SourceDataDTO packet) {
        Map<String, String> serviceToRequestID = new HashMap<>();
        for (DestinationDTO destination : packet.getDestinations()) {
            UUID requestID = deliveryService.deliver(destination, packet);
            serviceToRequestID.put(destination.getServiceName(),
                                   requestID.toString());
        }
        return serviceToRequestID;
    }

}
