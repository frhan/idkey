package com.tasawr.camera;

import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
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

public class IDKeyCameraView extends SurfaceView implements SurfaceHolder.Callback,AutoFocusCallback{

	private Camera camera;
	private int width;
	private int height;
	private Context mContext;
	private CameraPictureCallback mPictureCallback;
	private CameraPreviewCallback mPreviewCallBack;
	private IPictureCallback mIpicCallback;
	private IPreviewCallBack mIPrevCallback;
	private String TAG = "Camera";
	public IDKeyCameraView(Context context)
	{
		this(context,null);
	}
	public IDKeyCameraView(Context context, AttributeSet attrs) {		
		super(context, attrs);

		mContext = context;
		SurfaceHolder previewHolder = this.getHolder();		
		previewHolder.addCallback(this);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);		
		mPictureCallback = new CameraPictureCallback();
	}

	public void setPictureCallback(IPictureCallback pictureCallback)
	{
		mIpicCallback = pictureCallback;

		if(mPictureCallback != null)
		{
			mPictureCallback.setPictureCallBackListener(mIpicCallback);
		}
	}

	public void setPrevCallBack(IPreviewCallBack prevCallBack)
	{
		mIPrevCallback = prevCallBack;	

		if(mPreviewCallBack != null)
		{
			mPreviewCallBack.setPreviewCallbackListener(mIPrevCallback);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		stopPreview(false);
		super.finalize();
	}

	public boolean initPreview(){

		if(camera == null)
			camera=Camera.open();			
		try {

			Parameters params = camera.getParameters();

			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
			{
				params.set("orientation", "portrait");
				camera.setDisplayOrientation(90);
			}
			camera.setPreviewDisplay(this.getHolder());
		}
		catch (Throwable t) {
			Log.e("PreviewDemo-surfaceCallback",
					"Exception in setPreviewDisplay()", t);
			return false;
		}
		return true;
	}

	public void stopPreview(boolean temporaryStop){
		if(camera != null){
			camera.stopPreview();
			camera.release();
			camera=null;
		}		
		if(temporaryStop != true){
			SurfaceHolder previewHolder = this.getHolder();			
			previewHolder.removeCallback(this);
		}
	}
	private Size mPreviewSize;
	public void resumePreview(){
		// height width is not initialized yet
		if(height == 0 || width == 0)
			return;

		if(camera == null){
			if(initPreview() == false){
				return;
			}
		}

		Camera.Parameters parameters = camera.getParameters();
		int format = parameters.getPreviewFormat();		

		List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();

		if(isPreviewSizeSupported(supportedSizes) == true){
			parameters.setPreviewSize(width,height);
		}
		else
			parameters.setPreviewSize(supportedSizes.get(0).width, supportedSizes.get(0).height);			

		parameters.setPictureFormat(ImageFormat.JPEG);
		//parameters.setPictureSize(width, height);

		if (mPreviewSize == null) {
			// h and w get inverted on purpose
			mPreviewSize = getOptimalSize(parameters.getSupportedPictureSizes(), width > height ? width
					: height, width > height ? height : width);
		}

		parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);

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
		setDisplayOrientation(parameters, angle);

		List<String> focusModes = parameters.getSupportedFocusModes();
		if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}

		try {
			camera.setParameters(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}

		camera.startPreview();	
	}

	private Size getOptimalSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.2;
		double targetRatio = (double) w / h;
		Log.d(TAG, "target view size: " + w + "x" + h + ", target ratio=" + targetRatio);

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
			Log.d(TAG, "Supported preview size: " + size.width + "x" + size.height + ", ratio="
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
			Log.d(TAG,
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
		Log.d(TAG, "optimalSize.width=" + optimalSize.width + ", optimalSize.height="
				+ optimalSize.height);

		return optimalSize;
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
		} catch (Exception e) 
		{
			Log.d(TAG, "Can't call Camera.setDisplayOrientation on this device, trying another way");
			if (angle == 90 || angle == 270) params.set("orientation", "portrait");
			else if (angle == 0 || angle == 180)  params.set("orientation", "landscape");
		}
		params.setRotation(angle);
	}

	/**
	 * @return the default angle of the camera
	 */
	private int angle = 0;
	public int getAngle(){
		return angle;
	}
	private boolean isPreviewSizeSupported(List<Size> prevSizes){
		for (Size size : prevSizes) {
			if(size.width == width && size.height == height)
				return true;
		}
		return false;
	}

	public void takePicture()
	{
		if(camera != null && mPictureCallback != null)
		{
			/*try {
				//System.gc();
				camera.takePicture(shutterCallback, 
						rawCallback,
						mPictureCallback);					
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}*/
			camera.autoFocus(this);

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

	/************************* SurfaceHolder.Callback methods ****************************/	
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
		initPreview();
	}	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopPreview(true);		
	}
	@Override
	public void onAutoFocus(boolean success, Camera camera) 
	{
		try {
			//System.gc();
			camera.takePicture(shutterCallback, 
					rawCallback,
					mPictureCallback);					
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}


	}
}

