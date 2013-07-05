package com.prologic.idkey.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;
import org.pixmob.httpclient.HttpClient;
import org.pixmob.httpclient.HttpClientException;
import org.pixmob.httpclient.HttpRequestBuilder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.prologic.idkey.api.ApiException.ExceptionType;
		

public class ApiConnection 
{
	private static final int TIMEOUT = 6000;
	private static final int RETRIES = 3;
	public static final String mainUrl = "http://50.57.84.233/cms/api/";
	//public static final String mainUrl = "http://192.168.9.102:3000";
	protected android.util.Log Log;
	protected ConnectivityManager connectivityManager;
	private HttpClient httpClient;
	private static ApiConnection apiConnection = null;
	private Context context;

	private ApiConnection(Context context) 
	{
		this.context = context;
		httpClient = new HttpClient(context);
		httpClient.setConnectTimeout(TIMEOUT);
		httpClient.setReadTimeout(TIMEOUT);		
	}

	public static ApiConnection getInstance(Context context)
	{
		if(apiConnection == null)
			apiConnection = new ApiConnection(context);
		return apiConnection;		
	}

	/**
	 * Execute a POST HTTP request given some parameter. Note that this calls post(url, parameters, expectedHttpCode)
	 * with HttpURLConnection.HTTP_OK.
	 * @param url The url to post to
	 * @param parameters The names and values to post as parameters
	 * @return The server response data as String
	 * @throws ApiException Thrown when a connection exception occurs, including when the user is offline
	 */
	public String post(String url, List<? extends NameValuePair> parameters) throws ApiException {
		return post(url, parameters, HttpURLConnection.HTTP_OK);
	}
	
	public String post(String url, JSONObject jsonObject) throws ApiException {
		return post(url, jsonObject, HttpURLConnection.HTTP_OK);
	}
	
	public String post(String url,JSONObject jsonObject, int expectedHttpCode) throws ApiException {
		HttpRequestBuilder post = httpClient.post(url).expect(expectedHttpCode);
		/*for (NameValuePair pair : parameters) {
			post.param(pair.getName(), pair.getValue());
		}*/
		post.jsonParam(jsonObject);
		return readStream(executeRequest(post));
	}
	/**
	 * Execute a POST HTTP request given some parameters and a specific expected HTTP response code
	 * @param url The url to post to
	 * @param parameters The names and values to post as parameters
	 * @param expectedHttpCode The HTTP code that the caller expects will be returned (normally
	 *            HttpURLConnection.HTTP_OK)
	 * @return The server response data as String
	 * @throws ApiException Thrown when a connection exception occurs, including when the user is offline
	 */
	public String post(String url, List<? extends NameValuePair> parameters, int expectedHttpCode) throws ApiException {
		HttpRequestBuilder post = httpClient.post(url).expect(expectedHttpCode);
		for (NameValuePair pair : parameters) {
			post.param(pair.getName(), pair.getValue());
		}
		return readStream(executeRequest(post));
	}
	/**
	 * Execute a POST HTTP request given some parameters and a file to upload
	 * @param url The url to post to
	 * @param parameters The names and values to post as parameters
	 * @param file The file to upload
	 * @param fileFieldName The name of the file upload field in the html form
	 * @return The server response data as String
	 * @throws ApiException Thrown when a connection exception occurs, including when the user is offline
	 */
	public String postFile(String url, List<? extends NameValuePair> parameters, File file, String fileFieldName)
			throws ApiException {

		// Prepare a POST request to url
		HttpRequestBuilder prepared = httpClient.post(url);
		for (NameValuePair pair : parameters) {
			prepared.param(pair.getName(), pair.getValue());
		}

		// Build a multipart http POST request where the content type and file contents are written to
		final String BOUNDARY = "xxxxxxxxxx";
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(bytes);
		byte[] requestBody = null;
		try {
			dataOut.writeBytes("--");
			dataOut.writeBytes(BOUNDARY);
			dataOut.writeBytes("\r\n");
			dataOut.writeBytes("Content-Disposition: form-data; name=\"" + fileFieldName + "\"; filename=\""
					+ file.getName() + "\"\r\n");
			// dataOut.writeBytes("Content-Type: text/xml; charset=utf-8\r\n");
			dataOut.writeBytes("Content-Type: application/octet-stream\r\n");
			dataOut.writeBytes("\r\n");
			FileInputStream inputStream = new FileInputStream(file);
			byte[] buffer = new byte[4096]; // 4kB buffer should be enough, as the photo uploads are only small
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				dataOut.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			dataOut.writeBytes("\r\n");
			dataOut.writeBytes("\r\n--" + BOUNDARY + "--\r\n");
			dataOut.flush();
			requestBody = bytes.toByteArray();
		} catch (IOException ex) {
			throw new ApiException(ExceptionType.ConnectionError, "Could not read or write the file bytes."
					+ ex.toString());
		} finally {
			if (bytes != null) {
				try {
					bytes.close();
				} catch (IOException e) {
				}
			}
		}

		if (requestBody == null)
			throw new ApiException(ExceptionType.ConnectionError,
					"Could not build a request body for the multipart file upload.");
		// Set the content bytes (multipart form data and file data) on the POST request
		prepared.content(requestBody, "multipart/form-data; boundary=" + BOUNDARY);
		// Perform the POST request
		return readStream(executeRequest(prepared));

	}

	/**
	 * Execute a GET HTTP request for some url
	 * @param url The url to get
	 * @return The server response data as String
	 * @throws ApiException Thrown when a connection exception occurs, including when the user is offline
	 */
	public String get(String url) throws ApiException {
		return readStream(getRaw(url));
	}

	/**
	 * Execute a GET HTTP request for some url and a specific expected HTTP code
	 * @param url The url to get
	 * @param expectedHttpCode The HTTP code that the caller expects will be returned (normally
	 *            HttpURLConnection.HTTP_OK)
	 * @return The server response data as String
	 * @throws ApiException Thrown when a connection exception occurs, including when the user is offline
	 */
	public String get(String url, int expectedHttpCode) throws ApiException {
		return readStream(executeRequest(httpClient.get(url).expect(expectedHttpCode)));
	}

	/**
	 * Execute a GET HTTP request for some url and retrieve the data as input stream
	 * @param url The url to get
	 * @return The raw server response data stream
	 * @throws ApiException Thrown when a connection exception occurs, including when the user is offline
	 */
	public InputStream getRaw(String url) throws ApiException {
		return executeRequest(httpClient.get(url).expect(HttpURLConnection.HTTP_OK));
	}

	protected InputStream executeRequest(HttpRequestBuilder prepared) throws ApiException {

		// Check if we are even connected to a network
		if (!isConnected())
			throw new ApiException(ExceptionType.Offline,
					"User is not connected to a network (as reported by the system)");

		// Now try to execute the http request; if it fails for some reach we retry at most RETRIES times
		for (int i = 0; i < RETRIES; i++) {
			try {
				//
				return prepared.execute().getPayload();
			} catch (HttpClientException e) {
				Log.i("TAG", "GET failed: " + e.toString()
						+ " (now retry)");
				// Retry
			}
		}

		throw new ApiException(ExceptionType.ConnectionError, "We tried " + RETRIES + " times but the request to "
				+ prepared.toString() + " still failed.");

	}

	public static String readStream(InputStream is) throws ApiException {
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(is, "windows-1252");
		} catch (UnsupportedEncodingException e1) {
			throw new ApiException(ExceptionType.ConnectionError,
					"HTTP stream was received but it does not seem to be in the expected windows-1252 encoding");
		}
		BufferedReader reader = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			android.util.Log.e("TAG",
					"HTTP InputStream received but an IO exception occured when reading it.");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// Wasn't opened in the first place: ignore this case
			}
		}
		return sb.toString();
	}

	/**
	 * Returns whether the user has an active internet connection. Note that it migth still be unstable or not actually
	 * connected to the internet (such as a local-only wifi network).
	 * @return Returns true if the system reports there is an active network connection
	 */
	public boolean isConnected() {

		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = null;

		netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(netInfo != null && netInfo.isConnectedOrConnecting()){
			return true;
		}

		else{
			netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				return true;
			}
		}
		return false;
	}

}
