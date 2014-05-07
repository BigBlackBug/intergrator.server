package com.icl.integrator.controllers;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.registration.UserRegistrationDTO;
import com.icl.integrator.services.IntegratorWorkerService;
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
	private IntegratorWorkerService workerService;

	@Override
	public ResponseDTO<Void> registerUser(@RequestBody UserRegistrationDTO packet) {
		try {
			workerService.registerUser(packet);
			return new ResponseDTO<>(true);
		} catch (Exception ex) {
			logger.error("Ошибочка вышла при выполнении метода", ex);
			return new ResponseDTO<>(new ErrorDTO(ex));
		}
	}
}
