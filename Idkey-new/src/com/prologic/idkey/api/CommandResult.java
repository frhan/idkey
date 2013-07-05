package com.prologic.idkey.api;

public class CommandResult {

	final private Command command;
	

	public CommandResult(Command command) {
		this.command = command;
	}
	

	public Command getCommand() {
		return command;
	}
	
	@Override
	public String toString() {
		return "Result for " + command.toString();
	}
	
}
