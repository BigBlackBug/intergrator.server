package com.icl.integrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.services.IntegratorHttpService;
import com.icl.integrator.springapi.IntegratorHttpAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:16
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class IntegratorHttpController implements IntegratorHttpAPI {

    @Autowired
    private IntegratorHttpService httpService;

    @Autowired
    private ObjectMapper objectMapper;

    private <Result, Arg> Result fixConversion(
            Arg argument, TypeReference<Result> type) {
        return objectMapper.convertValue(argument, type);
    }

    @Override
    public Map<String, ResponseDTO<UUID>> deliver(
            @RequestBody(required = true)
            IntegratorPacket<DeliveryDTO> delivery) {
        TypeReference<IntegratorPacket<DeliveryDTO>> type =
                new TypeReference<IntegratorPacket<DeliveryDTO>>() {
                };
        return httpService.deliver(fixConversion(delivery, type));
    }

    @Override
    public Boolean ping(@RequestBody(required = true)
                        IntegratorPacket<Void> responseHandler) {
        TypeReference<IntegratorPacket<Void>> type =
                new TypeReference<IntegratorPacket<Void>>() {
                };
        return httpService.ping(fixConversion(responseHandler, type));
    }

    @Override
    public <T extends ActionDescriptor>
    ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            @RequestBody(required = true)
            IntegratorPacket<TargetRegistrationDTO<T>> registrationDTO) {
        TypeReference<IntegratorPacket<TargetRegistrationDTO<T>>> type =
                new TypeReference<IntegratorPacket<TargetRegistrationDTO<T>>>() {
                };
        return httpService.registerService(fixConversion(
                registrationDTO, type));
    }

    @Override
    public ResponseDTO<Boolean> isAvailable(@RequestBody(required = true)
                                            IntegratorPacket<PingDTO> pingDTO) {
        TypeReference<IntegratorPacket<PingDTO>> type =
                new TypeReference<IntegratorPacket<PingDTO>>() {
                };
        return httpService.isAvailable(fixConversion(pingDTO, type));
    }

    @Override
    public ResponseDTO<List<ServiceDTO>> getServiceList(
            @RequestBody(required = false)
            IntegratorPacket<Void> responseHandlerDescriptor) {
        TypeReference<IntegratorPacket<Void>> type =
                new TypeReference<IntegratorPacket<Void>>() {
                };
        return httpService.getServiceList(fixConversion(
                responseHandlerDescriptor, type));
    }

    @Override
    public ResponseDTO<List<String>> getSupportedActions(
            @RequestBody(required = true)
            IntegratorPacket<ServiceDTO> serviceDTO) {
        TypeReference<IntegratorPacket<ServiceDTO>> type =
                new TypeReference<IntegratorPacket<ServiceDTO>>() {
                };

        return httpService.getSupportedActions(fixConversion(
                serviceDTO, type));
    }

    @Override
    public ResponseDTO addAction(@RequestBody(required = true)
                                 IntegratorPacket<AddActionDTO> actionDTO) {
        TypeReference<IntegratorPacket<AddActionDTO>> type =
                new TypeReference<IntegratorPacket<AddActionDTO>>() {
                };
        return httpService.addAction(fixConversion(actionDTO, type));
    }

    @Override
    public <T extends EndpointDescriptor, Y extends ActionDescriptor>
    ResponseDTO<FullServiceDTO<T, Y>> getServiceInfo(
            @RequestBody(required = true)
            IntegratorPacket<ServiceDTO> serviceDTO) {
        TypeReference<IntegratorPacket<ServiceDTO>> type =
                new TypeReference<IntegratorPacket<ServiceDTO>>() {
                };
        return httpService.getServiceInfo(fixConversion(serviceDTO, type));
    }
}
