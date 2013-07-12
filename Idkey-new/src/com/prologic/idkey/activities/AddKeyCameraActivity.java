package com.prologic.idkey.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ff.camera.CameraActivity;
import com.ff.camera.CameraView;
import com.prologic.idkey.R;

public class AddKeyCameraActivity extends CameraActivity{

	private Button btnClick;
	private Button btnRetake;
	private Button btnUse;
	public static final String IMAGE_PATH = "IMAGE_PATH";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_camera_view);

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View toastView = (View) layoutInflater.inflate(R.layout.toast_view, null);

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastView);
		toast.show();

		cameraView = (CameraView) findViewById(R.id.add_camera_view);
		cameraView.setPictureCallback(this);
		btnClick = (Button) findViewById(R.id.btn_camera_snap);
		btnUse = (Button) findViewById(R.id.btn_pic_use);
		btnRetake = (Button)findViewById(R.id.btn_pic_retake);
		//View v = new LinearLayout(this);
		View overlayView = (View) layoutInflater.inflate(R.layout.overlay_view, null);
		addContentView(overlayView, cameraView.getLayoutParams());
	}

	@Override
	public void onClickUse(View v) 
	{
		super.onClickUse(v);
		if(getCurrentPhotoPath() != null)
		{
			Bundle b = new Bundle();
			b.putString(IMAGE_PATH, getCurrentPhotoPath());
			setCurrent(com.prologic.idkey.activities.AddKeyActivity.class, b);
		}
	}

	@Override
	public void onClickSnap(View v) {
		super.onClickSnap(v);
		btnClick.setVisibility(Button.GONE);
		btnRetake.setVisibility(Button.VISIBLE);
		btnUse.setVisibility(Button.VISIBLE);
	}

	@Override
	public void onClickRetake(View v) {
		super.onClickRetake(v);

		btnClick.setVisibility(Button.VISIBLE);
		btnRetake.setVisibility(Button.GONE);
		btnUse.setVisibility(Button.GONE);
	}
}
