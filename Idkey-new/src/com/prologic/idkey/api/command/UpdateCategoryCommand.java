package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class UpdateCategoryCommand extends JsonCommand 
{
	private int categoryId;
	private String updateName;
	private String message;
	private boolean updatedSuccessfully;
	public UpdateCategoryCommand(int categoryId,String updateName) 
	{
		this.categoryId = categoryId;
		this.updateName = updateName;
		updatedSuccessfully = false;
	}

	public String getMessage() {
		return message;
	}

	public boolean isUpdatedSuccessfully() {
		return updatedSuccessfully;
	}
	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {

		String url = ApiConnection.mainUrl+"update_category.json"+"?auth_token="+Session.getInstance().getAuthToken();
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("user_category_id", categoryId);
			jsonObj.put("name", updateName);			

		} catch (JSONException e) 
		{			
			Log.e(TAG, e.getMessage());
		}
		return apiConnection.put(url, jsonObj.toString());
	}

	@Override
	protected void parse(String data) throws JSONException
	{
		try {
			JSONObject jsonObject = new JSONObject(data);
			if(jsonObject.has("message"))
			{
				message = jsonObject.getString("message");
				if(message.length() > 0)
				{
					if(message.split(":")[0].trim().equalsIgnoreCase("SUCCESS"))
					{
						updatedSuccessfully = true;
					}
				}
			}		

		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
