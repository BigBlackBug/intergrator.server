package com.icl.integrator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.api.IntegratorAPI;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.editor.EditServiceDTO;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.services.AuthorizationService;
import com.icl.integrator.springapi.IntegratorHttpAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: BigBlackBug
 * Date: 29.11.13
 * Time: 9:16
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class IntegratorHttpController implements IntegratorHttpAPI {

    private static Log logger = LogFactory.getLog(IntegratorHttpController.class);

	@Qualifier("integratorService")
	@Autowired
	private IntegratorAPI integratorService;

	@Autowired
	private AuthorizationService authService;

	@Autowired
	private ObjectMapper objectMapper;

	private <Result, Arg> Result fixConversion(
			Arg argument, TypeReference<Result> type) {
		return objectMapper.convertValue(argument, type);
	}

	@Override
	public <T extends DestinationDescriptor>
    ResponseDTO<Map<String, ResponseDTO<String>>>
    deliver(@RequestBody IntegratorPacket<DeliveryDTO, T> delivery) {
        TypeReference<IntegratorPacket<DeliveryDTO, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<DeliveryDTO, DestinationDescriptor>>() {
				};
		return integratorService.deliver(fixConversion(delivery, type));
	}

	@Override
    public <T extends DestinationDescriptor>
    ResponseDTO<Boolean> ping(@RequestBody IntegratorPacket<Void, T> responseHandler) {
        TypeReference<IntegratorPacket<Void, DestinationDescriptor>> type =
                new TypeReference<IntegratorPacket<Void, DestinationDescriptor>>() {
                };
		return integratorService.ping(fixConversion(responseHandler, type));
	}

	@Override
    public <T extends ActionDescriptor, Y extends DestinationDescriptor>
	ResponseDTO<List<ActionRegistrationResultDTO>>
    registerService(@RequestBody IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO) {
        TypeReference<IntegratorPacket<TargetRegistrationDTO<ActionDescriptor>, DestinationDescriptor>>
                type =
                new TypeReference<IntegratorPacket<TargetRegistrationDTO<ActionDescriptor>,
                        DestinationDescriptor>>() {
                };
        return integratorService.registerService(fixConversion(
                registrationDTO, type));
	}

	@Override
    public <T extends DestinationDescriptor> ResponseDTO<Boolean>
    isAvailable(@RequestBody IntegratorPacket<ServiceDestinationDescriptor, T> serviceDescriptor) {
        TypeReference<IntegratorPacket<ServiceDestinationDescriptor, DestinationDescriptor>> type =
                new TypeReference<IntegratorPacket<ServiceDestinationDescriptor,
                        DestinationDescriptor>>() {
                };
        return integratorService.isAvailable(fixConversion(serviceDescriptor, type));
    }

    @Override
    public <T extends DestinationDescriptor> ResponseDTO<List<ServiceDTO>>
    getServiceList(@RequestBody IntegratorPacket<Void, T> responseHandlerDescriptor) {
        TypeReference<IntegratorPacket<Void, DestinationDescriptor>> type =
                new TypeReference<IntegratorPacket<Void, DestinationDescriptor>>() {
                };
		return integratorService.getServiceList(fixConversion(
				responseHandlerDescriptor, type));
	}

	@Override
	public <T extends DestinationDescriptor,Y extends ActionDescriptor>
    ResponseDTO<List<ActionEndpointDTO<Y>>>
    getSupportedActions(@RequestBody IntegratorPacket<String, T> serviceName) {
        TypeReference<IntegratorPacket<String, DestinationDescriptor>>
                type =
				new TypeReference<IntegratorPacket<String, DestinationDescriptor>>() {
				};

		return integratorService.getSupportedActions(fixConversion(
				serviceName, type));
	}

    @Override
    public <T extends DestinationDescriptor, Y extends ActionDescriptor>
    ResponseDTO<Void> addAction(@RequestBody IntegratorPacket<AddActionDTO<Y>, T> packet) {
        TypeReference<IntegratorPacket<AddActionDTO<Y>, DestinationDescriptor>>
		        type =
		        new TypeReference<IntegratorPacket<AddActionDTO<Y>, DestinationDescriptor>>() {
		        };
	    AddActionDTO<Y> data = packet.getData();
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (!authService.hasAccessToService(data.getServiceName(), auth)) {
		    return new ResponseDTO<>(new ErrorDTO(
				    "Вы не можете добавлять действия к сервису, созданному не Вами"));
	    }
	    return integratorService.addAction(fixConversion(packet, type));
    }

	@Override
	public <ADType extends ActionDescriptor,
			DDType extends DestinationDescriptor>
	ResponseDTO<FullServiceDTO<ADType>>
    getServiceInfo(@RequestBody IntegratorPacket<String, DDType> serviceName) {
        TypeReference<IntegratorPacket<String, DestinationDescriptor>>
                type =
				new TypeReference<IntegratorPacket<String, DestinationDescriptor>>() {
				};
		return integratorService.getServiceInfo(fixConversion(serviceName, type));
	}

	@Override
	public <T extends DestinationDescriptor, Y>
    ResponseDTO<List<ResponseDTO<Void>>>
    registerAutoDetection(@RequestBody
                          IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO) {
        TypeReference<IntegratorPacket<AutoDetectionRegistrationDTO<Y>, DestinationDescriptor>>
                type =
				new TypeReference<IntegratorPacket<AutoDetectionRegistrationDTO<Y>, DestinationDescriptor>>() {
				};
		return integratorService.registerAutoDetection(fixConversion(autoDetectionDTO, type));
	}

    @Override
    public <T extends DestinationDescriptor>
    ResponseDTO<List<DeliveryActionsDTO>>
    getActionsForDelivery(@RequestBody IntegratorPacket<Void, T> packet) {
        TypeReference<IntegratorPacket<Void, DestinationDescriptor>>
                type =
                new TypeReference<IntegratorPacket<Void, DestinationDescriptor>>() {
                };
        return integratorService.getActionsForDelivery(fixConversion(packet, type));
    }

    @Override
    public <T extends DestinationDescriptor, Y extends ActionDescriptor>
    ResponseDTO<List<ServiceAndActions<Y>>>
    getServicesSupportingActionType(@RequestBody IntegratorPacket<ActionMethod, T> packet) {
        TypeReference<IntegratorPacket<ActionMethod, DestinationDescriptor>>
                type =
                new TypeReference<IntegratorPacket<ActionMethod, DestinationDescriptor>>() {
                };
        return integratorService.getServicesSupportingActionType(fixConversion(packet, type));
    }

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<List<Modification>> fetchUpdates(
			@RequestBody IntegratorPacket<Void, T> responseHandler) {
		TypeReference<IntegratorPacket<Void, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<Void, DestinationDescriptor>>() {
				};
		return integratorService.fetchUpdates(fixConversion(responseHandler, type));
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Void> removeService(
			@RequestBody IntegratorPacket<String, T> serviceNamePacket) {
		TypeReference<IntegratorPacket<String, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<String, DestinationDescriptor>>() {
				};
		String serviceName = serviceNamePacket.getData();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!authService.hasAccessToService(serviceName, auth)) {
			return new ResponseDTO<>(new ErrorDTO(
					"Вы не можете удалять сервис, созданный не Вами"));
		}
		return integratorService.removeService(fixConversion(serviceNamePacket, type));
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Void> editService(
			@RequestBody IntegratorPacket<EditServiceDTO, T> editServiceDTO) {
		TypeReference<IntegratorPacket<EditServiceDTO, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<EditServiceDTO, DestinationDescriptor>>() {
				};
		String serviceName = editServiceDTO.getData().getServiceName();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!authService.hasAccessToService(serviceName, auth)) {
			return new ResponseDTO<>(new ErrorDTO(
					"Вы не можете изменять сервис, созданный не Вами"));
		}
		return integratorService.editService(fixConversion(editServiceDTO, type));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDTO> handleNotAuthenticatedException(
			Exception ex,
			HttpServletRequest request) {
		logger.info("handling exception");
		return new ResponseEntity<ResponseDTO>(
				new ResponseDTO<>(new ErrorDTO(ex)),
				HttpStatus.BAD_REQUEST
		);
	}
}
