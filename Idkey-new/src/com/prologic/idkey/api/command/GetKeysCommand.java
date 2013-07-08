package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class GetKeysCommand extends JsonCommand {

	int userCategoryId;
	public GetKeysCommand()
	{
		userCategoryId = -1;
		
	}
	public GetKeysCommand(int userCategoryId) 
	{
		this.userCategoryId = userCategoryId;
	}
	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {
	//http://50.57.84.233/cms/api/get_keys.json?auth_token=xM2e7dJNx5f5Ey7pxQqC
		String url = "http://50.57.84.233/cms/api/get_keys.json?auth_token=xM2e7dJNx5f5Ey7pxQqC"; //ApiConnection.mainUrl+"get_keys.json"+"?auth_token="+Session.getInstance().getAuthToken();		
		JSONObject jsonObj = null;
		if(userCategoryId >-1)
		{			
			try {
				jsonObj = new JSONObject();
				jsonObj.put("user_category_id", userCategoryId);
			
			} catch (JSONException e) 
			{			
				Log.e(TAG, e.getMessage());
			}
		}
		return apiConnection.get(url,jsonObj.toString());
	}

	@Override
	protected void parse(String data) throws JSONException {
		
		
	}

}
