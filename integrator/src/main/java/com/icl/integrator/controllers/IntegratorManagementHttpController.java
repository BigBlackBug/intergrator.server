package com.icl.integrator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.registration.UserCredentialsDTO;
import com.icl.integrator.services.IntegratorWorkerService;
import com.icl.integrator.services.utils.Synchronized;
import com.icl.integrator.springapi.IntegratorManagementHttpAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by BigBlackBug on 07.05.2014.
 */
@Controller
public class IntegratorManagementHttpController implements IntegratorManagementHttpAPI {

	private static Log logger = LogFactory.getLog(IntegratorManagementHttpController.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IntegratorWorkerService workerService;

	private <Result, Arg> Result fixConversion(Arg argument, TypeReference<Result> type) {
		return objectMapper.convertValue(argument, type);
	}

	@Override
	@Synchronized
	public ResponseDTO<Void> registerUser(
			@RequestBody IntegratorPacket<UserCredentialsDTO, DestinationDescriptor> packet) {
		TypeReference<IntegratorPacket<UserCredentialsDTO, DestinationDescriptor>>
				type =
				new TypeReference<IntegratorPacket<UserCredentialsDTO, DestinationDescriptor>>() {
				};
		IntegratorPacket<UserCredentialsDTO, DestinationDescriptor>
				fixed = fixConversion(packet, type);
		try {
			workerService.registerUser(fixed.getData());
			return new ResponseDTO<>(true);
		} catch (Exception ex) {
			logger.error("Ошибочка вышла при регании пользователя", ex);
			return new ResponseDTO<>(new ErrorDTO(ex));
		}
	}
}
