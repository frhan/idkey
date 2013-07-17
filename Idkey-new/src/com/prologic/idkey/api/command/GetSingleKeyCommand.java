package com.prologic.idkey.api.command;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;
import com.prologic.idkey.objects.Key;

public class GetSingleKeyCommand extends JsonCommand{

	private String iqeToken;
	private Key key;
	
	public GetSingleKeyCommand(String iqeToken)
	{
		this.iqeToken = iqeToken;		
		key = null;
	}
	
	public Key getKey() {
		return key;
	}
	
	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {
		
		StringBuilder url = new StringBuilder(ApiConnection.mainUrl);
		url.append("get_key.json");
		url.append("?auth_token=");
		url.append(Session.getInstance().getAuthToken());
		url.append("&iqe_token=");
		url.append(iqeToken);		
		
		return apiConnection.get(url.toString());
	}

	@Override
	protected void parse(String data) throws JSONException 
	{
		try {
			
			JSONObject jsonObject = new JSONObject(data);
		} catch (JSONException e)
		{
			Log.e(TAG, e.getMessage());
			
		}
		
	}


}
