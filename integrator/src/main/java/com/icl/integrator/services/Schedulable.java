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
		this.deliverySettings = delivery.getEndpoint()
				.getDeliverySettings();
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