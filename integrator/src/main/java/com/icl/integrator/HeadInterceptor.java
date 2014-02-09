package com.icl.integrator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by BigBlackBug on 2/8/14.
 */
public class HeadInterceptor extends HandlerInterceptorAdapter {

	private final static Log logger = LogFactory.getLog(HeadInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request,
	                         HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getMethod() == "HEAD") {
			logger.info("Received a HEAD request. Ignoring");
			return false;
		} else {
			return true;
		}
	}
}
