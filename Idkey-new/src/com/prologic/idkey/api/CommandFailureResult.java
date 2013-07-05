package com.prologic.idkey.api;

public class CommandFailureResult extends CommandResult {

	final private ApiException exception;

	public CommandFailureResult(Command command, ApiException exception) {
		super(command);
		this.exception = exception;
	}

	public ApiException getException() {
		return exception;
	}

	@Override
	public String toString() {
		return getCommand().toString() + " failed: " + exception.toString();
	}
	
}
