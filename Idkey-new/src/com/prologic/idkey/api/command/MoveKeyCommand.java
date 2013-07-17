package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class MoveKeyCommand extends JsonCommand
{

	private int currentCategoryId;
	private int newCategoryId;
	private String message;
	private boolean movdeSuccessfully;

	
	public MoveKeyCommand(int currentCategoryId,int newCategoryId) 
	{
		this.currentCategoryId = currentCategoryId;
		this.newCategoryId = newCategoryId;		
		this.message = "Error Changing the category";
		this.movdeSuccessfully = false;
	}
	public String getMessage() {
		return message;
	}
	public boolean isMovedSuccessfully() {
		return movdeSuccessfully;
	}
	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {
		String url = ApiConnection.mainUrl+"move_keys.json"+"?auth_token="+Session.getInstance().getAuthToken();
		
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("current_category_id", currentCategoryId);
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
				movdeSuccessfully = true;				
			}
			
		} catch (JSONException e) 
		{
			Log.e(TAG, e.getMessage());
		}		
		
	}
	

}
