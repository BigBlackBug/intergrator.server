package com.icl.integrator.api;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;

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
	 * Основной метод, используемый для доставки
	 *
	 * @return Карта вида (название сервиса)->(ID запроса или ошибка)
	 */
    public <T extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<String>>>
    deliver(IntegratorPacket<DeliveryDTO, T> delivery);

    /**
     * Проверяет, доступен ли в данный момент интегратор
     */
    public <T extends DestinationDescriptor>
    ResponseDTO<Boolean>
    ping(IntegratorPacket<Void, T> packet);

    public <T extends ActionDescriptor, Y extends DestinationDescriptor>
    ResponseDTO<RegistrationResultDTO>
    registerService(IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO);

    /**
     * Проверяет доступность указанного сервиса
     */
    public <T extends DestinationDescriptor>
    ResponseDTO<Boolean>
    isAvailable(IntegratorPacket<ServiceDestinationDescriptor, T> pingDTO);

    public <T extends DestinationDescriptor>
    ResponseDTO<List<ServiceDTO>>
    getServiceList(IntegratorPacket<Void, T> packet);

    /**
     * Возвращает список действий, зарегистрированных на сервисе
     */
    public <T extends DestinationDescriptor,Y extends ActionDescriptor>
    ResponseDTO<List<ActionEndpointDTO<Y>>>
    getSupportedActions(IntegratorPacket<ServiceDTO, T> serviceDTO);

    /**
     * Возвращает полную информацию о сервисе, включая информацию о действиях
     */
    public <ADType extends ActionDescriptor,
            DDType extends DestinationDescriptor>
    ResponseDTO<FullServiceDTO<ADType>>
    getServiceInfo(IntegratorPacket<ServiceDTO, DDType> serviceDTO);

    public <T extends DestinationDescriptor,Y extends ActionDescriptor>
    ResponseDTO<Void>
    addAction(IntegratorPacket<AddActionDTO<Y>, T> actionDTO);

    /**
     * Добавляет правило автоматического определения таргета по сообщению
     */
	public <T extends DestinationDescriptor,Y>
	ResponseDTO<List<ResponseDTO<Void>>>
	registerAutoDetection(IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO);

    /**
     * Получает список таргетов, на которые можно отправить сообщения.
     * Не проверяет доступность этих таргетов.
     * @return список соответствий: действие - [сервисы, поддерживающие это действие].
     */
    public <T extends DestinationDescriptor>
    ResponseDTO<List<DeliveryActionsDTO>>
    getActionsForDelivery(IntegratorPacket<Void, T> packet);

    /**
     * Получает инфу о сервисах и действиях определённого типа
     * @param packet тип действия
     * @return карту название_сервиса - {сервис и инфа о действиях}
     */
    public <T extends DestinationDescriptor, Y extends ActionDescriptor>
    ResponseDTO<Map<String, ServiceAndActions<Y>>>
    getServicesSupportingActionType(IntegratorPacket<ActionMethod, T> packet);
}