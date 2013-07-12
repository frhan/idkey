package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;

public class AddKeyCommand extends JsonCommand
{
	private String iqeId;
	private String iqeResUrl;
	private String name;
	private int categoryId;
	private String message;
	private boolean addedSuccessfully;
	public AddKeyCommand(String iqeId,String iqeResUrl,String name,int categoryId) 
	{
		this.iqeId = iqeId;
		this.iqeResUrl = iqeResUrl;
		this.name = name;
		this.categoryId = categoryId;		
		addedSuccessfully = false;
		message = "Error:Adding Error";
	}
	public String getMessage() {
		return message;
	}
	public boolean isAddedSuccessfully() {
		return addedSuccessfully;
	}

	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {

		String url = ApiConnection.mainUrl + "create_key.json"+"?auth_token="+Session.getInstance().getAuthToken();
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject();
			jsonObj.put("iqe_token", iqeId);
			jsonObj.put("scan_url", iqeResUrl);	
			jsonObj.put("name", name);
			if(categoryId >-1)
				jsonObj.put("category_id", categoryId);

		} catch (JSONException e) 
		{			
			Log.e(TAG, e.getMessage());
		}

		return apiConnection.post(url, jsonObj.toString());
	}

	@Override
	protected void parse(String data) throws JSONException 
	{
		Log.i(TAG, data);
		try 
		{
			JSONObject jsonObject = new JSONObject(data);

			if(jsonObject.has("message"))
			{
				message = jsonObject.getString("message");
			}
			if(jsonObject.has("new_key"))
			{
				addedSuccessfully = true;				
			}			

		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}


	}


}
