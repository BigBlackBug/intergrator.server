package com.icl.integrator.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.services.DeliveryService;
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

	@Autowired
	private DeliveryService deliveryService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                    AuthenticationException exception)
			throws IOException, ServletException {
		if (exception instanceof BadCredentialsException) {
			finish(request, response, "Неверный пароль");
		} else if (exception instanceof UsernameNotFoundException) {
			finish(request, response, "Пользователя с таким именем не существует");
		} else {
			finish(request, response, exception.getMessage());
		}
	}

	private void finish(HttpServletRequest request, final HttpServletResponse response,
	                    String message) throws IOException {
		IntegratorPacket packet =
				(IntegratorPacket) request.getAttribute(AuthenticationFilter.PACKET_ATTRIBUTE);

		ResponseDTO error = new ResponseDTO(new ErrorDTO(message));
		deliveryService.deliver(packet.getResponseHandlerDescriptor(), error);

		String responseValue = mapper.writeValueAsString(error);
		response.setHeader("Content-Type","application/json; charset=UTF-8");
		response.getWriter().append(responseValue);
	}
}
