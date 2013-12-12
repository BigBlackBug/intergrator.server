package com.icl.integrator.async.service;

import com.icl.integrator.dto.DestinationDTO;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.dto.ResponseToSourceDTO;
import com.icl.integrator.util.EndpointConnector;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.Callable;

class DeliveryFailedCallable implements Callable<Void> {

    private final DestinationDTO targetDestination;

    private final UUID requestID;

    private final EndpointConnector sourceConnector;

    DeliveryFailedCallable(EndpointConnector sourceConnector,
                                     DestinationDTO targetDestination,
                                     UUID requestID) {
        this.sourceConnector = sourceConnector;
        this.targetDestination = targetDestination;
        this.requestID = requestID;
    }

    @Override
    public Void call() throws Exception {
        String generalMessage = "Не могу доставить запрос {0} " +
                "на сервис {1}";
        String targetServiceName = targetDestination.getServiceName();
        ErrorDTO errorDTO = new ErrorDTO(MessageFormat.format
                (generalMessage, requestID, targetServiceName));
        ResponseFromTargetDTO<Object> responseFromTarget =
                new ResponseFromTargetDTO<>(errorDTO);
        ResponseToSourceDTO requestData = new ResponseToSourceDTO(
                responseFromTarget,
                targetServiceName,
                requestID.toString());
        sourceConnector.sendRequest(requestData,
            /*TODO ask*/ ResponseFromTargetDTO.class);
        return null;
    }
}