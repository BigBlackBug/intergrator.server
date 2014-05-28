package com.icl.integrator.api;

import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.registration.UserCredentialsDTO;

/**
 * Created by BigBlackBug on 07.05.2014.
 */
public interface IntegratorManagementAPI {

	public ResponseDTO<Void> registerUser(
			IntegratorPacket<UserCredentialsDTO, DestinationDescriptor> packet);

}
