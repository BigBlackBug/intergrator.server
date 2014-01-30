package com.icl.integrator.api;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;

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

    public ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
            IntegratorPacket<DeliveryDTO> delivery);

    public Boolean ping(IntegratorPacket<Void> responseHandlerDescriptor);

    public <T extends ActionDescriptor>
    ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            IntegratorPacket<TargetRegistrationDTO<T>> registrationDTO);

    public ResponseDTO<Boolean> isAvailable(IntegratorPacket<PingDTO> pingDTO);

    public ResponseDTO<List<ServiceDTO>> getServiceList(
            IntegratorPacket<Void> responseHandlerDescriptor);

    public ResponseDTO<List<String>> getSupportedActions(
            IntegratorPacket<ServiceDTO> serviceDTO);

    public <T extends EndpointDescriptor, Y extends ActionDescriptor>
    ResponseDTO<FullServiceDTO<T, Y>> getServiceInfo(
            IntegratorPacket<ServiceDTO> serviceDTO);

    public ResponseDTO<Void> addAction(IntegratorPacket<AddActionDTO>
                                               actionDTO);

}
