package com.icl.integrator.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by BigBlackBug on 13.05.2014.
 */
public class IntegratorAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                    AuthenticationException exception)
			throws IOException, ServletException {
		if (exception instanceof BadCredentialsException) {
			finish(response, "Неверный пароль");
		} else if (exception instanceof UsernameNotFoundException) {
			finish(response, "Пользователя с таким именем не существует");
		} else {
			finish(response, exception.getMessage());
		}
	}

	private void finish(final HttpServletResponse response, String message)
			throws IOException {
		String responseValue = mapper.writeValueAsString(new ResponseDTO(new ErrorDTO(message)));
		response.setHeader("Content-Type","application/json; charset=UTF-8");
		response.getWriter().append(responseValue);
	}
}
