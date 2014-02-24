package com.icl.integrator.api;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.AutoDetectionRegistrationDTO;
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

    public
    <T extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
            IntegratorPacket<DeliveryDTO, T> delivery);

    public <T extends DestinationDescriptor> ResponseDTO<Boolean>
    ping(IntegratorPacket<Void, T> packet);

    public <T extends ActionDescriptor, Y extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO);

    public <T extends DestinationDescriptor>
    ResponseDTO<Boolean> isAvailable(IntegratorPacket<ServiceDestinationDescriptor, T> pingDTO);

    public <T extends DestinationDescriptor> ResponseDTO<List<ServiceDTO>>
    getServiceList(IntegratorPacket<Void, T> packet);

    public <T extends DestinationDescriptor> ResponseDTO<List<String>>
    getSupportedActions(IntegratorPacket<ServiceDTO, T> serviceDTO);

    public <EDType extends EndpointDescriptor, ADType extends ActionDescriptor,
            DDType extends DestinationDescriptor>
    ResponseDTO<FullServiceDTO<EDType, ADType>>
    getServiceInfo(IntegratorPacket<ServiceDTO, DDType> serviceDTO);

    public <T extends DestinationDescriptor> ResponseDTO<Void>
    addAction(IntegratorPacket<AddActionDTO, T> actionDTO);

	public <T extends DestinationDescriptor,Y> ResponseDTO<List<ResponseDTO<Void>>> registerAutoDetection(
			IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO);
}
