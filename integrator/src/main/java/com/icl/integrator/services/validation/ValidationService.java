package com.icl.integrator.services.validation;

import com.icl.integrator.dto.DeliveryDTO;
import com.icl.integrator.dto.DeliveryType;
import com.icl.integrator.dto.RequestDataDTO;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BigBlackBug on 2/12/14.
 */
@Service
public class ValidationService {

	private Map<DeliveryType, Validator<RequestDataDTO>> validatorMap = new HashMap<>();

	@PostConstruct
	private void init() {
	}

	//	private Map<>
	public boolean validate(DeliveryDTO packet) throws ValidationException{
		return true;  //TODO
	}

	private static interface Validator<T>{
		public boolean validate(T packet);
	}

}
