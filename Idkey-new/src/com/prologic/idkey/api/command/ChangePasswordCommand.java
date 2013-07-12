package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class ChangePasswordCommand extends JsonCommand
{

	private String oldPassword;
	private String newPassword;
	private String message;
	private boolean changeSuccessfully;

	public ChangePasswordCommand(String oldPassword,String newPassword)
	{
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.message = "Error Changing Password";
		this.changeSuccessfully = false;
	}

	public String getMessage() {
		return message;
	}
	public boolean isChangeSuccessfully() {
		return changeSuccessfully;
	}

	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {
		//current_password, new_password, auth_token
		String url = ApiConnection.mainUrl+"change_password.json"+"?auth_token="+Session.getInstance().getAuthToken();

		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("current_password", oldPassword);
			jsonObj.put("new_password", newPassword);
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

				if(userObject.has("message"))
				{
					message = userObject.getString("message");
				}

				if(userObject.has("auth_token"))
				{
					changeSuccessfully = true;
				}
			}

		} catch (JSONException e)
		{
			Log.e(TAG, e.getMessage());
		}


	}


}
