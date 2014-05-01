package com.icl.integrator.security;

import com.icl.integrator.services.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.persistence.NoResultException;

/**
 * Created by BigBlackBug on 22.04.2014.
 */
public class UserService implements UserDetailsService {

	@Autowired
	private PersistenceService service;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			return service.findUserByUsername(username);
		} catch (NoResultException nex) {
			throw new UsernameNotFoundException("Пользователь "+username+" не найден");
		}
	}
}
