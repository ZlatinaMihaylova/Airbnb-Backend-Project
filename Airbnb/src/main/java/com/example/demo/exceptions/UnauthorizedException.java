package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3987363861083083023L;

	public UnauthorizedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UnauthorizedException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public UnauthorizedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public UnauthorizedException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public UnauthorizedException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
