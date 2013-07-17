package com.ff.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.spec.IvParameterSpec;

import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.prologic.idkey.R;
import com.prologic.idkey.activities.MainActivity;

public abstract class CameraActivity extends MainActivity implements IPictureCallback
{
	protected CameraView cameraView;
	private byte []  tempData;
	private AlbumStorageDirFactory albumStorageDirFactory = null;
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private String currentPhotoPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	

		disableScreenTurnOff();

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
		}
	}

	public void onClickRetake(View v)
	{
		if(cameraView != null)
		{
			cameraView.resumePreview();
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
			if(cameraView != null)
			{
				cameraView.resumePreview();
			}
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

	@Override
	public void pictureTakenData(byte[] data, Camera camera) 
	{
		if(data != null)
		{
			tempData = data;
		}
	}
	public String getCurrentPhotoPath() {
		return currentPhotoPath;
	}
}
