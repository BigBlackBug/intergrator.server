package com.icl.integrator;

import com.icl.integrator.model.Delivery;
import com.icl.integrator.model.DeliveryStatus;
import com.icl.integrator.services.DeliveryService;
import com.icl.integrator.services.PersistenceService;
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
public class DeliveryBootstrap implements
		ApplicationListener<ContextRefreshedEvent> {

	public static final String BASE_PACKAGE =
			"com.icl.ios.registration.card.model.dataBase";

	private static Log logger =
			LogFactory.getLog(DeliveryBootstrap.class);

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
		List<Delivery> deliveries = persistenceService.findAllUnfinishedDeliveries();
		for(Delivery delivery:deliveries){
			delivery.setRequestDate(new Date());
			delivery.setDeliveryStatus(DeliveryStatus.ACCEPTED);
			delivery = persistenceService.saveOrUpdate(delivery);
			deliveryService.deliver(delivery);
		}
		isInitialized = true;
	}


}