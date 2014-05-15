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

	@Around("@annotation(com.icl.integrator.services.utils.Synchronized)")
	public Object synchronizationAroundAdvice(ProceedingJoinPoint pjp) {
		IntegratorPacket packet = (IntegratorPacket) pjp.getArgs()[0];
		Object value;
		synchronized (IntegratorServiceAroundAspect.class) {
			try {
				value =  pjp.proceed();
			} catch (Throwable ex) {
				logger.error("Ошибочка вышла при выполнении метода " +
						             pjp.getSignature().getName(), ex);
				return new ResponseDTO<>(new ErrorDTO(ex));
			}
			deliveryService.deliver(packet.getResponseHandlerDescriptor(), value);
		}
		return value;
	}

}
