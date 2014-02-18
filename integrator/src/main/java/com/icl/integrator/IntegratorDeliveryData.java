package com.icl.integrator;

import com.icl.integrator.dto.destination.DestinationDescriptor;

import java.util.UUID;

/**
 * Created by BigBlackBug on 2/4/14.
 */
public class IntegratorDeliveryData {

    private final DestinationDescriptor responseDestinationDescriptor;

    private final UUID requestID;

    public IntegratorDeliveryData(UUID requestID,
                                  DestinationDescriptor responseDestinationDescriptor) {
        this.requestID = requestID;
        this.responseDestinationDescriptor = responseDestinationDescriptor;
    }

    public DestinationDescriptor getResponseDestinationDescriptor() {
        return responseDestinationDescriptor;
    }

    public UUID getRequestID() {
        return requestID;
    }
}
