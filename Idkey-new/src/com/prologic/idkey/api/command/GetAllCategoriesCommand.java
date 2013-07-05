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

public class GetAllCategoriesCommand extends JsonCommand 
{
	
	private List<Category> categories;
	public GetAllCategoriesCommand() 
	{
		categories = null;
	}
	
	public List<Category> getCategories() 
	{
		
		return categories;
	}

	@Override
	protected String makeRequest(ApiConnection apiConnection)
			throws ApiException {

		String url = ApiConnection.mainUrl+"get_categories.json"+"?auth_token="+Session.getInstance().getAuthToken();		
		return apiConnection.get(url);
	}	

	@Override
	protected void parse(String data) throws JSONException 
	{
		try {
			JSONObject jsonObject = new JSONObject(data);
			
			if(jsonObject.has("user_categories") && jsonObject.get("user_categories") instanceof JSONArray)
			{
				JSONArray categoryArray = jsonObject.getJSONArray("user_categories");
				
				if(categoryArray.length() >0)
				{
					categories = new ArrayList<Category>();
					
					for(int i = 0; i<categoryArray.length(); i++)
					{
						if(categoryArray.get(i) instanceof JSONObject)
						{
							int id = -1;
							String categoryName = "";
							int count = 0;
							JSONObject jsonCategoryObj = categoryArray.getJSONObject(i);
							if(jsonCategoryObj.has("id"))
							{
								id = jsonCategoryObj.getInt("id");								
							}
							if(jsonCategoryObj.has("count"))
							{
								count = jsonCategoryObj.getInt("count");								
							}
							if(jsonCategoryObj.has("category_name"))
							{
								categoryName = jsonCategoryObj.getString("category_name");								
							}
							
							if(id > -1)
							{
								Category category = new Category(id,categoryName,count);
								categories.add(category);
							}
							
						}
					}
				}
			}
			
		} catch (JSONException e) 
		{
			Log.e(TAG, e.getMessage());
		}


	}

}
