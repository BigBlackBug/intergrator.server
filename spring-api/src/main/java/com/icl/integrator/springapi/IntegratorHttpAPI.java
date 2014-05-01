package com.icl.integrator.springapi;

import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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
    @ResponseBody  <T extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<String>>> deliver(
		    @RequestBody
		    IntegratorPacket<DeliveryDTO, T> delivery);

    @Override
    @RequestMapping(value = "ping", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor>
    ResponseDTO<Boolean> ping(@RequestBody
                 IntegratorPacket<Void, T> responseHandlerDescriptor);

    @Override
    @RequestMapping(value = "registerService", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends ActionDescriptor, Y extends DestinationDescriptor>
    ResponseDTO<List<ActionRegistrationResultDTO>> registerService(
		    @RequestBody
		    IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO);

    @Override
    @RequestMapping(value = "isAvailable", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor>
    ResponseDTO<Boolean> isAvailable(@RequestBody
                                     IntegratorPacket<ServiceDestinationDescriptor, T> pingDTO);

    @Override
    @RequestMapping(value = "getServiceList", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor>
    ResponseDTO<List<ServiceDTO>> getServiceList(
            @RequestBody
            IntegratorPacket<Void, T> responseHandlerDescriptor);

    @Override
    @RequestMapping(value = "getSupportedActions", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor,Y extends ActionDescriptor>
    ResponseDTO<List<ActionEndpointDTO<Y>>> getSupportedActions(
            @RequestBody
            IntegratorPacket<String, T> serviceName);

    @Override
    @RequestMapping(value = "addAction", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor,Y extends ActionDescriptor>
    ResponseDTO<Void> addAction(@RequestBody
                                IntegratorPacket<AddActionDTO<Y>, T> actionDTO);

    @Override
    @RequestMapping(value = "getServiceInfo", method = RequestMethod.POST)
    public @ResponseBody
    <ADType extends ActionDescriptor,
            DDType extends DestinationDescriptor>
    ResponseDTO<FullServiceDTO<ADType>> getServiceInfo(
            @RequestBody
            IntegratorPacket<String, DDType> serviceName);

	@Override
	@RequestMapping(value = "registerAutoDetection", method = RequestMethod.POST)
	public
	@ResponseBody
	<T extends DestinationDescriptor, Y>
	ResponseDTO<List<ResponseDTO<Void>>> registerAutoDetection(
			@RequestBody
			IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO);

    @Override
    @RequestMapping(value = "getActionsForDelivery", method = RequestMethod.POST)
    public
    @ResponseBody
    <T extends DestinationDescriptor>
    ResponseDTO<List<DeliveryActionsDTO>>
    getActionsForDelivery(@RequestBody IntegratorPacket<Void, T> packet);

    @Override
    @RequestMapping(value = "getServicesSupportingActionType", method = RequestMethod.POST)
    public
    @ResponseBody <T extends DestinationDescriptor,Y extends ActionDescriptor>
    ResponseDTO<List<ServiceAndActions<Y>>>
    getServicesSupportingActionType(@RequestBody IntegratorPacket<ActionMethod, T> packet);
}
