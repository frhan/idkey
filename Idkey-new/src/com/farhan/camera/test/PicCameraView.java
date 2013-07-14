package com.farhan.camera.test;

import com.ff.camera.CameraPictureCallback;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PicCameraView extends SurfaceView implements SurfaceHolder.Callback  {

	private SurfaceHolder holder;
	private int width;
	private int height;
	
	public PicCameraView(Context context) {
		this(context,null);
	}
	public PicCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder = getHolder();
		holder.addCallback(this);
		// this is needed for old android version
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		
	}

}
