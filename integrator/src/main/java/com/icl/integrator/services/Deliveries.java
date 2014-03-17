package com.icl.integrator.services;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.model.Delivery;

import java.util.*;

class Deliveries {

	private final Map<String, ResponseDTO<String>> errorMap = new HashMap<>();

	private final List<Delivery> deliveries = new ArrayList<>();

	public Deliveries() {
	}

	public boolean isEmpty(){
		return deliveries.isEmpty();
	}

	public void addError(String serviceName, Exception ex) {
		ErrorDTO error = new ErrorDTO(ex);
		errorMap.put(serviceName, new ResponseDTO<String>(error));
	}

	public void addDelivery(Delivery delivery) {
		deliveries.add(delivery);
	}

	public Map<String, ResponseDTO<String>> getErrorMap() {
		return errorMap;
	}

	public List<Delivery> getDeliveries() {
		return deliveries;
	}
}