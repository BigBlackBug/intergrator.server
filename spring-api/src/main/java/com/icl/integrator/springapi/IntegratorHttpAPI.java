package com.icl.integrator.springapi;

import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: e.shahmaev
 * Date: 20.01.14
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping(value = "/integrator/",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
public interface IntegratorHttpAPI extends IntegratorAPI {

    @Override
    @RequestMapping(value = "deliver", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
            @RequestBody(required = true)
            IntegratorPacket<DeliveryDTO, DestinationDescriptor> delivery);

    @Override
    @RequestMapping(value = "ping", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor>
    Boolean ping(@RequestBody(required = false)
                 IntegratorPacket<Void, T> responseHandlerDescriptor);

    @Override
    @RequestMapping(value = "registerService", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends ActionDescriptor, Y extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            @RequestBody(required = true)
            IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO);

    @Override
    @RequestMapping(value = "isAvailable", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor>
    ResponseDTO<Boolean> isAvailable(@RequestBody(required = true)
                                     IntegratorPacket<PingDTO, T> pingDTO);

    @Override
    @RequestMapping(value = "getServiceList", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor>
    ResponseDTO<List<ServiceDTO>> getServiceList(
            @RequestBody(required = false)
            IntegratorPacket<Void, T> responseHandlerDescriptor);

    @Override
    @RequestMapping(value = "getSupportedActions", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor>
    ResponseDTO<List<String>> getSupportedActions(
            @RequestBody(required = true)
            IntegratorPacket<ServiceDTO, T> serviceDTO);

    @Override
    @RequestMapping(value = "addAction", method = RequestMethod.POST)
    @ResponseBody
    <T extends DestinationDescriptor>
    ResponseDTO<Void> addAction(@RequestBody(required = true)
                                IntegratorPacket<AddActionDTO, T> actionDTO);

    @Override
    @RequestMapping(value = "getServiceInfo", method = RequestMethod.POST)
    public <EDType extends EndpointDescriptor, ADType extends ActionDescriptor,
            DDType extends DestinationDescriptor>
    ResponseDTO<FullServiceDTO<EDType, ADType>> getServiceInfo(
            @RequestBody(required = true)
            IntegratorPacket<ServiceDTO, DDType> serviceDTO);
}
