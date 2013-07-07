package com.prologic.idkey.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.prologic.idkey.api.command.Session;

import android.util.Log;



public class WebService {

	private static final String TAG = WebService.class.getName();		
	public static final String mainUrl = "http://50.57.84.233/cms/api/";
	private static WebService webService = null;
	public WebService() 
	{
	}
	public static WebService getInstance()
	{
		if(webService == null)
			webService = new WebService();
		return webService;		
	}
	private static  HttpResponse getPostResponse(String url,List<NameValuePair> nameValuePairs)
	{
		HttpResponse response = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			HttpPost httpPost = new HttpPost(url); 

			httpPost.setEntity(new  UrlEncodedFormEntity(nameValuePairs, "utf-8"));

			response = httpClient.execute(httpPost);		

		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
		catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
		}
		catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return response;
	}

	private static  HttpResponse getPostResponse(String url,JSONObject jsonObject)
	{
		HttpResponse response = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			HttpPost httpPost = new HttpPost(url); 
			StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
			//httpPost.setEntity(jsonObject.toString(),"utf-8");


			response = httpClient.execute(httpPost);		

		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
		catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
		}
		catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return response;
	}

	public static HttpResponse getDeleteResponseWithBody(String url,String body)
	{
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
		HttpResponse response = null;

		HttpDeleteWithBody delete = new HttpDeleteWithBody(url);

		StringEntity se;
		try {
			se = new StringEntity(body, HTTP.UTF_8);
			se.setContentType("application/json");
			delete.setEntity(se); 
			response = client.execute(delete);
		}catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return response;

	}

	public boolean deleteCategory(int id)
	{
		boolean isDeletedSuccessfully = false;
		try {
			String url = ApiConnection.mainUrl+"delete_category.json"+"?auth_token="+Session.getInstance().getAuthToken();;
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("user_category_id",id);
			HttpResponse response = getDeleteResponseWithBody(url, jsonObj.toString());

			if(response != null && response.getStatusLine().getStatusCode() == HttpsURLConnection.HTTP_OK)
			{
				String result =   EntityUtils.toString(response.getEntity());
				Log.i(TAG, result);

				if(result != null && result.length() > 0)
				{
					Log.i(TAG, result);
					isDeletedSuccessfully = parseJsonForDelete(result);

				}
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}catch (Exception e) 
		{
			Log.e(TAG, e.getMessage());
		}
		return isDeletedSuccessfully;
	}

	private boolean parseJsonForDelete(String data)
	{
		boolean deletedSuccessfully = false;
		try {
			JSONObject jsonObject = new JSONObject(data);
			if(jsonObject.has("message"))
			{
				String message = jsonObject.getString("message");
				if(message != null &&message.length() > 0)
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
		return deletedSuccessfully;
	}


	protected void sendJsonForDeleteCategory(String url,String id) 
	{

		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
		HttpResponse response;

		url=url+"?auth_token="+Session.getInstance();
		HttpDeleteWithBody delete = new HttpDeleteWithBody(url);
		String param="{\"user_category_id\":\""+id+"\"}";

		StringEntity se;
		try {
			se = new StringEntity(param, HTTP.UTF_8);
			se.setContentType("application/json");
			delete.setEntity(se); 
			response = client.execute(delete);

			if(response!=null)
			{
				InputStream in = response.getEntity().getContent(); //Get the data in the entity
				//Log.d("delete request******",convertStreamToString(in));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void setupSimpleRequestBody(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, String body) throws UnsupportedEncodingException {
		if (body != null) {
			httpEntityEnclosingRequestBase.setHeader("Content-Type", "application/json; charset=utf-8");
			httpEntityEnclosingRequestBase.setEntity(new StringEntity(body, "UTF-8"));
		}
	}
	public void signUp(String email,String password)
	{
		try {
			String signUpUrl = mainUrl+"remote_signup.json";
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("email", email);
			jsonObj.put("password", password);
			jsonObj.put("password_confirmation", password);

			HttpResponse response = getPostResponse(signUpUrl, jsonObj);
			if(response != null && response.getStatusLine().getStatusCode() == HttpsURLConnection.HTTP_OK)
			{
				String result =   EntityUtils.toString(response.getEntity());
				Log.i(TAG, result);

				if(result != null && result.length() > 0)
				{
					Log.i(TAG, result);

				}
			}

		} catch (Exception e) {

		}
	}

}
