package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.model.Delivery;

import java.util.*;

class Deliveries {

	private final Map<String, ResponseDTO<UUID>> errorMap = new HashMap<String, ResponseDTO<UUID>>();

	private final List<Delivery> deliveries = new ArrayList<Delivery>();

	public Deliveries() {
	}

	public void addError(String serviceName, Exception ex) {
		ErrorDTO error = new ErrorDTO(ex);
		errorMap.put(serviceName, new ResponseDTO<UUID>(error));
	}

	public void addDelivery(Delivery delivery) {
		deliveries.add(delivery);
	}

	public Map<String, ResponseDTO<UUID>> getErrorMap() {
		return errorMap;
	}

	public List<Delivery> getDeliveries() {
		return deliveries;
	}
}