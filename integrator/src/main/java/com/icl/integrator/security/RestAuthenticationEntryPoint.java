package com.icl.integrator.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public void commence(final HttpServletRequest request, final HttpServletResponse response,
	                     final AuthenticationException authException) throws IOException {
		String responseValue = mapper.writeValueAsString(
				new ResponseDTO(new ErrorDTO("Вы не авторизованы", ErrorCode.UNAUTHORIZED)));
		response.setHeader("Content-Type", "application/json; charset=UTF-8");
		response.getWriter().append(responseValue);
	}

}