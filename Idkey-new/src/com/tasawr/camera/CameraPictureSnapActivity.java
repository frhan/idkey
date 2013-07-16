package com.tasawr.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
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

import com.ff.camera.AlbumStorageDirFactory;
import com.ff.camera.BaseAlbumDirFactory;
import com.ff.camera.FroyoAlbumDirFactory;
import com.prologic.idkey.R;
import com.prologic.idkey.activities.AddKeyActivity;
import com.prologic.idkey.activities.AddKeyCameraActivity;
import com.prologic.idkey.activities.MainActivity;
import com.tasawr.camera.CameraView;
import com.tasawr.camera.IPictureCallback;

public class CameraPictureSnapActivity extends MainActivity implements IPictureCallback,OnClickListener
{
	private Button btnClick,btnRetake,btnUse;
	private CameraView mCameraview;
	private byte[] tempData;
	private String TAG = "Camera";
	public static final String SELECTED_IMAGE_PATH = "IMAGE_PATH";
	private AlbumStorageDirFactory albumStorageDirFactory = null;
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private String currentPhotoPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		disableScreenTurnOff();

		setContentView(R.layout.camera_view);

		btnClick = (Button) findViewById(R.id.btn_camera_snap);
		btnRetake = (Button) findViewById(R.id.btn_pic_retake);
		btnUse = (Button) findViewById(R.id.btn_pic_use);

		mCameraview = (CameraView) findViewById(R.id.camera_view);		
		mCameraview.setPictureCallback(this);
		mCameraview.initPreview();

		btnClick.setOnClickListener(this);     
		btnRetake.setOnClickListener(this);   
		btnUse.setOnClickListener(this);   

		btnRetake.setVisibility(Button.GONE);
		btnUse.setVisibility(Button.GONE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			albumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			albumStorageDirFactory = new BaseAlbumDirFactory();
		}
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View toastView = (View) layoutInflater.inflate(R.layout.toast_view, null);

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastView);
		toast.show();

		View overlayView = (View) layoutInflater.inflate(R.layout.overlay_view, null);
		addContentView(overlayView, mCameraview.getLayoutParams());

	}
	@Override
	protected void onPause() {		

		if(mCameraview != null)
		{
			mCameraview.stopPreview(true);			
		}
		super.onPause();
	}

	@Override
	protected void onResume() {	
		if(mCameraview != null)
		{
			mCameraview.resumePreview();
			//mCameraview.onResume();
		}
		btnClick.setVisibility(Button.VISIBLE);
		btnRetake.setVisibility(Button.GONE);
		btnUse.setVisibility(Button.GONE);
		super.onResume();
	}

	@Override
	protected void onStop() {
		if(mCameraview != null)
		{
			mCameraview.stopPreview(true);	
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {		
		super.onDestroy();

		if(mCameraview != null)
		{
			mCameraview.stopPreview(false);
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

	@Override
	public void pictureTakenData(byte[] data, Camera camera) {

		if(data != null)
		{
			tempData = data;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_camera_snap:

			if(mCameraview != null)
			{
				mCameraview.takePicture();

				btnClick.setVisibility(Button.GONE);
				btnRetake.setVisibility(Button.VISIBLE);
				btnUse.setVisibility(Button.VISIBLE);

			}

			break;
		case R.id.btn_pic_retake:

			if(mCameraview != null)
			{
				mCameraview.resumePreview();

				btnClick.setVisibility(Button.VISIBLE);
				btnRetake.setVisibility(Button.GONE);
				btnUse.setVisibility(Button.GONE);
			}

			break;
		case R.id.btn_pic_use:

			try 
			{
				saveToMemory();
			} catch (Exception e) {
				Toast.makeText(CameraPictureSnapActivity.this, e.getMessage(),
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} 

			if(currentPhotoPath != null)
			{	
				Intent intent = new Intent(CameraPictureSnapActivity.this,AddKeyActivity.class); 
				intent.putExtra(AddKeyCameraActivity.IMAGE_PATH, currentPhotoPath);
				startActivity(intent);
				//finish();
			}

			break;

		default:
			break;
		}
	}
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

	private String saveToCard() throws IOException
	{
		FileOutputStream outStream = null;
		String imagePath = null;
		try {

			File dir = new File(Environment.getExternalStorageDirectory() + "/idakey1");

			String state = Environment.getExternalStorageState();
			if(!state.equals(Environment.MEDIA_MOUNTED))  
			{
				throw new IOException("SD Card is not mounted.  It is " + state + ".");
			}

			if (!dir.exists()) 
			{				
				if(!dir.mkdir())
				{
					throw new IOException("Path to file could not be created.");				
				}
			}	 

			File outputFile = new File(dir, String.format("%d.jpg",System.currentTimeMillis()));
			imagePath  = outputFile.getAbsolutePath();
			// Write to SD Card
			outStream = new FileOutputStream(outputFile); // <9>
			outStream.write(tempData);
			outStream.close();
			Log.d(TAG, "onPictureTaken - wrote bytes: " + tempData.length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}	
		return imagePath;
	}



}
