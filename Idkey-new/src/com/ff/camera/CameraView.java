package com.ff.camera;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	private boolean DEBUG = true;
	private static final String TAG = CameraView.class.getSimpleName();
	private SurfaceHolder mHolder;
	public Camera camera;
	private Size mPreviewSize;
	private int angle;
	private Size mFramePreviewSize;
	private Boolean mAutoFocus;
	private Thread mAutofocusThread;
	private int width;
	private int height;

	public CameraView(Context context) 
	{
		this(context,null);

	}

	public CameraView(Context context, AttributeSet attrs) 
	{		
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		// this is needed for old android version
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		this.pictureCallback = new CameraPictureCallback();
	}
	//	void startAutofocus() {
	//		mAutoFocus = true;
	//		mAutofocusThread = new Thread(new AutoFocusRunnable(),"Autofocus Thread");
	//		mAutofocusThread.start();
	//	}
	public void takePicture()
	{
		if(camera != null && this.pictureCallback != null)
		{
			try {
				System.gc();
				camera.takePicture(shutterCallback, 
						rawCallback,
						pictureCallback);					
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}

		}
	}

	private IPictureCallback iPictureCallback;
	private CameraPictureCallback pictureCallback;

	public void setPictureCallback(IPictureCallback pictureCallback)
	{
		this.iPictureCallback = pictureCallback;

		if(this.pictureCallback != null)
		{
			this.pictureCallback.setPictureCallBackListener(iPictureCallback);
		}
	}


	ShutterCallback shutterCallback = new ShutterCallback() { // <6>
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() { // <7>
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() { // <8>
		public void onPictureTaken(byte[] data, Camera camera) 
		{
			Log.d(TAG, "onPictureTaken - raw");			
		}
	};

	void stopPreview() {
		mAutoFocus = false;
		if (camera != null) camera.cancelAutoFocus();
		mAutofocusThread=null;
		if (camera != null) camera.stopPreview();
	}

	void startPreview(){
		if (camera!=null){
			camera.startPreview();
			//startAutofocus();
		}
	}
	private Size getOptimalSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.2;
		double targetRatio = (double) w / h;
		if (DEBUG) Log.d(TAG, "target view size: " + w + "x" + h + ", target ratio=" + targetRatio);

		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;
		int targetWidth = w;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			boolean fitToView = size.width <= w && size.height <= h;
			if (DEBUG) Log.d(TAG, "Supported preview size: " + size.width + "x" + size.height + ", ratio="
					+ ratio + ", fitToView=" + fitToView);
			if (!fitToView) {
				// we can not use preview size bigger than surface dimensions
				// skipping
				continue;
			}
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
				continue;
			}

			double hypot = Math.hypot(size.height - targetHeight, size.width - targetWidth);
			if (hypot < minDiff) {
				optimalSize = size;
				minDiff = hypot;
			}
		}

		if (optimalSize == null) {
			if (DEBUG) Log.d(TAG,
					"Cannot find preview that matchs the aspect ratio, ignore the aspect ratio requirement");

			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (size.width > w || size.height > h) {
					// we can not use preview size bigger than surface
					// dimensions
					continue;
				}

				double hypot = Math.hypot(size.height - targetHeight, size.width - targetWidth);
				if (hypot < minDiff) {
					optimalSize = size;
					minDiff = hypot;
				}
			}
		}

		if (optimalSize == null) {
			throw new RuntimeException("Unable to determine optimal preview size");
		}
		if (DEBUG) Log.d(TAG, "optimalSize.width=" + optimalSize.width + ", optimalSize.height="
				+ optimalSize.height);

		return optimalSize;
	}

	public void resumePreview() 
	{
		if(width == 0 || height == 0)
		{
			return;
		}
		if(camera == null){
			if(initPreview() == false)
			{
				if (DEBUG) Log.e(TAG, "mCamera == null !");        
				return;
			}
		}

		if (camera == null) 
		{
			if (DEBUG) Log.e(TAG, "mCamera == null !");        
			return;
		}
		Camera.Parameters params = camera.getParameters();
		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();

		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			angle = 90;
			break;
		case Surface.ROTATION_90:
			angle = 0;
			break;
		case Surface.ROTATION_180:
			angle = 270;
			break;
		case Surface.ROTATION_270:
			angle = 180;
			break;
		default:
			throw new AssertionError("Wrong surface rotation value");
		}
		setDisplayOrientation(params, angle);

		if (mPreviewSize == null) {
			// h and w get inverted on purpose
			mPreviewSize = getOptimalSize(params.getSupportedPreviewSizes(), width > height ? width
					: height, width > height ? height : width);
		}

		params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		List<String> focusModes = params.getSupportedFocusModes();
		if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}

		camera.setParameters(params);

		try {
			camera.setPreviewDisplay(this.getHolder());
		} catch (IOException e) {
			Log.e(TAG, "Can't set preview display", e);
		}

		startPreview();

		mFramePreviewSize = camera.getParameters().getPreviewSize();
	}

	private boolean initPreview()
	{
		if(camera == null)
			camera = Camera.open();	

		return true;

	}

	public void stopPreview(boolean temporaryStop){
		if (camera != null) {
			synchronized (this) {
				camera.setPreviewCallback(null);           	
				camera.stopPreview();
				camera.release();
				camera = null;
			}
		}

		if(temporaryStop != true)
		{
			SurfaceHolder previewHolder = this.getHolder();		
			previewHolder.removeCallback(this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.width = width;
		this.height = height;

		resumePreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		//		if(mCamera==null){
		//			try {
		//				mCamera = Camera.open();
		//			} catch (RuntimeException e) {
		//				Toast.makeText(getContext(),
		//						"Unable to connect to camera. " + "Perhaps it's being used by another app.",
		//						Toast.LENGTH_LONG).show();
		//			}
		//		}

		initPreview();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{


		stopPreview(true);

	}




	private void setDisplayOrientation(Camera.Parameters params, int angle) {
		try {
			Method method = camera.getClass().getMethod("setDisplayOrientation", new Class[] {
					int.class
			});
			if (method != null)
				method.invoke(camera, new Object[] {
						angle
				});
		} catch (Exception e) {
			if (DEBUG) Log.d(TAG, "Can't call Camera.setDisplayOrientation on this device, trying another way");
			if (angle == 90 || angle == 270) params.set("orientation", "portrait");
			else if (angle == 0 || angle == 180)  params.set("orientation", "landscape");
		}
		params.setRotation(angle);
	}

	/**
	 * @return the default angle of the camera
	 */

	public int getAngle(){
		return angle;
	}


}
