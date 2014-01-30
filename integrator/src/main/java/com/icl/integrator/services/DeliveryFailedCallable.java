package com.icl.integrator.services;

import com.icl.integrator.dto.DestinationDTO;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.util.connectors.EndpointConnector;

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
        ResponseDTO responseFromTarget = new ResponseDTO(errorDTO);
        ResponseFromTargetDTO requestData = new ResponseFromTargetDTO(
                responseFromTarget,
                targetServiceName,
                requestID.toString());
         /*we're not expecting any response I guess*/
        sourceConnector.sendRequest(requestData, ResponseDTO.class);
        return null;
    }
}