package com.icl.integrator.api;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.EndpointDescriptor;

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

	/**
	 * Основной метод, используемый для доста
	 *
	 * @param delivery
	 * @return Карта вида (название сервиса)->(ID запроса или ошибка)
	 */
    public <T extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<String>>>
    deliver(IntegratorPacket<DeliveryDTO, T> delivery);

    public <T extends DestinationDescriptor>
    ResponseDTO<Boolean>
    ping(IntegratorPacket<Void, T> packet);

    public <T extends ActionDescriptor, Y extends DestinationDescriptor>
    ResponseDTO<RegistrationResultDTO>
    registerService(IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO);

    public <T extends DestinationDescriptor>
    ResponseDTO<Boolean>
    isAvailable(IntegratorPacket<ServiceDestinationDescriptor, T> pingDTO);

    public <T extends DestinationDescriptor>
    ResponseDTO<List<ServiceDTO>>
    getServiceList(IntegratorPacket<Void, T> packet);

    public <T extends DestinationDescriptor>
    ResponseDTO<List<ActionEndpointDTO>>
    getSupportedActions(IntegratorPacket<ServiceDTO, T> serviceDTO);

    public <EDType extends EndpointDescriptor, ADType extends ActionDescriptor,
            DDType extends DestinationDescriptor>
    ResponseDTO<FullServiceDTO<EDType, ADType>>
    getServiceInfo(IntegratorPacket<ServiceDTO, DDType> serviceDTO);

    public <T extends DestinationDescriptor,Y extends ActionDescriptor>
    ResponseDTO<Void>
    addAction(IntegratorPacket<AddActionDTO<Y>, T> actionDTO);

	public <T extends DestinationDescriptor,Y>
	ResponseDTO<List<ResponseDTO<Void>>>
	registerAutoDetection(IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO);
}
