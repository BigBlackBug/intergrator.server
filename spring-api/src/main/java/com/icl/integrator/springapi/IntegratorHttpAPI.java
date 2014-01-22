package com.icl.integrator.springapi;

import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
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
    Map<String, ResponseDTO<UUID>> deliver(
            @RequestBody(required = true) DeliveryDTO packet);

    @Override
    @RequestMapping(value = "ping", method = RequestMethod.POST)
    public
    @ResponseBody
    Boolean ping(@RequestBody(required = false)
                 RawDestinationDescriptorDTO responseHandler);

    @Override
    @RequestMapping(value = "registerService", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            @RequestBody(required = true)
            TargetRegistrationDTO<?> registrationDTO);

    @Override
    @RequestMapping(value = "checkAvailability", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseDTO<Boolean> isAvailable(@RequestBody(required = true)
                                     PingDTO pingDTO);

    @Override
    @RequestMapping(value = "getServiceList", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseDTO<List<ServiceDTO>> getServiceList(
            @RequestBody(required = false)
            RawDestinationDescriptorDTO responseHandler);

    @Override
    @RequestMapping(value = "getSupportedActions", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseDTO<List<String>> getSupportedActions(
            @RequestBody(required = true)
            ServiceDTOWithResponseHandler serviceDTO);

    @Override
    @RequestMapping(value = "addAction", method = RequestMethod.POST)
    @ResponseBody
    ResponseDTO addAction(@RequestBody(required = true) AddActionDTO actionDTO);
}
