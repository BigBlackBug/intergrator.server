package com.icl.integrator;

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

    @Override
    public <T extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
            @RequestBody(required = true)
            IntegratorPacket<DeliveryDTO, T> delivery) {
        return integratorService.deliver(delivery);
    }

    //TODO format
    @Override
    public <T extends DestinationDescriptor> Boolean ping(@RequestBody(
            required = false) IntegratorPacket<Void, T> responseHandler) {
        return integratorService.ping(responseHandler);
    }

    @Override
    public <T extends ActionDescriptor, Y extends DestinationDescriptor> ResponseDTO<Map<String, ResponseDTO<Void>>> registerService(
            @RequestBody(
                    required = true) IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO) {
        return integratorService
                .registerService(registrationDTO);
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
            @RequestBody(
                    required = true) IntegratorPacket<PingDTO, T> pingDTO) {
        return integratorService.isAvailable(pingDTO);
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<ServiceDTO>> getServiceList(
            @RequestBody(
                    required = false) IntegratorPacket<Void, T> responseHandlerDescriptor) {
        return integratorService.getServiceList(
                responseHandlerDescriptor);
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<String>> getSupportedActions(
            @RequestBody(
                    required = true) IntegratorPacket<ServiceDTO, T> serviceDTO) {

        return integratorService.getSupportedActions(
                serviceDTO);
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<Void> addAction(
            @RequestBody(
                    required = true) IntegratorPacket<AddActionDTO, T> actionDTO) {
        return integratorService.addAction(actionDTO);
    }

    @Override
    public <EDType extends EndpointDescriptor,
            ADType extends ActionDescriptor,
            DDType extends DestinationDescriptor>
    ResponseDTO<FullServiceDTO<EDType, ADType>> getServiceInfo(
            @RequestBody(
                    required = true) IntegratorPacket<ServiceDTO, DDType> serviceDTO) {
        return integratorService
                .getServiceInfo(serviceDTO);
    }
}
