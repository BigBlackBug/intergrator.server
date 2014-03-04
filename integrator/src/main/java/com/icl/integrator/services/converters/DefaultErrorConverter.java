package com.icl.integrator.services.converters;

import com.icl.integrator.dto.ErrorDTO;
import com.icl.integrator.dto.ResponseDTO;

public class DefaultErrorConverter implements Converter<ErrorDTO, ResponseDTO> {

	@Override
	public ResponseDTO convert(ErrorDTO errorDTO) {
		return new ResponseDTO(errorDTO);
	}
}