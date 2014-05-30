package com.icl.integrator.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.destination.DestinationDescriptor;
import com.icl.integrator.dto.registration.UserCredentialsDTO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public static final String DEFAULT_URL = "/login";

	public static final String PACKET_ATTRIBUTE = "packet";

	@Autowired
	private ObjectMapper mapper;

	public AuthenticationFilter() {
		super(DEFAULT_URL);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
	                                            HttpServletResponse response) throws
			AuthenticationException, IOException {
		//TODO add packet validation. навесить фильтр с валидацией до логина
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException(
					"Authentication method not supported: " + request.getMethod());
		}

		String json = IOUtils.toString(request.getInputStream());
		logger.info(json);
		TypeReference<IntegratorPacket<UserCredentialsDTO, DestinationDescriptor>> type =
				new TypeReference<IntegratorPacket<UserCredentialsDTO, DestinationDescriptor>>() {
				};
		IntegratorPacket<UserCredentialsDTO, DestinationDescriptor> packet = mapper.readValue(json, type);

		request.setAttribute(PACKET_ATTRIBUTE, packet);

		UserCredentialsDTO credentials = packet.getData();
		UsernamePasswordAuthenticationToken authRequest =
				new UsernamePasswordAuthenticationToken(credentials.getUsername(),
				                                        credentials.getPassword());
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	protected void setDetails(HttpServletRequest request,
	                          UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}
}