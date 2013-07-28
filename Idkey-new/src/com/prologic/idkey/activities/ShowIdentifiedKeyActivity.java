package com.prologic.idkey.activities;

import java.io.IOException;
import java.util.List;

import com.iqengines.sdk.IQRemote;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.GetKeysCommand;
import com.prologic.idkey.objects.Key;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
	private TextView txtKeyName;
	public static final String IQE_TOKEN = "iqe_token";
	public static final String OBJ_NAME = "obj_name";

	private String iqeToken = null;
	private String  objName = null;
	private IQRemote iqRemote;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identified_key_view);

		ivIdImage = (ImageView) findViewById(R.id.iv_idq_key_image);
		txtId = (TextView) findViewById(R.id.txt_id_key_id);
		txtCategoryName = (TextView) findViewById(R.id.txt_idq_category_name);
		txtKeyName = (TextView) findViewById(R.id.txt_id_key_title);

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
		
		iqRemote = new IQRemote(getResources().getString(R.string.iqe_app_key), getResources().getString(R.string.iqe_app_secret));

		Bundle b = getIntent().getExtras();
		if(b != null)
		{
			iqeToken = b.getString(IQE_TOKEN);
			objName = b.getString(OBJ_NAME);
		}

		if(objName != null && iqeToken != null)
		{
			//txtKeyName.setText(objName);
			//txtId.setText(iqeToken);
			new LaodIdQTask(this).execute();

		}

	}

	private void updateKeyImage(String imageUrl)
	{
		imageLoader.displayImage(imageUrl, ivIdImage, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "Input/Output error";
					break;
				case DECODING_ERROR:
					message = "Image can't be decoded";
					break;
				case NETWORK_DENIED:
					message = "Downloads are denied";
					break;
				case OUT_OF_MEMORY:
					message = "Out Of Memory error";
					break;
				case UNKNOWN:
					message = "Unknown error";
					break;
				}
				Log.e("Image Loader", message);
				//progressBarView.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				//progressBarView.setVisibility(View.GONE);
			}
		});
	}

	private void updateKeyIdAndCategory(List<Key> keys)
	{
		for(int i=0;i<keys.size(); i++)
		{
			Key key = keys.get(i);
			if(key.getIqeToken().equals(iqeToken))
			{
				txtId.setText(key.getName());
				txtCategoryName.setText(key.getCategoryName());
				break;
			}
		}
		
	}

	public void onClickDone(View v) 
	{
		goHome();
	}

	private class LaodIdQTask extends AsyncTask<Void, Void, Void>
	{

		private String [] retrieveData;
		private Context context;
		private GetKeysCommand keysCommand;
		
		public LaodIdQTask(Context context) 
		{
			this.context = context;
			retrieveData = null;
	
			showProgressDaoilog(null,"Loading...", true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			String data;
			try {
				data = iqRemote.retrieveObject(iqeToken, null, true);
				retrieveData = KeyShowActivity.parseIqeRetrieveData(data);
				
				keysCommand = new GetKeysCommand();
				keysCommand.execute(ApiConnection.getInstance(context));
			} catch (IOException e) 
			{
				e.printStackTrace();
			}			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		
			super.onPostExecute(result);
			hideProgressDaoilog();
			
			if(retrieveData != null && retrieveData[2] != null)
			{			
				updateKeyImage(retrieveData[2]);
			}
			if(keysCommand.getListKeys() != null)
				updateKeyIdAndCategory(keysCommand.getListKeys());
		}

	}

}
