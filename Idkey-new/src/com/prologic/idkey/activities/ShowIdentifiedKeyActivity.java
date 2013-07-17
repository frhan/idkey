package com.prologic.idkey.activities;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.prologic.idkey.R;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowIdentifiedKeyActivity extends MainActivity 
{
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private ImageView ivIdImage;
	private TextView txtId;
	private TextView txtCategoryName;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identified_key_view);
		
		ivIdImage = (ImageView) findViewById(R.id.iv_idq_key_image);
		txtId = (TextView) findViewById(R.id.txt_id_key_id);
		txtCategoryName = (TextView) findViewById(R.id.txt_idq_category_name);

		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.img_avatar)
		.showImageOnFail(R.drawable.img_avatar)
		.resetViewBeforeLoading()
		.cacheOnDisc()
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.defaultDisplayImageOptions(options)
		.threadPoolSize(1)
		.threadPriority(Thread.MIN_PRIORITY + 3)
		.denyCacheImageMultipleSizesInMemory()
		.memoryCacheSize(2 * 1024 * 1024)
		.enableLogging()
		.build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config); 
		imageLoader.handleSlowNetwork(true);
	}
	
	private void loadIdentifiedKey()
	{
		
	}
	
	public void onClickDone(View v) 
	{
		finish();		
	}
	
	private class LaodIdQTask extends AsyncTask<Void, Void, Void>
	{

		
		@Override
		protected Void doInBackground(Void... params) {
			
			return null;
		}
		
	}

}
