package com.icl.integrator.model;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by BigBlackBug on 23.04.2014.
 */
public interface HasCreator {

	public UserDetails getCreator();

}
