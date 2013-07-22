package com.prologic.idkey.api.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
	private SimpleDateFormat dateTimeFormat;
	private int keyNo;
	public GetKeysCommand()
	{
		this(-1);	
		

	}
	public GetKeysCommand(int userCategoryId) 
	{
		this.userCategoryId = userCategoryId;
		dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss-SS:S",Locale.US);
		keyNo = 1;
	}
	public List<Key> getListKeys() {
		return listKeys;
	}


	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {

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
							Date date = null;
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
								try {
									date = dateTimeFormat.parse(created);								
								
								} catch (ParseException e) 
								{									
									e.printStackTrace();
									Log.e(TAG, e.getMessage());
								}
								
							}
							if(id >-1)
							{
								Key key = new Key(id, name, created, category_name);
								key.setIqeToken(iqe_token);
								key.setCategoryId(category_id);
								key.setScanUrl(scan_url);
								key.setCreatedDate(date);
								key.setNo(keyNo++);								
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
