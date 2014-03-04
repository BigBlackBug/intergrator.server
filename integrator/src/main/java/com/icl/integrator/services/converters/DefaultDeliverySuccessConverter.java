package com.icl.integrator.services.converters;

import com.icl.integrator.dto.ResponseDTO;
import com.icl.integrator.dto.ResponseFromTargetDTO;
import com.icl.integrator.model.Delivery;

public class DefaultDeliverySuccessConverter implements Converter<ResponseDTO, ResponseDTO> {

	private Delivery delivery;

	public DefaultDeliverySuccessConverter(Delivery delivery) {
		this.delivery = delivery;
	}

	@Override
	public ResponseDTO convert(ResponseDTO responseDTO) {
		return new ResponseDTO<>(new ResponseFromTargetDTO(
				responseDTO,
				delivery.getEndpoint().getServiceName(),
				delivery.getId().toString()));
	}
}