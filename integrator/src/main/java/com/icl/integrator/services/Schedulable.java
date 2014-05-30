package com.icl.integrator.services;

import com.icl.integrator.model.Delivery;
import com.icl.integrator.model.DeliverySettings;
import com.icl.integrator.task.TaskCreator;

public final class Schedulable<T> {

	private final TaskCreator<T> taskCreator;

	private final Delivery delivery;

	private final DeliverySettings deliverySettings;

	public Schedulable(TaskCreator<T> taskCreator,
	                   Delivery delivery,
	                   DeliverySettings deliverySettings) {
		this.taskCreator = taskCreator;
		this.delivery = delivery;
		this.deliverySettings = deliverySettings;
	}

	public Schedulable(TaskCreator<T> taskCreator,
	                   Delivery delivery) {
		this.taskCreator = taskCreator;
		this.delivery = delivery;
		DeliverySettings endpointDS = delivery.getEndpoint().getDeliverySettings();
		DeliverySettings actionDS = delivery.getAction().getDeliverySettings();
		if (actionDS != null) {
			this.deliverySettings = actionDS;
		} else {
			this.deliverySettings = endpointDS;
		}
	}

	public DeliverySettings getDeliverySettings() {
		return deliverySettings;
	}

	public TaskCreator<T> getTaskCreator() {
		return taskCreator;
	}

	public Delivery getDelivery() {
		return delivery;
	}
}