package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class CreateCategoryCommand extends JsonCommand{

	private String name;
	private String message;
	private int id;
	private boolean hasCreatedSuccessfully;

	public CreateCategoryCommand(String name) 
	{
		this.name = name;
		this.message = "";
		hasCreatedSuccessfully = false;
		this.id = -1;
	}

	public boolean isCreatedSuccessfully() {
		return hasCreatedSuccessfully;
	}

	public String getMessage() {
		return message;
	}

	public int getId() {
		return id;
	}

	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {
		String url = ApiConnection.mainUrl + "create_category.json"+"?auth_token="+Session.getInstance().getAuthToken();
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("new_user_category", name);

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

			if(jsonObject.has("message"))
			{
				message = jsonObject.getString("message");
			}
			if(jsonObject.has("user_category") && jsonObject.get("user_category") instanceof JSONObject)
			{
				JSONObject userCategoryObject = jsonObject.getJSONObject("user_category");

				if(userCategoryObject.has("id"))
				{
					id = userCategoryObject.getInt("id");
					if(id >-1)
						hasCreatedSuccessfully = true;					
				}				
			}

		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}

	}

}
