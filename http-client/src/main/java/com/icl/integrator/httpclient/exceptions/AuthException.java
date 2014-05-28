package com.icl.integrator.httpclient.exceptions;

/**
 * Created by BigBlackBug on 13.05.2014.
 */
public class AuthException extends RuntimeException {

	public AuthException() {
	}

	public AuthException(String message) {
		super(message);
	}

	public AuthException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthException(Throwable cause) {
		super(cause);
	}
}
