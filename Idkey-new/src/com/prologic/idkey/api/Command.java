package com.prologic.idkey.api;


public abstract class Command 
{
	private  ApiMethod method;
	public static final String TAG = Command.class.getName();
	
	protected Command()
	{
		
	}

	public ApiMethod getMethod() {
		return method;
	}
	/**
	 * Execute the set up command against the forum
	 * @param apiConnection The current RateBeer API connection to execute the command against
	 * @return The command result
	 */
	public abstract CommandResult execute(ApiConnection apiConnection);

	@Override
	public String toString() {
		return method.toString();
	}	
	

}
