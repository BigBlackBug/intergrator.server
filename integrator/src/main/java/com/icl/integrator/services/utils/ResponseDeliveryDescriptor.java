package com.icl.integrator.services.utils;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.services.converters.Converter;

public class ResponseDeliveryDescriptor<ResponseClass, AcceptedBySource> {

	private Converter<ErrorDTO, AcceptedBySource> failedConverter;

	private Converter<ResponseClass, AcceptedBySource> successConverter;

	public ResponseDeliveryDescriptor(
			Converter<ErrorDTO, AcceptedBySource> failedConverter,
			Converter<ResponseClass, AcceptedBySource> successConverter) {
		this.failedConverter = failedConverter;
		this.successConverter = successConverter;
	}


	public Converter<ResponseClass, AcceptedBySource> getSuccessConverter() {
		return successConverter;
	}

	public Converter<ErrorDTO, AcceptedBySource> getFailedConverter() {
		return failedConverter;
	}
}