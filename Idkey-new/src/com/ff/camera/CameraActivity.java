package com.ff.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.prologic.idkey.R;
import com.prologic.idkey.activities.MainActivity;

public class CameraActivity extends MainActivity implements IPictureCallback
{
	private CameraView cameraView;
	private byte []  tempData;
	private Button btnClick;
	private Button btnRetake;
	private Button btnUse;
	private AlbumStorageDirFactory albumStorageDirFactory = null;
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private String currentPhotoPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.camera_view);
		disableScreenTurnOff();

		btnClick = (Button) findViewById(R.id.btn_camera_snap);
		btnUse = (Button) findViewById(R.id.btn_pic_use);
		btnRetake = (Button)findViewById(R.id.btn_pic_retake);

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View toastView = (View) layoutInflater.inflate(R.layout.toast_view, null);

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastView);
		toast.show();

		cameraView = (CameraView) findViewById(R.id.camera_view);
		cameraView.setPictureCallback(this);
		//View v = new LinearLayout(this);
		View overlayView = (View) layoutInflater.inflate(R.layout.overlay_view, null);
		addContentView(overlayView, cameraView.getLayoutParams());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			albumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			albumStorageDirFactory = new BaseAlbumDirFactory();
		}

		tempData = null;

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

	public void onClickSnap(View v) 
	{
		if(cameraView != null)
		{
			cameraView.takePicture();

			btnClick.setVisibility(Button.GONE);
			btnRetake.setVisibility(Button.VISIBLE);
			btnUse.setVisibility(Button.VISIBLE);

		}
	}

	public void onClickRetake(View v)
	{
		if(cameraView != null)
		{
			cameraView.resumePreview();

			btnClick.setVisibility(Button.VISIBLE);
			btnRetake.setVisibility(Button.GONE);
			btnUse.setVisibility(Button.GONE);
		}
		tempData = null;

	}

	public void onClickUse(View v)
	{
		try 
		{
			saveToMemory();

		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} 

	}
	/*	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.btn_camera_snap:
			if(cameraView != null)
			{
				cameraView.takePicture();

				btnClick.setVisibility(Button.GONE);
				btnRetake.setVisibility(Button.VISIBLE);
				btnUse.setVisibility(Button.VISIBLE);

			}

			break;
		case R.id.btn_pic_retake:
			if(cameraView != null)
			{
				cameraView.resumePreview();

				btnClick.setVisibility(Button.VISIBLE);
				btnRetake.setVisibility(Button.GONE);
				btnUse.setVisibility(Button.GONE);
			}
			tempData = null;
			break;
		case R.id.btn_pic_use:

			try 
			{
				saveToMemory();
				if(currentPhotoPath != null)
				{
					//do your task
				}

			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(),
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} 

			break;

		default:
			break;
		}

	}*/
	private void saveToMemory()
	{

		FileOutputStream outStream = null;
		File f = null;
		try {
			f = setUpPhotoFile();
			currentPhotoPath = f.getAbsolutePath();
			outStream = new FileOutputStream(f); // <9>
			outStream.write(tempData);
			outStream.close();
			Log.d(TAG, "onPictureTaken - wrote bytes: " + tempData.length);

		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			currentPhotoPath = null;
		}
	}
	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		currentPhotoPath = f.getAbsolutePath();
		return f;
	}
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			storageDir = albumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}
	private String getAlbumName() {
		return getString(R.string.app_name);
	}

	@Override
	public void pictureTakenData(byte[] data, Camera camera) 
	{
		if(data != null)
		{
			tempData = data;
		}


	}
}
