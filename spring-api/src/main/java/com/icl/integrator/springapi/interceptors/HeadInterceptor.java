package com.icl.integrator.springapi.interceptors;

import com.icl.integrator.dto.*;
import com.icl.integrator.dto.registration.ActionDescriptor;
import com.icl.integrator.dto.registration.RegistrationResultDTO;
import com.icl.integrator.dto.source.EndpointDescriptor;
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

	private static final Map<DeliveryType, Type> types;

	static {
		types = new HashMap<>();
		types.put(DeliveryType.TARGET_METHOD, new ParameterizedTypeReference<RequestDataDTO>() {
		}.getType());
		types.put(DeliveryType.HANDLE_RESPONSE_FROM_TARGET,
		          new ParameterizedTypeReference<ResponseDTO<ResponseFromTargetDTO>>() {
		          }.getType());
		types.put(DeliveryType.HANDLE_DELIVERY,
		          new ParameterizedTypeReference<Map<String, ResponseDTO<UUID>>>() {
		          }.getType());
		types.put(DeliveryType.HANDLE_SERV_REG,
		          new ParameterizedTypeReference<ResponseDTO<RegistrationResultDTO>>() {
		          }.getType());
		types.put(DeliveryType.HANDLER_SER_IS_AVAIL,
		          new ParameterizedTypeReference<ResponseDTO<Boolean>>() {
		          }.getType());
		types.put(DeliveryType.HANDLE_GET_SERLIST,
		          new ParameterizedTypeReference<ResponseDTO<List<ServiceDTO>>>() {
		          }.getType());
		types.put(DeliveryType.HANDLER_GET_SUPP_ACTIONS,
		          new ParameterizedTypeReference<ResponseDTO<List<String>>>() {
		          }.getType());
		types.put(DeliveryType.HANDLE_ADD_ACTION,
		          new ParameterizedTypeReference<ResponseDTO<Void>>() {
		          }.getType());
		types.put(DeliveryType.HANDLE_GET_SERV_INFO,
		          new ParameterizedTypeReference<ResponseDTO<FullServiceDTO<EndpointDescriptor, ActionDescriptor>>>() {
		          }.getType());              //TODO TETS
		types.put(DeliveryType.HANDLE_AUTO_REG,
		          new ParameterizedTypeReference<ResponseDTO<List<ResponseDTO<Void>>>>() {
		          }.getType());
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
	                         HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getMethod().equals("HEAD")) {
			logger.info("Received a HEAD request. Ignoring");
			Type realType = ((HandlerMethod) handler).getMethod().getGenericParameterTypes()[0];
			String deliveryType = request.getParameter("DELIVERY_TYPE");

			Type requiredType = types.get(DeliveryType.valueOf(deliveryType));
			if (!requiredType.equals(realType)) {
				response.sendError(422, "Выбранное действие не поддерживается");
			}
			return false;
		} else {
			return true;
		}
	}
}
