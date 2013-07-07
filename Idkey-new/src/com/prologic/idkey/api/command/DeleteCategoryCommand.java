package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class DeleteCategoryCommand extends JsonCommand
{
	private int categoryId;
	private String message;
	private boolean deletedSuccessfully;

	public DeleteCategoryCommand(int categoryId) {

		this.categoryId = categoryId;
		deletedSuccessfully = false;
	}
	public String getMessage() {
		return message;
	}
	public boolean isDeletedSuccessfully() {
		return deletedSuccessfully;
	}

	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {

		String url = ApiConnection.mainUrl+"delete_category.json"+"?auth_token="+Session.getInstance().getAuthToken();
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("user_category_id", categoryId);						

		} catch (JSONException e) 
		{			
			Log.e(TAG, e.getMessage());
		}
		return null;

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
						deletedSuccessfully = true;
					}
				}
			}		

		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}

	}


}
