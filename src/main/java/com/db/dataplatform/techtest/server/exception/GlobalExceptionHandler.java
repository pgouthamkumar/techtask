package com.db.dataplatform.techtest.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(UnableToFetchException.class)
	public ResponseEntity<String> handleApplicationException(UnableToFetchException ex) {
		log.error("Recieved Error during processing",ex);
		return ResponseEntity.status(HttpStatus.valueOf(ex.getStatus())).body(ex.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleApplicationException(Exception ex) {
		log.error("Recieved Error during processing",ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
}
