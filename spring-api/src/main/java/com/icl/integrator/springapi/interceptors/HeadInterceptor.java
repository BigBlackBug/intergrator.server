package com.icl.integrator.springapi.interceptors;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.ActionMethod;
import com.icl.integrator.dto.registration.RegistrationResultDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by BigBlackBug on 2/8/14.
 */
public class HeadInterceptor extends HandlerInterceptorAdapter {

	private final static Log logger = LogFactory.getLog(HeadInterceptor.class);

	private static final Map<ActionMethod, Type> types;

	static {
		types = new HashMap<>();
		types.put(ActionMethod.HANDLE_DELIVERY, new ParameterizedTypeReference<RequestDataDTO>() {
		}.getType());
		types.put(ActionMethod.HANDLE_RESPONSE_FROM_TARGET,
		          new ParameterizedTypeReference<ResponseDTO<ResponseFromTargetDTO>>() {
		          }.getType());
		types.put(ActionMethod.HANDLE_DELIVERY_RESPONSE,
		          new ParameterizedTypeReference<Map<String, ResponseDTO<UUID>>>() {
		          }.getType());
		types.put(ActionMethod.HANDLE_SERVER_REGISTRATION_RESPONSE,
		          new ParameterizedTypeReference<ResponseDTO<RegistrationResultDTO>>() {
		          }.getType());
		types.put(ActionMethod.HANDLE_SERVICE_IS_AVAILABLE,
		          new ParameterizedTypeReference<ResponseDTO<Boolean>>() {
		          }.getType());
		types.put(ActionMethod.HANDLE_GET_SERVER_LIST,
		          new ParameterizedTypeReference<ResponseDTO<List<ServiceDTO>>>() {
		          }.getType());
		types.put(ActionMethod.HANDLE_GET_SUPPORTED_ACTIONS,
		          new ParameterizedTypeReference<ResponseDTO<List<String>>>() {
		          }.getType());
		types.put(ActionMethod.HANDLE_ADD_ACTION,
		          new ParameterizedTypeReference<ResponseDTO<Void>>() {
		          }.getType());
		types.put(ActionMethod.HANDLE_GET_SERVICE_INFO,
		          new ParameterizedTypeReference<ResponseDTO<FullServiceDTO<ActionDescriptor>>>() {
		          }.getType());
		types.put(ActionMethod.HANDLE_AUTO_DETECTION_REGISTRATION_RESPONSE,
		          new ParameterizedTypeReference<ResponseDTO<List<ResponseDTO<Void>>>>() {
		          }.getType());
		types.put(ActionMethod.HANDLE_PING, new ParameterizedTypeReference<ResponseDTO<Boolean>>() {
		}.getType());
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
	                         HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getMethod().equals("HEAD")) {
			logger.info("Received a HEAD request. Ignoring");
			Type realType = ((HandlerMethod) handler).getMethod().getGenericParameterTypes()[0];
			String actionMethod = request.getParameter("action_method");

			Type requiredType = types.get(ActionMethod.valueOf(actionMethod));
			if (!requiredType.equals(realType)) {
				response.sendError(422, "Выбранный метод не подходит для этого действия. Правильная сигнатура "+
						requiredType.toString());
			}
			return false;
		} else {
			return true;
		}
	}
}
