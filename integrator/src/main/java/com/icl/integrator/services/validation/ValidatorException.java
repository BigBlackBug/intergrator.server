package com.icl.integrator.services.validation;

/**
 * Created by BigBlackBug on 2/19/14.
 */
public class ValidatorException extends RuntimeException {

	public ValidatorException() {
	}

	public ValidatorException(String message) {
		super(message);
	}

	public ValidatorException(String message, Throwable cause) {
		super(message, cause);
	}
}
