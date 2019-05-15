package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoomNotFoundException extends Exception {

	private static final long serialVersionUID = 1379150189683956548L;

	public RoomNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RoomNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public RoomNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RoomNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RoomNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
