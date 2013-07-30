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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.ff.camera.AlbumStorageDirFactory;
import com.ff.camera.BaseAlbumDirFactory;
import com.ff.camera.FroyoAlbumDirFactory;
import com.iqengines.sdk.Utils;
import com.prologic.idkey.R;
import com.prologic.idkey.activities.AddImageKeyActivity;
import com.prologic.idkey.activities.AddKeyActivity;
import com.prologic.idkey.activities.AddKeyCameraActivity;
import com.prologic.idkey.activities.MainActivity;

public class CameraPictureSnapActivity extends MainActivity implements IPictureCallback,OnClickListener
{
	private Button btnClick,btnRetake,btnUse;
	protected IDKeyCameraView mCameraview;
	private byte[] tempData;
	private String TAG = "Camera";
	public static final String SELECTED_IMAGE_PATH = "IMAGE_PATH";
	private AlbumStorageDirFactory albumStorageDirFactory = null;
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private String currentPhotoPath = null;
	protected 	LayoutInflater layoutInflater;
	protected ImageView ivOverlayView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		disableScreenTurnOff();

		setContentView(R.layout.camera_view);

		btnClick = (Button) findViewById(R.id.btn_camera_snap);
		btnRetake = (Button) findViewById(R.id.btn_pic_retake);
		btnUse = (Button) findViewById(R.id.btn_pic_use);

		mCameraview = (IDKeyCameraView) findViewById(R.id.camera_view);		
		mCameraview.setPictureCallback(this);
		//mCameraview.initPreview();

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
		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View toastView = (View) layoutInflater.inflate(R.layout.toast_view, null);

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastView);
		toast.show();

		View overlayView = (View) layoutInflater.inflate(R.layout.overlay_view, null);
		ivOverlayView = (ImageView) overlayView.findViewById(R.id.iv_overlay_view);
		addContentView(overlayView, mCameraview.getLayoutParams());
		setOverlayBackground(R.drawable.add_cameraoverlay);

	}


	protected void setOverlayBackground(int resId)
	{
		ivOverlayView.setImageResource(resId);

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

	protected void  onClickSnap() {

		if(mCameraview != null)
		{
			mCameraview.takePicture();

			btnClick.setVisibility(Button.GONE);
			btnRetake.setVisibility(Button.VISIBLE);
			btnUse.setVisibility(Button.VISIBLE);

		}

	}

	protected void onClickRetake()
	{

		if(mCameraview != null)
		{
			mCameraview.resumePreview();

			btnClick.setVisibility(Button.VISIBLE);
			btnRetake.setVisibility(Button.GONE);
			btnUse.setVisibility(Button.GONE);
		}
	}

	protected void onClickUse()
	{
		try 
		{
			saveToMemory();
		} catch (Exception e) {
			Toast.makeText(CameraPictureSnapActivity.this, e.getMessage(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} 

		if(currentPhotoPath != null && currentFile != null)
		{	
			Intent intent = new Intent(CameraPictureSnapActivity.this,AddImageKeyActivity.class); 
			intent.putExtra(AddKeyCameraActivity.IMAGE_PATH, currentPhotoPath);
			intent.putExtra(AddKeyActivity.IMAGE_FILE, currentFile);

			startActivity(intent);
			//finish();
		}else {
			Toast.makeText(context, "Image not saved successfully", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_camera_snap:
			onClickSnap();
			break;
		case R.id.btn_pic_retake:
			onClickRetake();
			break;
		case R.id.btn_pic_use:
			onClickUse();
			break;

		default:
			break;
		}
	}
	protected void saveToMemory()
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

			if(currentPhotoPath != null)
			{
				setCropImage();
			}

		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			currentPhotoPath = null;
		}
	}
	protected File currentFile = null;

	public void setCropImage()
	{
		try {
			Bitmap thumb  = null ;
			Bitmap origBmp = BitmapFactory.decodeFile(currentPhotoPath);
			thumb = transformBitmapToThumb(origBmp);
			origBmp.recycle();
			currentFile = Utils.saveBmpToFile(context, thumb);
		} catch (OutOfMemoryError e) {
			Log.e(TAG,e.getMessage());
			currentFile = null;
		}
	}

	private Bitmap transformBitmapToThumb(Bitmap origBmp) {
		int thumbSize = getResources()
				.getDimensionPixelSize(R.dimen.image_size);
		return Utils.cropBitmap(origBmp, thumbSize);
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
