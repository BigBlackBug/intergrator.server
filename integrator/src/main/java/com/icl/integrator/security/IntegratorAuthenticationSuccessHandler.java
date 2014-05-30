package com.icl.integrator.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.services.VersioningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IntegratorAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private VersioningService versioningService;

	private RequestCache requestCache = new HttpSessionRequestCache();

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request,
	                                    final HttpServletResponse response,
	                                    final Authentication authentication) throws
			ServletException, IOException {
		//TODO считать из пакета и отослать результат
		versioningService.login(authentication.getName());

		final SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest == null) {
			finish(request, response);
			return;
		}

		final String targetUrlParameter = getTargetUrlParameter();
		if (isAlwaysUseDefaultTargetUrl() || (targetUrlParameter != null
				&& StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
			requestCache.removeRequest(request, response);
			finish(request, response);
			return;
		}

		finish(request, response);
	}

	private void finish(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		clearAuthenticationAttributes(request);
		response.setHeader("Content-Type", "application/json; charset=UTF-8");
		String responseValue = mapper.writeValueAsString(new ResponseDTO(true));
		response.getWriter().append(responseValue);
	}

	public void setRequestCache(final RequestCache requestCache) {
		this.requestCache = requestCache;
	}
}
