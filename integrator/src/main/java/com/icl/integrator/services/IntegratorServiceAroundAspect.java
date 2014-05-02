package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.model.IntegratorUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IntegratorServiceAroundAspect {

	//TODO move to props
	private static final String errorMessage =
			"Состояние Вашего клиента не совпадает с состоянием сервера." +
					"Пожалуйста вызовите метод update, чтобы получить изменения," +
					" совершённые с момента последнего обновления";

	private static Log logger = LogFactory.getLog(IntegratorServiceAroundAspect.class);

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private VersioningService versioningService;

	@Around("execution(* com.icl.integrator.services.IntegratorService.*(..))")
	public Object employeeAroundAdvice(ProceedingJoinPoint pjp) {
		IntegratorPacket packet = (IntegratorPacket) pjp.getArgs()[0];
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		IntegratorUser user = (IntegratorUser) authentication.getPrincipal();
		Object value;
		if (versioningService.isAllowedToContinue(user.getUsername())) {
			synchronized (IntegratorServiceAroundAspect.class) {
				if (versioningService.isAllowedToContinue(user.getUsername())) {
					try {
						value = pjp.proceed();
					} catch (Throwable ex) {
						logger.error("Ошибочка вышла при выполнении метода " +
								             pjp.getSignature().getName(), ex);
						value = new ResponseDTO<>(new ErrorDTO(ex));
					}
				} else {
					value = new ResponseDTO<>(new ErrorDTO(errorMessage));
				}
			}
		} else {
			value = new ResponseDTO<>(new ErrorDTO(errorMessage));
		}
		deliveryService.deliver(packet.getResponseHandlerDescriptor(), value);
		return value;
	}

}
