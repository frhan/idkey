package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class SignUpCommand extends JsonCommand
{

	private String email;
	private String password;
	private String message;
	private boolean signUpSuccessfull;

	public SignUpCommand(String email,String password)
	{
		this.email = email;
		this.password = password;
		signUpSuccessfull = false;
		message = "Sign Up failed";
	}
	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {

		String url = ApiConnection.mainUrl+"remote_signup.json";
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("email", email);
			jsonObj.put("password", password);
			jsonObj.put("password_confirmation", password);
		} catch (JSONException e) 
		{			
			Log.e(TAG, e.getMessage());
		}
		return apiConnection.post(url, jsonObj.toString());

	}

	public boolean isSignUpSuccessfull() {
		return signUpSuccessfull;
	}
	public String getMessage() {
		return message;
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
						signUpSuccessfull = true;
					}
				}

			}

		} catch (JSONException e)
		{
			Log.e(TAG, e.getMessage());

		}


	}


}
