package com.prologic.idkey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Utilities {

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	public static Bitmap decodeSampledBitmapFromResource(String filePath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;		

		return  BitmapFactory.decodeFile(filePath, options);
	}

	public static Bitmap decodeFile(String filePath,int size) {

		Bitmap bm = null;
		int orientation;

		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, o);

			// The new size we want to scale to
			//final int REQUIRED_SIZE = 240;
			final int REQUIRED_SIZE = size;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			bm = BitmapFactory.decodeFile(filePath, o2);

			Bitmap bitmap = bm;

			ExifInterface exif = new ExifInterface(filePath);

			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);

			Log.e("ExifInteface .........", "rotation ="+orientation);

			//exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

			Log.e("orientation", "" + orientation);
			Matrix m = new Matrix();

			if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) 
			{
				m.postRotate(180);
				//m.postScale((float) bm.getWidth(), (float) bm.getHeight());
				// if(m.preRotate(90)){
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
				return bitmap;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				m.postRotate(90); 
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
				return bitmap;
			}
			else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				m.postRotate(270);
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
				return bitmap;
			} 
			return bitmap;

		} catch (Exception e) 
		{
			Log.e("decode image",e.getMessage());
			return null;	
		}

	}

	public static boolean deleteFile(String filePath)
	{
		return false;
	}


	@SuppressWarnings("deprecation")
	public static int[] getScreenSize(Context context)
	{
		int screenSize [] = new int[2]; 
		int Measuredwidth = 0;
		int Measuredheight = 0;
		Point size = new Point();
		//WindowManager w = (WindowManager) getActivity().getWindowManager();
		WindowManager w = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
			w.getDefaultDisplay().getSize(size);

			Measuredwidth = size.x;
			Measuredheight = size.y; 
		}else{
			Display d = w.getDefaultDisplay(); 
			Measuredwidth = d.getWidth(); 
			Measuredheight = d.getHeight(); 
		}
		screenSize[0] = Measuredwidth;
		screenSize[1] = Measuredheight;

		return screenSize;
	}


}
