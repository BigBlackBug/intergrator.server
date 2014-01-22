package com.icl.integrator.api;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public interface IntegratorAPI {

    public Map<String, ResponseDTO<UUID>> deliver(DeliveryDTO packet);

    public Boolean ping(RawDestinationDescriptorDTO responseHandler);

    public ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            TargetRegistrationDTO<?> registrationDTO);

    public ResponseDTO<Boolean> isAvailable(PingDTO pingDTO);

    public ResponseDTO<List<ServiceDTO>> getServiceList
            (RawDestinationDescriptorDTO responseHandler);

    public ResponseDTO<List<String>> getSupportedActions(
            ServiceDTOWithResponseHandler serviceDTO);

    public ResponseDTO addAction(AddActionDTO actionDTO);
    //TODO add action to service

    //TODO get service info?

    //TODO складывать в базу входящие запросы
}
