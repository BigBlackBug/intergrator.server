package com.icl.integrator.services;

import com.icl.integrator.dto.IntegratorPacket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IntegratorServiceAroundAspect {

	@Autowired
	private DeliveryService deliveryService;

	@Around("execution(* com.icl.integrator.services.IntegratorService.*(..))")
	public Object employeeAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
		IntegratorPacket packet = (IntegratorPacket) proceedingJoinPoint.getArgs()[0];
		Object value = null;
		try {
			value = proceedingJoinPoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		deliveryService.deliver(packet.getResponseHandlerDescriptor(), value);
		return value;
	}
}
