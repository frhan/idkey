package com.prologic.idkey.api;

import org.json.JSONException;

import android.util.Log;


public abstract class JsonCommand extends Command{

	protected JsonCommand()
	{	
		
	}
	
	@Override
	public CommandResult execute(ApiConnection apiConnection) 
	{		
		try {
			String json = makeRequest(apiConnection);
			//parse(new JSONArray(json));
			Log.i(TAG, json);
			parse(json);
			return new CommandSuccessResult(this);
		} catch (JSONException e) {
			return new CommandFailureResult(this, new ApiException(ApiException.ExceptionType.CommandFailed,
					"JSON parsing error: " + e.toString()));
		} catch (ApiException e) {
			return new CommandFailureResult(this, e);
		}
	}
	
	protected abstract String makeRequest(ApiConnection apiConnection) throws ApiException;

	protected abstract void parse(String data) throws JSONException;

}
