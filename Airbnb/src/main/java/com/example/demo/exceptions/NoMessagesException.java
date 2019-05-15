package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoMessagesException extends Exception {

	public NoMessagesException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NoMessagesException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public NoMessagesException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public NoMessagesException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public NoMessagesException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3636703492199777493L;

}
