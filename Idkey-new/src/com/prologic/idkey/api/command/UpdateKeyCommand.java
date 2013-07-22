package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class UpdateKeyCommand extends JsonCommand{

	private String message;
	private String keyName;
	private int newCategoryId;
	private int userKeyId;
	private boolean updatedSuccessfully;
	
	public UpdateKeyCommand(int userKeyId,String keyName,int newCategoryId) 
	{
		this.keyName = keyName;
		this.newCategoryId = newCategoryId;
		this.message = "Update Failed";
		this.userKeyId = userKeyId;		
		updatedSuccessfully = false;
	}

	
	public UpdateKeyCommand(int userKeyId,String keyName) 
	{
		this(userKeyId,keyName, -1);		
	}
	public boolean isUpdatedSuccessfully() {
		return updatedSuccessfully;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {
	
	String url = ApiConnection.mainUrl+"update_key.json"+"?auth_token="+Session.getInstance().getAuthToken();
		
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("user_key_id", userKeyId);
			jsonObj.put("name", keyName);
			if(newCategoryId > -1)
				jsonObj.put("new_category_id", newCategoryId);
			
		} catch (JSONException e) 
		{			
			Log.e(TAG, e.getMessage());
		}
		return apiConnection.put(url, jsonObj.toString());
	}

	@Override
	protected void parse(String data) throws JSONException 
	{
		try 
		{
			JSONObject jsonObject = new JSONObject(data);
			if(jsonObject.has("message"))
			{
				message = jsonObject.getString("message");
			
			}
			if(jsonObject.has("auth_token"))
			{
				updatedSuccessfully = true;				
			}
			
		} catch (JSONException e) 
		{
			Log.e(TAG, e.getMessage());
		}	
		
	}

	
}
