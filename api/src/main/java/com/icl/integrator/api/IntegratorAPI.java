package com.icl.integrator.api;

import com.icl.integrator.dto.DeliveryDTO;
import com.icl.integrator.dto.PingDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ServiceDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public interface IntegratorAPI {

    public void deliver(DeliveryDTO packet);

    public Boolean ping();

    public ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            TargetRegistrationDTO<?> registrationDTO);

    public ResponseDTO<Boolean> isAvailable(PingDTO pingDTO);

    public ResponseDTO<List<ServiceDTO>> getServiceList();

    public ResponseDTO<List<String>> getSupportedActions(
            ServiceDTO serviceDTO);

    //TODO add action to service

    //TODO add an api for services
}
