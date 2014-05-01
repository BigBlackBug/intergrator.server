package com.icl.integrator.services;

import com.icl.integrator.model.AbstractEndpointEntity;
import com.icl.integrator.model.IntegratorUser;
import com.icl.integrator.model.RoleEnum;
import com.icl.integrator.util.IntegratorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by BigBlackBug on 23.04.2014.
 */
@Service
public class AuthorizationService {

	@Autowired
	private PersistenceService persistenceService;

	public boolean hasAccessToService(String serviceID, Authentication authentication) {
		IntegratorUser user = (IntegratorUser) authentication.getPrincipal();
		RoleEnum role = user.getRole().getRole();
		return role == RoleEnum.ROLE_ADMIN || role == RoleEnum.ROLE_USER
				&& checkUsername(serviceID, user.getUsername());
	}

	private boolean checkUsername(String serviceID, String username) {
		AbstractEndpointEntity endpointEntity =
				persistenceService.find(AbstractEndpointEntity.class, UUID.fromString(serviceID));
		if (endpointEntity != null) {
			return endpointEntity.getCreator().getUsername().equals(username);
		} else {
			throw new IntegratorException("Сервиса с id='" + serviceID + "' не существует");
		}
	}

}
