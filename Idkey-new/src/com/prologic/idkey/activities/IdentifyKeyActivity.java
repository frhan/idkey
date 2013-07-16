package com.prologic.idkey.activities;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iqengines.sdk.IQRemote;
import com.iqengines.sdk.IQRemote.IQEQuery;
import com.iqengines.sdk.Utils;
import com.prologic.idkey.R;
import com.tasawr.camera.CameraPictureSnapActivity;

public class IdentifyKeyActivity extends CameraPictureSnapActivity{

	private IQRemote iqRemote;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		
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
		
		sendRemoteToIdentify();

	}
	
	public void sendRemoteToIdentify()
	{
		if(currentFile != null)
		{
			new IdKeyTask(context).execute();
		}
	}
	private void parseIqQuryResult()
	{
		
	}

	private class IdKeyTask extends AsyncTask<Void, Void, Void>
	{

		private Context context;
		public IdKeyTask(Context context) 
		{
			this.context = context;
		}
		@Override
		protected Void doInBackground(Void... params) 
		{
			try {
				String qid = iqRemote.DefineQueryId(currentFile,null,null,Utils.getDeviceId(context),true,null,null,null);
				IQEQuery iqQuery = iqRemote.query(currentFile, Utils.getDeviceId(context), qid);
				String result = iqRemote.result(iqQuery.getQID(),true);
				if(result != null)
				{
					Log.i("ID-A-Key", result);
				}
			} catch (IOException e) {
				
				e.printStackTrace();
				Log.e("ID-A-Key", e.getMessage());
			}
			//iqRemote.query(currentFile, null, null, Utils.getDeviceId(context), true, null, null, null, queryId)

			return null;
		}
		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			mCameraview.resumePreview();
		}

	}

}
