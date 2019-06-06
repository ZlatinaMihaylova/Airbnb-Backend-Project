package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingIsOverlapingException extends Exception {

	private static final long serialVersionUID = -5312668235458175395L;

	public BookingIsOverlapingException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BookingIsOverlapingException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public BookingIsOverlapingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public BookingIsOverlapingException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public BookingIsOverlapingException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Throwable fillInStackTrace() {
		return null;
	}
}
