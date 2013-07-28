package com.iqengines.sdk;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.YuvImage;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

/**
 * Facade for providing unified access to local and remote search functionality
 * 
 * @author smineyev
 *
 */
public class IQE extends Handler {

	/*
	 * Callback interface that IQE uses to notify client about each search step 
	 */

	public interface OnResultCallback {
		/**
		 * This method gets called by IQE when unique query id is assigned to search query
		 * 
		 * @param queryId 
		 * 		  A {@link String} representing the ID assigned to this search query.
		 * @param pathName
		 *        A {@link String} the local path to the picture's data.
		 * @param remoteSearch
		 * 		  A {@link Integer}, defines what sent the query (snap, scan ...).       
		 */
		public void onQueryIdAssigned(String queryId, String pathName, int callType);

		/**
		 * This method gets called by IQE whenever search result is available.
		 * 
		 *  @param queryId
		 *  	   A {@link String} giving the query ID of the search query.
		 *  @param objId
		 *  	   A {@link String} giving the object ID of match found by IQE. null if no match found.
		 *  @param objName
		 *  	   A {@link String} which is the object name (label) of the match found by IQE.  null if no match found.
		 *  @param objMeta
		 *  	   A {@link String}which are object meta information of the match found by IQE.  null if no match found.
		 *  @param Barcode
		 *  	   A {@link Integer}, gives the engine matching (local, remote, barcode).   
		 *  @param e 
		 *         An {@link Integer}, defines what sent the query (snap, scan ...). 
		 */
		public void onResult(String queryId, String objId, String objName, String objMeta, int Engine, int callType);

		/**
		 *This method gets called when no results are found
		 *  
		 *  @param snap
		 *  	   A {@link boolean} if true, the query has been started with a snapped button. If false the query has been started with continuous scanning.
		 *  @param e 
		 *      An {@link Exception} that occurred during the search. null is search finished without exceptions.
		 *  @param imgFile
		 *  	   The
		 */

		public void onNoResult(int callType, Exception e, File imgFile);
	}

	/**
	 * Tells if remote search is enable.
	 */
	private boolean remoteSearch;




	/**
	 * Interface Between the UI and the different engines.
	 */

	public static final int CMD_DECODE=1;

	//public static final int CMD_DECODE_LOCAL=2;

	//public static final int CMD_DECODE_BARCODE=3;

	public static final int CMD_DECODE_REMOTE=4;

	//public static final int CMD_SUCCESS_BARCODE=5;

	//public static final int CMD_SUCCESS_LOCAL = 6;

	public static final int CMD_SUCCESS_REMOTE = 7;

	public static final int CMD_RESULT = 8;

	public static final int CMD_NO_RESULT = 9;

	public static final int CMD_COMPRESS_IMG = 10;

	public static final int CMD_SERVER_RECEIVED =11;

	//public static final int CMD_NO_RESULT_BARCODE  =12;

	//public static final int CMD_NO_RESULT_LOCAL = 13;


	/**
	 * Call types.
	 */

	//public static final int scan = 1;

	public static final int snap = 2;

	private int callType;

	/**
	 * Tells if it's a 1D or 2D barcode.
	 */

	public static final int oneDFormat = 1;

	public static final int twoDFormat = 2;

	public static final int remote = 3;

	private static boolean DEBUG = true;

	private static boolean isLastEngineSnap = false;

	private static boolean isLastEngineScan = false;

	private static String TAG = IQE.class.getName();

	public static final String NO_MATCH_FOUND_STR = "no match found";

	/**
	 * This get rids of some border issues.
	 */
	private int compteur = 0;

	private int secondCompteur = 0;



	private IQRemote iqRemote;

	private Activity activity;

	private String deviceId;

	private AtomicBoolean stopScanSearch = new AtomicBoolean(false);

	private AtomicBoolean indexInitialized = new AtomicBoolean(false);

	private Object newIncomingRemoteMatchSemaphore = new Object();

	private RemoteResultUpdateThread remoteUpdateThread;

	private Map<String, OnResultCallback> remoteQueryMap = Collections
			.synchronizedMap(new HashMap<String, IQE.OnResultCallback>());

	private OnResultCallback onResultCallback;

	/**
	 * Constructor 
	 * 
	 * @param activity
	 * 		  The {@link Activity} within the query is made.
	 * @param remoteSearch
	 * 		  A {@link Boolean} whether remote search is enabled.
	 * @param localSearch
	 * 		  A {@link Boolean} whether local search is enabled.
	 * @param remoteKey
	 * 		  A {@link String} : Unique developer's key for accessing IQ Engines web search. (IQ Engines.com->developer center->settings)
	 * @param remoteSecret
	 * 		  A {@link String} : Developer's secret key for accessing IQ Engines web search. (IQ Engines.com->developer center->settings)
	 */


	public IQE(Activity activity, boolean remoteSearch,OnResultCallback onResultCallback, 
			String remoteKey, String remoteSecret) {

		if (!remoteSearch) { 
			throw new IllegalArgumentException("At least one type of search must be enabled");
		}
		this.onResultCallback=onResultCallback;
		this.activity = activity;
		this.remoteSearch = remoteSearch;

		deviceId = Utils.getDeviceId(activity);

		initIqSdk(remoteKey, remoteSecret);
	}

	/**
	 * Initialize the different tools of the IQE class.
	 * 
	 * @param remoteKey
	 * 		a {@link String}, you key to access your database.
	 * @param remoteSecret
	 * 		a {@link String}, the other part of the key.
	 */


	private void initIqSdk(String remoteKey, String remoteSecret) {

		if (remoteSearch) {
			iqRemote = new IQRemote(remoteKey, remoteSecret);
		}

		synchronized (indexInitialized) {
			indexInitialized.notifyAll();
		}

	}


	/**
	 * Interface between the activity and IQE tools.
	 * Call the CMD_DECODE with a {@link message} including the {@link YuvImage} to process as an object and an {@link Integer}
	 * Representing the context as the first argument.
	 */


	public void handleMessage(Message message) {

		switch (message.what) {

		case CMD_DECODE:
			obtainMessage(CMD_COMPRESS_IMG,IQE.snap, 0,(File) message.obj).sendToTarget();
			obtainMessage(CMD_DECODE_REMOTE).sendToTarget();
			break;

		case CMD_DECODE_REMOTE:

			if (remoteSearch){

				new SearchWithImageRemote().start();
				if (DEBUG) Log.d(TAG,"Searching remotely");
			}
			break;
		case CMD_COMPRESS_IMG:
			imgFile = (File) message.obj;
			break;

			/**CMD_SUCCESS_BARCODE
			 * message :
			 * obj = String label
			 * arg1 = callType
			 * arg2 = Barcode dimension
			 */

		case CMD_SUCCESS_REMOTE:

			// nb : "no match found" also considered as a successful remote match

			if (DEBUG) Log.d(TAG, "-------------- REMOTE MATCH ---------------");
			sendMessageAtFrontOfQueue (obtainMessage(CMD_RESULT,message.arg1, 0, (Result) message.obj));
			break;

			/**CMD_RESULT
			 * message :
			 * obj = result
			 * arg1 = callType
			 */

		case CMD_RESULT:

			switch(message.arg1)
			{
			case(snap):
				Result resultSnap = (Result) message.obj;
			onResultCallback.onResult(resultSnap.queryId, resultSnap.objId, resultSnap.objName, resultSnap.objMeta, resultSnap.engine, message.arg1);
			break;
			}
			break;

			/**CMD_NO_RESULT
			 * message :
			 * obj = result
			 * arg1 = callType
			 */

		case CMD_NO_RESULT:

			// ugly hack to get rid of some bugs
			if(message.arg2 == 0) break;
			if(message.arg2 == 1){
				if (secondCompteur == 0)
					secondCompteur ++ ;
				else
					break;}

			if (DEBUG) Log.d(TAG, "-------------- NO MATCH FOUND ---------------");
			onResultCallback.onNoResult(message.arg1,null,imgFile);
			break;

			/**CMD_NO_RESULT_BARCODE
			 * message :
			 * obj = YuvImage 
			 * arg1 = callType
			 */

		}
	}




	/**
	 * Searches on IQ Engines server. this Method upload the query to our server and instantiate the updating Thread.
	 * Method blocks caller while it's submitting query to server but actual result is delivered later using update-API. 
	 * 
	 * CMD_SUCCESS_REMOTE is called when results are ready.
	 * 
	 * @param imgFile
	 * 		  A {@link File} containing an image to find match for.
	 * @param onResultCallback
	 *        An {@link OnResultCallback} object to be called when query id is assigned and when result is found.
	 */

	// this is were snap picture to be uploaded are stored.
	private File imgFile;

	private class SearchWithImageRemote extends Thread{

		@Override
		public void run() {

			if (!remoteSearch) {
				throw new IllegalStateException("remoteSearch is disabled");
			}

			String qid;

			try {
				qid = iqRemote.DefineQueryId(imgFile, null, null, deviceId, true, null, null, null);
				onResultCallback.onQueryIdAssigned(qid, imgFile.getPath(),snap);
				remoteQueryMap.put(qid, onResultCallback);
				iqRemote.query(imgFile, deviceId,qid);
				sleep(1000);
				String result = iqRemote.update(deviceId, true);


			} catch (IOException e) {
				onResultCallback.onNoResult(snap,e,imgFile);
				return;
			}
			catch (InterruptedException e) {
				onResultCallback.onNoResult(snap,e,imgFile);
				return;
			}
			//release the thread looking for server response 
			synchronized (newIncomingRemoteMatchSemaphore) {
				newIncomingRemoteMatchSemaphore.notifyAll();
			}
			if (DEBUG) {	
				Log.d(TAG, "remote query qid: " + qid);
			}

		}

	}

	/**
	 * Method is to be called when user wants to resume update-thread. 
	 * Usually called from Activity.onResume.
	 */


	public void resume()
	{
		if (remoteSearch)
		{
			remoteUpdateThread = new RemoteResultUpdateThread(this, "RemoteResultUpdateThread");
			remoteUpdateThread.start();
		}
	}


	/**
	 * Method is to be called when user wants to stop update-thread. 
	 * Usually called from Activity.onPause.
	 */


	public void pause()
	{
		if (remoteSearch)
		{
			remoteUpdateThread.stopThread();
			remoteUpdateThread = null;
		}

		removeAllMessages();
		synchronized (newIncomingRemoteMatchSemaphore) {
			newIncomingRemoteMatchSemaphore.notifyAll();           
		}
	}

	/**
	 * Method is to be called when user wants to destroy IQE (to free-up memory).
	 * Usually called from Activity.onDestroy.
	 */

	public synchronized void destroy() {

		removeAllMessages();

		iqRemote = null;

	}


	/**
	 * Checks whether local search index is initialized and ready for use.
	 * 
	 * @return true if local search index is initialized.
	 */

	public boolean isIndexInitialized() {
		return indexInitialized.get();
	}

	/**
	 * Thread looking for results from IQ Engines server for the remote research.
	 * It also processes the data given by the server.
	 * 
	 * Calls CMD_SUCCESS_REMOTE when results are ready.
	 * 		
	 * @throws RunTimeException 
	 */

	private class RemoteResultUpdateThread extends Thread {

		private IQE iqe;

		volatile private boolean run = true;

		public RemoteResultUpdateThread(IQE iqe, String string){
			super(string);
			this.iqe=iqe;
		}

		public synchronized void stopThread()
		{
			run = false;
		}

		@Override
		public void run() {

			JSONObject results = null;

			while (run) {
				synchronized (newIncomingRemoteMatchSemaphore) {
					try {
						newIncomingRemoteMatchSemaphore.wait(1000);
					} catch (InterruptedException e) {
						new RuntimeException(e);
					}
				}
				if (run == false)
					break;

				try {
					String resultStr = null;
					resultStr = iqRemote.update(deviceId, true);
					if (resultStr == null)
						continue;
					if (DEBUG)
						Log.d(TAG, "update: " + resultStr);
					try {
						JSONObject result = new JSONObject(resultStr);
						if (!result.has("data")){
							continue;
						}
						results = result.getJSONObject("data");
					} catch (JSONException e) {
						Log.w(TAG, "Can't parse result", e);
						continue;
					}
				} catch (IOException e) {
					Log.w(TAG, "Server call failed", e);
					continue;
				}

				try {

					if (results.has("error")) {
						final int error = results.getInt("error");
						if (error != 0) {
							Log.e(TAG, "Server return error: " + error);
							continue;
						}
					}

					final JSONArray resultsList = results.getJSONArray("results");
					for (int i = 0, lim = resultsList.length(); i < lim; ++i) {

						final JSONObject resultObj = resultsList.getJSONObject(i);
						final String qid = resultObj.optString("qid");

						if (qid == null) {
							Log.e(TAG, "update result qid is null");
							continue;
						}

						final JSONObject qidData = resultObj.getJSONObject("qid_data");

						final String labels;
						final String meta;

						if (qidData.length() > 0) {
							labels = qidData.optString("labels", null);
							meta = qidData.optString("meta", null);
						} else {
							labels = NO_MATCH_FOUND_STR;
							meta = null;
						}

						Message remoteResults = null;
						Result result;

						if (onResultCallback != null) {
							result = new Result(qid,null,labels, meta, remote);
							remoteResults = obtainMessage(CMD_SUCCESS_REMOTE,IQE.snap,0,result);
							iqe.sendMessageAtFrontOfQueue (remoteResults);
						} else {	

							onResultCallback = remoteQueryMap.get(qid);

							if (onResultCallback != null) {

								result = new Result(qid,null,labels, meta, remote);
								remoteResults = obtainMessage(CMD_SUCCESS_REMOTE,IQE.snap,0, result) ;
								iqe.sendMessageAtFrontOfQueue (remoteResults);
							} else {
								Log.w(TAG, "OnResultCallback is null for qid: " + qid);
							}
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, "JSON error", e);
				}
			}
		}
	}

	/**
	 *Class used to pass the result information through the messages.
	 */

	class Result {
		public String queryId;
		public String objId;
		public String objName;
		public String objMeta;
		public int engine;

		public Result(String queryId,String objId,String objName,String objMeta, int engine){
			this.queryId=queryId;
			this.objId=objId;
			this.objMeta=objMeta;
			this.objName=objName;
			this.engine=engine;
		}

	}

	/**
	 * Removes all the messages from the messageQueue
	 */

	public void removeAllMessages(){
		removeMessages(CMD_DECODE, null);
		removeMessages(CMD_DECODE_REMOTE, null);
		removeMessages(CMD_SUCCESS_REMOTE, null);
		removeMessages(CMD_RESULT, null);
		removeMessages(CMD_NO_RESULT, null);
		removeMessages(CMD_COMPRESS_IMG, null);
		removeMessages(CMD_SERVER_RECEIVED,null);
	}


}