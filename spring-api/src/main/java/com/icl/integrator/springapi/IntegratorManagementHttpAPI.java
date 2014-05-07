package com.icl.integrator.springapi;

import com.icl.integrator.api.IntegratorManagementAPI;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.registration.UserRegistrationDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by BigBlackBug on 07.05.2014.
 */
@RequestMapping(value = "/management/",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
public interface IntegratorManagementHttpAPI extends IntegratorManagementAPI {

	@Override
	@RequestMapping(value = "registerUser", method = RequestMethod.POST)
	public
	@ResponseBody
	ResponseDTO<Void> registerUser(@RequestBody UserRegistrationDTO packet);
}
