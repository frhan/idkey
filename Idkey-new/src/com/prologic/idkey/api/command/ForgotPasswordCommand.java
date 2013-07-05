package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class ForgotPasswordCommand extends JsonCommand {

	private String email;
	private String message;
	private String authToken;
	public ForgotPasswordCommand(String email)
	{
		this.email = email;		
		this.message = "";
		authToken = "";

	}

	public String getMessage() {
		return message;
	}
	public String getAuthToken() {
		return authToken;
	}
	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {

		String url = ApiConnection.mainUrl+"forgotten_password.json";

		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("email", email);

		} catch (JSONException e) 
		{			
			Log.e(TAG, e.getMessage());
		}		

		return apiConnection.post(url, jsonObj);
	}

	@Override
	protected void parse(String data) throws JSONException 
	{
		try {
			JSONObject jsonObject = new JSONObject(data);

			if(jsonObject.has("user") && jsonObject.get("user") instanceof JSONObject)
			{
				JSONObject userObject = jsonObject.getJSONObject("user");
				if(userObject != null)
				{
					if(userObject.has("message"))
					{
						message = userObject.getString("message");
					}

					if(userObject.has("auth_token"))
					{
						authToken = userObject.getString("auth_token");
					}
				}
			}

		}
		catch (JSONException e) 
		{
			Log.e(TAG, e.getMessage());
		}

	}



}


