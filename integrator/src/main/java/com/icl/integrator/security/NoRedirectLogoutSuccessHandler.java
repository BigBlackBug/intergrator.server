package com.icl.integrator.security;

import com.icl.integrator.model.IntegratorUser;
import com.icl.integrator.services.VersioningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by BigBlackBug on 23.04.2014.
 */
public class NoRedirectLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired
	private VersioningService versioningService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
	                            Authentication authentication)
			throws IOException, ServletException {
		IntegratorUser integratorUser = (IntegratorUser) (authentication.getPrincipal());
		versioningService.logout(integratorUser.getUsername());
	}
}
