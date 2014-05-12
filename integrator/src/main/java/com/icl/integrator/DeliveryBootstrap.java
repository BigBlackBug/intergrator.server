package com.icl.integrator;

import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.registration.ActionMethod;
import com.icl.integrator.model.Delivery;
import com.icl.integrator.model.DeliveryStatus;
import com.icl.integrator.services.DeliveryService;
import com.icl.integrator.services.PersistenceService;
import com.icl.integrator.services.converters.DefaultDeliverySuccessConverter;
import com.icl.integrator.services.converters.DefaultErrorConverter;
import com.icl.integrator.services.utils.ResponseDeliveryDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by BigBlackBug on 2/13/14.
 */
@Component
public class DeliveryBootstrap implements ApplicationListener<ContextRefreshedEvent> {

	private static Log logger = LogFactory.getLog(DeliveryBootstrap.class);

	private boolean isInitialized = false;

	@Autowired
	private PersistenceService persistenceService;

	@Autowired
	private DeliveryService deliveryService;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (isInitialized) {
			return;
		}
		//TODO remove
		persistenceService.createDefaultUser();
		List<Delivery> deliveries = persistenceService.findAllUnfinishedDeliveries();
		for (Delivery delivery : deliveries) {
			delivery.setRequestDate(new Date());
			delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
			delivery = persistenceService.saveOrUpdate(delivery);
			if (delivery.getAction().getActionMethod() == ActionMethod.HANDLE_DELIVERY) {
				DefaultErrorConverter errorConverter = new DefaultErrorConverter();
				DefaultDeliverySuccessConverter successConverter =
						new DefaultDeliverySuccessConverter(delivery);
				deliveryService.deliver(delivery, ResponseDTO.class,
				                        new ResponseDeliveryDescriptor<>(
						                        errorConverter, successConverter));
			} else {
				deliveryService.deliver(delivery, Void.class);
			}
		}
		isInitialized = true;
	}


}