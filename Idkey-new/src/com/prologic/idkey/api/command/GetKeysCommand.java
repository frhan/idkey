package com.prologic.idkey.api.command;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.ApiException;
import com.prologic.idkey.api.JsonCommand;
import com.prologic.idkey.objects.Category;
import com.prologic.idkey.objects.Key;

public class GetKeysCommand extends JsonCommand {

	int userCategoryId;
	private List<Key> listKeys;
	public GetKeysCommand()
	{
		userCategoryId = -1;

	}
	public GetKeysCommand(int userCategoryId) 
	{
		this.userCategoryId = userCategoryId;
	}
	public List<Key> getListKeys() {
		return listKeys;
	}


	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {
		//http://50.57.84.233/cms/api/get_keys.json?auth_token=xM2e7dJNx5f5Ey7pxQqC
		//String url = "http://50.57.84.233/cms/api/get_keys.json?auth_token=xM2e7dJNx5f5Ey7pxQqC"; //ApiConnection.mainUrl+"get_keys.json"+"?auth_token="+Session.getInstance().getAuthToken();		
		//String url = "http://50.57.84.233/cms/api/get_keys.json?auth_token=xM2e7dJNx5f5Ey7pxQqC";
		/*		JSONObject jsonObj = null;
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
		String body = (jsonObj == null)? null : jsonObj.toString();*/

		StringBuilder url = new StringBuilder(ApiConnection.mainUrl);
		if(userCategoryId > -1)
		{
			url.append("get_keys.json");
			url.append("?user_category_id=");
			url.append(userCategoryId);
			url.append("&auth_token=");
			url.append(Session.getInstance().getAuthToken());
		}else {
			url.append("get_keys.json").append("?auth_token=").append(Session.getInstance().getAuthToken());
		}

		return apiConnection.get(url.toString());
	}

	@Override
	protected void parse(String data) throws JSONException 
	{
		try {
			JSONObject jsonObject = new JSONObject(data);

			if(jsonObject.has("user_keys") && jsonObject.get("user_keys") instanceof JSONArray)
			{
				JSONArray userKeysJsonArray = jsonObject.getJSONArray("user_keys");

				if(userKeysJsonArray.length() >0)
				{
					listKeys = new ArrayList<Key>();
					for(int i = 0; i<userKeysJsonArray.length(); i++)
					{
						if(userKeysJsonArray.get(i) instanceof JSONObject)
						{
							int id = -1;
							int category_id = -1;
							String name = "";							
							String iqe_token = "";
							String scan_url = "";
							String category_name = "";
							String created = "";

							JSONObject jsonCategoryObj = userKeysJsonArray.getJSONObject(i);
							if(jsonCategoryObj.has("id"))
							{
								id = jsonCategoryObj.getInt("id");								
							}
							if(jsonCategoryObj.has("category_id"))
							{
								category_id = jsonCategoryObj.getInt("category_id");								
							}
							if(jsonCategoryObj.has("name"))
							{
								name = jsonCategoryObj.getString("name");								
							}
							if(jsonCategoryObj.has("iqe_token"))
							{
								iqe_token = jsonCategoryObj.getString("iqe_token");								
							}
							if(jsonCategoryObj.has("scan_url"))
							{
								scan_url = jsonCategoryObj.getString("scan_url");								
							}
							if(jsonCategoryObj.has("category_name"))
							{
								category_name = jsonCategoryObj.getString("category_name");								
							}
							if(jsonCategoryObj.has("created"))
							{
								created = jsonCategoryObj.getString("created");								
							}
							if(id >-1)
							{
								Key key = new Key(id, name, created, category_name);
								key.setIqeToken(iqe_token);
								key.setCategoryId(category_id);
								key.setScanUrl(scan_url);

								listKeys.add(key);
							}
						}
					}

				}

			}
		}
		catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}


	}

}