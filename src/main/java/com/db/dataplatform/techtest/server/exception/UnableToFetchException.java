package com.db.dataplatform.techtest.server.exception;

import lombok.Getter;

public class UnableToFetchException extends RuntimeException{
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	private int status;
	public UnableToFetchException(String message, int status) {
		super(message);
		this.status = status;
	}
}
