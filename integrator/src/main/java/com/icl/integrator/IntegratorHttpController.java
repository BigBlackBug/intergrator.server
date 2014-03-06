package com.icl.integrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.*;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.destination.ServiceDestinationDescriptor;
import com.icl.integrator.dto.registration.*;
import com.icl.integrator.dto.source.EndpointDescriptor;
import com.icl.integrator.services.IntegratorService;
import com.icl.integrator.springapi.IntegratorHttpAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
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

	private static Log logger =
			LogFactory.getLog(IntegratorHttpController.class);

	@Autowired
	private IntegratorService integratorService;

	@Autowired
	private ObjectMapper objectMapper;

	private <Result, Arg> Result fixConversion(
			Arg argument, TypeReference<Result> type) {
		return objectMapper.convertValue(argument, type);
	}

	@Override
	public <T extends DestinationDescriptor>
	ResponseDTO<Map<String, ResponseDTO<UUID>>> deliver(
			@RequestBody(required = true)
			IntegratorPacket<DeliveryDTO, T> delivery) {
		TypeReference<IntegratorPacket<DeliveryDTO, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<DeliveryDTO, DestinationDescriptor>>() {
				};
		return integratorService.deliver(fixConversion(delivery, type));
	}

	//TODO format
	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Boolean> ping(@RequestBody(
			required = false) IntegratorPacket<Void, T> responseHandler) {
		TypeReference<IntegratorPacket<Void, DestinationDescriptor>> type =
				new TypeReference<IntegratorPacket<Void, DestinationDescriptor>>() {
				};
		return integratorService.ping(fixConversion(responseHandler, type));
	}

	@Override
	public <T extends ActionDescriptor, Y extends DestinationDescriptor> ResponseDTO<RegistrationResultDTO> registerService(
			@RequestBody(
					required = true)
			IntegratorPacket<TargetRegistrationDTO<T>, Y> registrationDTO) {
		TypeReference<IntegratorPacket<TargetRegistrationDTO<ActionDescriptor>, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<TargetRegistrationDTO<ActionDescriptor>, DestinationDescriptor
						>>() {
				};
		return integratorService.registerService(fixConversion(
				registrationDTO, type));
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Boolean> isAvailable(
			@RequestBody(
					required = true)
			IntegratorPacket<ServiceDestinationDescriptor, T> serviceDescriptor) {
		TypeReference<IntegratorPacket<ServiceDestinationDescriptor, DestinationDescriptor>> type =
				new TypeReference<IntegratorPacket<ServiceDestinationDescriptor, DestinationDescriptor>>() {
				};
		return integratorService.isAvailable(fixConversion(serviceDescriptor, type));
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<List<ServiceDTO>> getServiceList(
			@RequestBody(
					required = false)
			IntegratorPacket<Void, T> responseHandlerDescriptor) {
		TypeReference<IntegratorPacket<Void, DestinationDescriptor>> type =
				new TypeReference<IntegratorPacket<Void, DestinationDescriptor>>() {
				};
		return integratorService.getServiceList(fixConversion(
				responseHandlerDescriptor, type));
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<List<String>> getSupportedActions(
			@RequestBody(
					required = true)
			IntegratorPacket<ServiceDTO, T> serviceDTO) {

		TypeReference<IntegratorPacket<ServiceDTO, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<ServiceDTO, DestinationDescriptor>>() {
				};

		return integratorService.getSupportedActions(fixConversion(
				serviceDTO, type));
	}

	@Override
	public <T extends DestinationDescriptor> ResponseDTO<Void> addAction(
			@RequestBody(
					required = true)
			IntegratorPacket<AddActionDTO, T> actionDTO) {
		TypeReference<IntegratorPacket<AddActionDTO, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<AddActionDTO, DestinationDescriptor>>() {
				};
		return integratorService.addAction(fixConversion(actionDTO, type));
	}

	@Override
	public <EDType extends EndpointDescriptor,
			ADType extends ActionDescriptor,
			DDType extends DestinationDescriptor>
	ResponseDTO<FullServiceDTO<EDType, ADType>>
	getServiceInfo(@RequestBody(required = true)
	               IntegratorPacket<ServiceDTO, DDType> serviceDTO) {
		TypeReference<IntegratorPacket<ServiceDTO, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<ServiceDTO, DestinationDescriptor>>() {
				};
		return integratorService
				.getServiceInfo(fixConversion(serviceDTO, type));
	}

	@Override
	public <T extends DestinationDescriptor, Y>
	ResponseDTO<List<ResponseDTO<Void>>> registerAutoDetection(
			@RequestBody(required = true)
			IntegratorPacket<AutoDetectionRegistrationDTO<Y>, T> autoDetectionDTO) {
		TypeReference<IntegratorPacket<AutoDetectionRegistrationDTO<Y>, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<AutoDetectionRegistrationDTO<Y>, DestinationDescriptor>>() {
				};
		return integratorService.registerAutoDetection(
				fixConversion(autoDetectionDTO, type));
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
