package com.icl.integrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.services.validation.PacketValidationException;
import com.icl.integrator.services.validation.ValidationService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ValidationFilter implements Filter {

	private static Log logger =
			LogFactory.getLog(ValidationFilter.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ValidationService validationService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
	                     FilterChain chain) throws IOException, ServletException {

		MultiReadHttpServletRequest multiReadRequest =
				new MultiReadHttpServletRequest((HttpServletRequest) request);

		String json = IOUtils.toString(multiReadRequest.getInputStream());
		logger.info(json);
		try {
			validationService.validateIntegratorPacket(json);
		} catch (PacketValidationException pvex) {
			logger.info("Ошибка валидации пакета", pvex);
			response.setContentType("application/json");
			ResponseDTO error = new ResponseDTO(pvex);
			String responseJson = objectMapper.writeValueAsString(error);
			response.setContentLength(responseJson.length());
			response.getWriter().write(responseJson);
			return;
		}
		chain.doFilter(multiReadRequest, response);
	}

	@Override
	public void destroy() {

	}
}