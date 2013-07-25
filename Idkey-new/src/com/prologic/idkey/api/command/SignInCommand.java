package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class SignInCommand extends JsonCommand 
{
	private String email;
	private String pass;
	private String message;
	private boolean signInSuccessfull;
	
	public SignInCommand(String email,String pass) 
	{
		this.email = email;
		this.pass = pass; 
		message = "Log in Failed";
		signInSuccessfull = false;
		
	}
	public boolean isSignInSuccessfull() {
		return signInSuccessfull;
	}
	
	public String getMessage() {
		return message;
	}

	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {
		
		String url = ApiConnection.mainUrl+"remote_signin.json";
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("email", email);
			jsonObj.put("password", pass);
		} catch (JSONException e) 
		{			
			Log.e(TAG, e.getMessage());
		}
		return apiConnection.post(url, jsonObj.toString());
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
					String authToken = null;
					if(userObject.has("message"))
					{
						message = userObject.getString("message");
					}
					if(userObject.has("id"))
					{
						int id = userObject.getInt("id");
						if(id>0)
							Session.getInstance().setId(id);						
					}

					if(userObject.has("email"))
					{
						String email = userObject.getString("email");
						Session.getInstance().setEmail(email);
					}
					if(userObject.has("auth_token"))
					{
						authToken = userObject.getString("auth_token");						
						Session.getInstance().setAuthToken(authToken);
					}

					if(authToken != null)
					{
						signInSuccessfull = true;
					}
				}

			}

		} catch (JSONException e)
		{
			Log.e(TAG, e.getMessage());

		}


	}

}
