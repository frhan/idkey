package com.prologic.idkey.activities;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.iqengines.sdk.IQE;
import com.iqengines.sdk.IQRemote;
import com.iqengines.sdk.OnResultCallback;
import com.iqengines.sdk.Utils;
import com.prologic.idkey.R;
import com.tasawr.camera.CameraPictureSnapActivity;

public class IdentifyKeyActivity extends CameraPictureSnapActivity{

	private IQRemote iqRemote;
	private Handler handler;
	static final boolean SEARCH_OBJECT_REMOTE = true;
	static IQE iqe;
	private AtomicBoolean activityRunning = new AtomicBoolean(false);
	private QueryProgressDialog pd;
	private String deviceId;
	public static final String NO_MATCH_FOUND_STR = "no match found";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);		

		setOverlayBackground(R.drawable.id_cameraoverlay);
		initIqSdk();
		initUI();
		handler = new Handler();
		deviceId = Utils.getDeviceId(this);

	}
	@Override
	protected void onResume() {
		super.onResume();
		activityRunning.set(true);

	}

	@Override
	protected void onStop() 
	{
		super.onStop();
		activityRunning.set(false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityRunning.set(false);

	}
	private void initUI()
	{
		pd = new QueryProgressDialog(this);
	}

	private void initIqSdk() 
	{
		iqRemote = new IQRemote(getResources().getString(R.string.iqe_app_key), getResources().getString(R.string.iqe_app_secret));

	}

	@Override
	protected void onClickUse() 
	{
		try 
		{
			saveToMemory();
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}	
		idKeyRemote();
	}

	private void idKeyRemote()
	{
		if(currentFile != null && isOnline())
		{
			showCenteredProgressDialog("Uploading...");
			processImageSnap();
		}else {
			Toast.makeText(context, "No internet Connection", Toast.LENGTH_LONG).show();
		}
	}
	private void processImageSnap() 
	{
		new SearchWithImageRemote().start();
	}


	private class QueryProgressDialog extends ProgressDialog {

		private boolean isShowing;

		public QueryProgressDialog(Activity activity) {
			super(activity);
			this.isShowing=false;
		}

		public void pdShowing(){
			this.isShowing=true;
		}

		public void pdDismissed(){
			this.isShowing=false;
		}

		public boolean isShowing(){
			return isShowing;
		}

	}

	public QueryProgressDialog showCenteredProgressDialog(final String msg) {

		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(true);
		pd.show();
		pd.pdShowing();
		handler.post(new Runnable() {
			@Override
			public void run() {
				pd.setMessage(msg);
			}
		});
		return pd;

	}

	private OnResultCallback onResultCallback = new OnResultCallback() {

		/**
		 * Called whenever a query is done (In the demo app, we call it on every
		 * snap queries and on Successful scan queries)
		 * 
		 * @param queryId
		 *            A {@link String}, the unique Id of the query.
		 * @param path
		 *            A {@link String}, the path of the picture associated with
		 *            the query.
		 * @param callType
		 *            An {@link Integer}, defines if it's a snap or a scan call.
		 */

		@Override
		public void onQueryIdAssigned(String queryId, String path) {

			handler.post(new Runnable() 
			{
				@Override
				public void run() {
					pd.setMessage("Searching key database...");
				}
			});

		}

		/**
		 * Handle the results.
		 * 
		 * @param queryId
		 *            A {@link String}, the unique Id of the query.
		 * @param objId
		 *            A {@link String}, the unique Id identifying the object on
		 *            our server. * @param objId A {@link String}, the object
		 *            label. * @param objId A {@link String}, the object
		 *            metadata. * @param objId A {@link Integer}, determines
		 *            which engine made the match (barcode, local, remote).
		 * @param callType
		 *            An {@link Integer}, defines if it's a snap or a scan call.
		 */

		@Override
		public void onResult(String queryId,final String objId, String objName,
				String objMeta) {

			final String qId = queryId;
			final String oNm = objName;


			Uri uri = null;
			// match's Metadata set as URI.
			if (objMeta != null) {

				try {
					uri = Uri.parse(objMeta);
				} catch (Exception e1) {
					uri = null;
				}
			}
			// if no Metadata : match's name set as URI.
			if (uri == null) {

				if (objName != null) {

					try {
						uri = Uri.parse(objName);
					} catch (Exception e1) {
						uri = null;
					}
				}
			}
			final Uri fUri = uri;
			handler.post(new Runnable() {
				@Override
				public void run() {
					// process and display the results
					processSearchResult(qId, oNm, objId);
				}
			});
		}

		private void processSearchResult(String searchId, String label,String objId) 
		{	
			if(!activityRunning.get())
				return;

			if(pd.isShowing)
			{
				pd.dismiss();
				pd.pdDismissed();
			}

			if(label.equals(NO_MATCH_FOUND_STR))
			{
				onNoMatches();
			}else 
			{
				Bundle b = new Bundle();
				b.putString(ShowIdentifiedKeyActivity.IQE_TOKEN, objId);
				b.putString(ShowIdentifiedKeyActivity.OBJ_NAME, label);

				setCurrent(com.prologic.idkey.activities.ShowIdentifiedKeyActivity.class, b);				
			}


		}

		private void onNoMatches()
		{
			showOkCancelAlertDailog("No match found.Do you want to:","Id a key","Go to Home", "Add key Now", false, new IDailogOKClickListener() {

				@Override
				public void onOkClick() {

					Intent intent = new Intent(context, com.prologic.idkey.activities.HomeScreenActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);

				}

				@Override
				public void onCancelClick() 
				{			
					setCurrent(com.tasawr.camera.CameraPictureSnapActivity.class, null);
					finish();
				}
			});

		}

		/**
		 * When no match are found, or exception occurs.
		 * 
		 * 
		 */

		@Override
		public void onNoResult(Exception e, File imgFile) {

			// if an exception occured
			if (e != null) {
				if (e instanceof IOException) {
					Log.w("Result", "Server call failed", e);
					handler.post(new Runnable() {
						@Override
						public void run() {
							//handler.removeCallbacks(postponedToastAction);
							Toast.makeText(
									IdentifyKeyActivity.this,
									"Unable to connect to the server. "
											+ "Check your intenet connection.",
											Toast.LENGTH_LONG).show();
							pd.dismiss();
							pd.pdDismissed();
							//unfreezePreview();
						}
					});
				} else {
					Log.e("Result", "Unable to complete search", e);
				}
				return;
			}		
		}
	};

	private class SearchWithImageRemote extends Thread{

		@Override
		public void run() 
		{
			String qid;
			try {
				qid = iqRemote.DefineQueryId(currentFile, null, null, deviceId, true, null, null, null);
				onResultCallback.onQueryIdAssigned(qid, currentFile.getPath());				
				iqRemote.query(currentFile, deviceId,qid);
				sleep(1000);

				String result = iqRemote.update(deviceId, true);
				processUpdateResult(result);


			} catch (IOException e) {
				onResultCallback.onNoResult(e,currentFile);
				return;
			}
			catch (InterruptedException e) {
				onResultCallback.onNoResult(e,currentFile);
				return;
			}	

		}

	}

	private void processUpdateResult(final String resultStr)
	{
		JSONObject results = null;

		try {

			if (resultStr == null)
				return;

			try {
				JSONObject result = new JSONObject(resultStr);
				if (!result.has("data"))
				{
					return;
				}
				results = result.getJSONObject("data");
			} catch (JSONException e) {
				Log.w("IDKEY", "Can't parse result", e);
				return;
			}
		} catch (Exception  e) {
			Log.w("IDKEY", "Server call failed", e);
			return;
		}

		try {

			if (results != null && results.has("error"))
			{
				final int error = results.getInt("error");
				if (error != 0) {
					Log.e("Id key", "Server return error: " + error);
					return;
				}


				final JSONArray resultsList = results.getJSONArray("results");
				for (int i = 0, lim = resultsList.length(); i < lim; ++i) 
				{

					final JSONObject resultObj = resultsList.getJSONObject(0);
					final String qid = resultObj.optString("qid");

					if (qid == null) {
						Log.e("Id key", "update result qid is null");
						return;
					}

					final JSONObject qidData = resultObj.getJSONObject("qid_data");

					final String labels;
					final String meta;
					final String objId;

					if (qidData.length() > 0) {
						labels = qidData.optString("labels", null);
						meta = qidData.optString("meta", null);
						objId = qidData.optString("obj_id", null);
					} else {
						labels = NO_MATCH_FOUND_STR;
						meta = null;
						objId = null;
					}
					Result result;

					if (onResultCallback != null) 
					{
						result = new Result(qid,objId,labels, meta);
						onResultCallback.onResult(result.queryId, result.objId, result.objName, result.objMeta);
					}
				}
			}
		} catch (JSONException e) {
			Log.e("IdKey", "JSON error", e);
		}

	}

	class Result {
		public String queryId;
		public String objId;
		public String objName;
		public String objMeta;
		public int engine;

		public Result(String queryId,String objId,String objName,String objMeta){
			this.queryId=queryId;
			this.objId=objId;
			this.objMeta=objMeta;
			this.objName=objName;
			this.engine= -1;
		}

		public Result(String queryId,String objId,String objName,String objMeta, int engine){
			this.queryId=queryId;
			this.objId=objId;
			this.objMeta=objMeta;
			this.objName=objName;
			this.engine=engine;
		}

	}

}
