package com.ff.camera;

import com.prologic.idkey.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class CameraActivity extends Activity 
{
	private CameraView cameraView;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.camera_view);
		disableScreenTurnOff();

		cameraView = (CameraView) findViewById(R.id.camera_view);

	}
	@Override
	protected void onPause() {		

		if(cameraView != null)
		{
			cameraView.stopPreview(true);			
		}
		super.onPause();
	}

	@Override
	protected void onResume() {	
		if(cameraView != null)
		{
			cameraView.resumePreview();
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		if(cameraView != null)
		{
			cameraView.stopPreview(true);	
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {		
		super.onDestroy();

		if(cameraView != null)
		{
			cameraView.stopPreview(false);
		}
	}

	/**
	 * Avoid that the screen get's turned off by the system.
	 */
	public void disableScreenTurnOff() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	/**
	 * Set's the orientation to landscape, as this is needed by AndAR.
	 */
	public void setOrientation()  {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
}
