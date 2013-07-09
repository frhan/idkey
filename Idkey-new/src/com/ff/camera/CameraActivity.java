package com.ff.camera;

import com.prologic.idkey.R;
import com.prologic.idkey.activities.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CameraActivity extends MainActivity 
{
	private CameraView cameraView;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.camera_view);
		disableScreenTurnOff();
		
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View toastView = (View) layoutInflater.inflate(R.layout.toast_view, null);

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastView);
		toast.show();

		cameraView = (CameraView) findViewById(R.id.camera_view);
		//View v = new LinearLayout(this);
		View overlayView = (View) layoutInflater.inflate(R.layout.overlay_view, null);
		addContentView(overlayView, cameraView.getLayoutParams());

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
