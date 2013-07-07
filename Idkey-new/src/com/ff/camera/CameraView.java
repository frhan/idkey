package com.ff.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	private boolean DEBUG =true;
	private static final String TAG = CameraView.class.getSimpleName();
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Size mPreviewSize;
	private int angle;
	private Size mFramePreviewSize;
	public CameraView(Context context) {
		super(context);

	}
	public CameraView(Context context, AttributeSet attrs) {		
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		// this is needed for old android version
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {


	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}


}
