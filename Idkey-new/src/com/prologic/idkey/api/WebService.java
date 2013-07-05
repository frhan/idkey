package com.prologic.idkey.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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
