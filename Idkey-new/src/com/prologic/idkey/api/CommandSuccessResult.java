package com.prologic.idkey.api;

public class CommandSuccessResult extends CommandResult {

	public CommandSuccessResult(Command command) {
		super(command);
	}

	@Override
	public String toString() {
		return getCommand().toString() + " was successful";
	}
	
}
