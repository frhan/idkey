package com.prologic.idkey.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.iqengines.sdk.IQRemote;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.prologic.idkey.CategorySpinnerAdapter;
import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.GetAllCategoriesCommand;
import com.prologic.idkey.objects.Category;
import com.prologic.idkey.objects.Key;

public class KeyShowActivity extends MainActivity 
{
	private Button btnSave;
	private Button btnDelete;
	private Key currentKey = null;
	public static final String CURRENT_KEY = "current_key";
	private EditText etKeyName;
	private CategoryListTask categoryTask = null;
	private List<Category> listCategories;
	private Spinner spinnerCategory;
	private CategorySpinnerAdapter spinnerAdapter;
	private TextView txtKeyTitle;
	private IQRemote iqRemote;
	private ImageView ivIqImage;

	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_key_layout);

		btnSave = (Button) findViewById(R.id.btn_kay_use);
		btnDelete = (Button) findViewById(R.id.btn_key_delete_cancel);
		etKeyName = (EditText) findViewById(R.id.et_key_id);
		spinnerCategory = (Spinner) findViewById(R.id.spinner_key_category);
		txtKeyTitle = (TextView) findViewById(R.id.txt_key_title);
		ivIqImage = (ImageView) findViewById(R.id.iv_add_key_image);

		listCategories = new ArrayList<Category>();
		spinnerAdapter = new CategorySpinnerAdapter(this,R.layout.spinner_view,listCategories);
		spinnerCategory.setAdapter(spinnerAdapter);

		btnSave.setText("Save");
		btnDelete.setBackgroundResource(R.drawable.delete_btn_800_400);

		iqRemote  = new IQRemote(getResources().getString(R.string.iqe_app_key),getResources().getString( R.string.iqe_app_secret));

		Bundle b = getIntent().getExtras();
		if(b != null)
		{
			String [] keyArray = b.getStringArray(CURRENT_KEY);

			if(keyArray != null && keyArray.length  > 0)
			{
				currentKey = new Key(Integer.valueOf(keyArray[0]));
				currentKey.setName(keyArray[1]);
				currentKey.setCategoryId(Integer.valueOf(keyArray[4]));
				currentKey.setIqeToken(keyArray[2]);
				currentKey.setScanUrl(keyArray[3]);
				currentKey.setCategoryName(keyArray[5]);
				currentKey.setCreateDate(keyArray[6]);				
			}			
		}

		if(currentKey != null)
		{
			etKeyName.setText(currentKey.getName());
			txtKeyTitle.setText("Key"+currentKey.getId());
			loadCategories();
		}

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

	private void loadCategories()
	{
		categoryTask = new CategoryListTask(this);
		categoryTask.execute();
	}


	private void updateCategoryList(List<Category> listCategories)
	{
		this.listCategories.clear();
		if(listCategories != null && listCategories.size() >0)
		{
			this.listCategories.addAll(listCategories);
			spinnerAdapter.notifyDataSetChanged();
			for(int i = 0; i<this.listCategories.size(); i++)
			{
				if(this.listCategories.get(i).getId() == currentKey.getCategoryId())
				{
					spinnerCategory.setSelection(i);
					break;					
				}
			}
		}
	}
	private void updateKeyImage(String imageUrl)
	{


		imageLoader.displayImage(imageUrl, ivIqImage, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				//progressBarView.setVisibility(View.VISIBLE);
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

	public void onClickUse(View v)
	{

	}

	public void onClickKeyCancelDelete(View v)
	{

	}

	private String[] parseIqeRetrieveData(String data)
	{
		String [] resultArray = new String[5];
		try {
			JSONObject jsonObject = new JSONObject(data);
			if(jsonObject.has("comment"))
			{
				resultArray[1] = jsonObject.getString("comment");
			}
			if(jsonObject.has("object"))
			{
				resultArray[0] = "Success";

				JSONObject imageObject = jsonObject.getJSONObject("object");

				if(imageObject.has("related_images"))
				{
					JSONObject relatedImageObject = imageObject.getJSONObject("related_images");
					if(relatedImageObject.has("images") && relatedImageObject.get("images") instanceof JSONArray)
					{
						JSONArray imageArray = relatedImageObject.getJSONArray("images");
						if(imageArray.length() >0 && imageArray.getJSONObject(0) instanceof JSONObject)
						{
							JSONObject mainImageObject = imageArray.getJSONObject(0);
							if(mainImageObject.has("data_url"))
							{
								resultArray[2] = mainImageObject.getString("data_url");
							}
							if(mainImageObject.has("img_id"))
							{
								resultArray[3] = mainImageObject.getString("img_id");
							}
							if(mainImageObject.has("resource_url"))
							{
								resultArray[4] = mainImageObject.getString("resource_url");
							}
						}
					}

				}
			}else {
				resultArray[0] = "fail";
			}


		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return resultArray;
	}
	private class CategoryListTask extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		private GetAllCategoriesCommand allCategoriesCommand;
		private CustomProgressDailog progressDialog;
		private String [] retrieveData;
		public CategoryListTask(Context context) 
		{
			this.context = context;
			progressDialog = new CustomProgressDailog(context);
			progressDialog.setTitle("Loading");
			progressDialog.setMessage("Please wait...");
			retrieveData = null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();

		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			allCategoriesCommand = new GetAllCategoriesCommand();
			allCategoriesCommand.execute(ApiConnection.getInstance(context));
			try {
				String data = iqRemote.retrieveObject(currentKey.getIqeToken(), null, true);
				retrieveData = parseIqeRetrieveData(data);
				//Log.i(TAG, data);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}

			if(allCategoriesCommand != null)
			{
				List<Category> categories = allCategoriesCommand.getCategories();
				updateCategoryList(categories);
			}
			if(retrieveData != null && retrieveData[0].equalsIgnoreCase("fail") && retrieveData[1] != null)
			{
				showOkAlertDailog(retrieveData[1], "IQEngine",false);				
			}else if(retrieveData != null && retrieveData[2] != null){
				//showOkAlertDailog(retrieveData[2], "IQEngine",true);	
				updateKeyImage(retrieveData[2]);
			}
		}
	}




}
