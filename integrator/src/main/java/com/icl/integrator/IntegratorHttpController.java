package com.icl.integrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.AddActionDTO;
import com.icl.integrator.dto.registration.TargetRegistrationDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.services.IntegratorService;
import com.icl.integrator.springapi.IntegratorHttpAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private IntegratorService integratorService;

    @Autowired
    private ObjectMapper objectMapper;

    private <Result, Arg> Result fixConversion(
            Arg argument, TypeReference<Result> type) {
        return objectMapper.convertValue(argument, type);
    }

    @RequestMapping(value = "/test")
    public @ResponseBody
    void test(@RequestBody Map string){
        System.out.println(string);
    }

	@Override
    public
    ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
            @RequestBody(required = true)
            IntegratorPacket<DeliveryDTO, DestinationDescriptor> delivery) {
        TypeReference<IntegratorPacket<DeliveryDTO, DestinationDescriptor>> type =
                new TypeReference<IntegratorPacket<DeliveryDTO, DestinationDescriptor>>() {
                };
        return integratorService.deliver(fixConversion(delivery, type));
    }

    //TODO format
    @Override
    public <T extends DestinationDescriptor> Boolean ping(@RequestBody(
            required = false) IntegratorPacket<Void, T> responseHandler) {
        TypeReference<IntegratorPacket<Void, T>> type =
                new TypeReference<IntegratorPacket<Void, T>>() {
                };
        return integratorService.ping(fixConversion(responseHandler, type));
    }

    @Override
    public <T extends ActionDescriptor, Y extends DestinationDescriptor> ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            @RequestBody(
                    required = true) IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO) {
        TypeReference<IntegratorPacket<TargetRegistrationDTO<T>, Y>> type =
                new TypeReference<IntegratorPacket<TargetRegistrationDTO<T>, Y
                        >>() {
                };
        return integratorService.registerService(fixConversion(
                registrationDTO, type));
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
            @RequestBody(
                    required = true) IntegratorPacket<PingDTO, T> pingDTO) {
        TypeReference<IntegratorPacket<PingDTO, T>> type =
                new TypeReference<IntegratorPacket<PingDTO, T>>() {
                };
        return integratorService.isAvailable(fixConversion(pingDTO, type));
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<ServiceDTO>> getServiceList(
            @RequestBody(
                    required = false) IntegratorPacket<Void, T> responseHandlerDescriptor) {
        TypeReference<IntegratorPacket<Void, T>> type =
                new TypeReference<IntegratorPacket<Void, T>>() {
                };
        return integratorService.getServiceList(fixConversion(
                responseHandlerDescriptor, type));
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<String>> getSupportedActions(
            @RequestBody(
                    required = true) IntegratorPacket<ServiceDTO, T> serviceDTO) {

        TypeReference<IntegratorPacket<ServiceDTO, T>> type =
                new TypeReference<IntegratorPacket<ServiceDTO, T>>() {
                };

        return integratorService.getSupportedActions(fixConversion(
                serviceDTO, type));
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Void> addAction(
            @RequestBody(
                    required = true) IntegratorPacket<AddActionDTO, T> actionDTO) {
        TypeReference<IntegratorPacket<AddActionDTO, T>> type =
                new TypeReference<IntegratorPacket<AddActionDTO, T>>() {
                };
        return integratorService.addAction(fixConversion(actionDTO, type));
    }

    @Override
    public <EDType extends EndpointDescriptor,
            ADType extends ActionDescriptor,
            DDType extends DestinationDescriptor>
    ResponseDTO<FullServiceDTO<EDType, ADType>> getServiceInfo(
            @RequestBody(
                    required = true) IntegratorPacket<ServiceDTO, DDType> serviceDTO) {
        TypeReference<IntegratorPacket<ServiceDTO, DDType>> type =
                new TypeReference<IntegratorPacket<ServiceDTO, DDType>>() {
                };
        return integratorService
                .getServiceInfo(fixConversion(serviceDTO, type));
    }
}
