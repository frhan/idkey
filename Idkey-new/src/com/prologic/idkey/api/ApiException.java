package com.prologic.idkey.api;

public class ApiException extends Exception
{
	private static final long serialVersionUID = -3698590320346609951L;

	final private ExceptionType type;

	public ApiException(ExceptionType type, String internal) {
		super(internal);
		this.type = type;
	}

	/**
	 * The type of exception that occurred
	 */
	public enum ExceptionType {
		Offline,
		ConnectionError,
		AuthenticationFailed, 
		CommandFailed;
	}

	public ExceptionType getType() 
	{
		return type;
	}


}
