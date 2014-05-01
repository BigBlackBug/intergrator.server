package com.icl.integrator.services.utils;

import org.aspectj.lang.JoinPoint;

//@Aspect
//@Component
public class LoggingAspect {

//	@Autowired
//	private DeliveryService deliveryService;

//	@AfterReturning(
//			pointcut = "execution(* com.icl.integrator.services.IntegratorService.*(..))",
//			returning = "result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		System.out.println("asdas");
		System.out.println(result);
//		IntegratorPacket packet = (IntegratorPacket) joinPoint.getArgs()[0];
//		deliveryService.deliver(packet.getResponseHandlerDescriptor(), result);
	}

}
