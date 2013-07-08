package com.prologic.idkey.api.command;

import org.json.JSONException;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;



public class SignOutCommand extends JsonCommand {

	public SignOutCommand() {
	}
	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {

		return null;
	}

	@Override
	protected void parse(String data) throws JSONException {


	}

}
