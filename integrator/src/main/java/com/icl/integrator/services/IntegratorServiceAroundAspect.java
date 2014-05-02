package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.IntegratorPacket;
import com.icl.integrator.dto.ResponseDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IntegratorServiceAroundAspect {

	private static Log logger = LogFactory.getLog(IntegratorServiceAroundAspect.class);

	@Autowired
	private DeliveryService deliveryService;

	@Around("execution(* com.icl.integrator.services.IntegratorService.*(..))")
	public Object employeeAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
		IntegratorPacket packet = (IntegratorPacket) proceedingJoinPoint.getArgs()[0];
		Object value;
		try {
			value = proceedingJoinPoint.proceed();
		} catch (Throwable ex) {
			logger.error(ex, ex);
			value = new ResponseDTO<>(new ErrorDTO(ex));
		}
		deliveryService.deliver(packet.getResponseHandlerDescriptor(), value);
		return value;
	}
}
