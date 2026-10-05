package com.promanatia.openbravonetsuiteintegration.Exception;

public class RowValidationException extends RuntimeException {

	public RowValidationException(String message) {
		super(message);
	}

	public RowValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
