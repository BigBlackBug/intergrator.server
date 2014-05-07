package com.icl.integrator.api;

import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.registration.UserRegistrationDTO;

/**
 * Created by BigBlackBug on 07.05.2014.
 */
public interface IntegratorManagementAPI {

	public ResponseDTO<Void> registerUser(UserRegistrationDTO packet);

}
