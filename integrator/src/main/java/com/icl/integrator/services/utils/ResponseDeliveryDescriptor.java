package com.icl.integrator.services.utils;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.services.converters.Converter;

public class ResponseDeliveryDescriptor<ResponseClass, ТипКоторыйПринимаетСорс> {

//	private DestinationEntity persistentDestination;

	//	private CallbackParams callbackParams;
//
//	public ResponseDeliveryDescriptor(
//			PersistentDestination persistentDestination,
//			CallbackParams callbackParams) {
//		this.persistentDestination = persistentDestination;
//		this.callbackParams = callbackParams;
//	}
//
//	public PersistentDestination getPersistentDestination() {
//		return persistentDestination;
//	}
//
//	public CallbackParams getCallbackParams() {
//		return callbackParams;
//	}
	private Converter<ErrorDTO, ТипКоторыйПринимаетСорс> failedConverter;

	private Converter<ResponseClass, ТипКоторыйПринимаетСорс> successConverter;

	public ResponseDeliveryDescriptor(
//			DestinationEntity persistentDestination,
			Converter<ErrorDTO, ТипКоторыйПринимаетСорс> failedConverter,
			Converter<ResponseClass, ТипКоторыйПринимаетСорс> successConverter) {
//		this.persistentDestination = persistentDestination;
		this.failedConverter = failedConverter;
		this.successConverter = successConverter;
	}

//	public DestinationEntity getPersistentDestination() {
//		return persistentDestination;
//	}

	public Converter<ResponseClass, ТипКоторыйПринимаетСорс> getSuccessConverter() {
		return successConverter;
	}

	public Converter<ErrorDTO, ТипКоторыйПринимаетСорс> getFailedConverter() {
		return failedConverter;
	}
}