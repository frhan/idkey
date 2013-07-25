package com.prologic.idkey.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.prologic.idkey.api.WebService;
import com.prologic.idkey.api.command.GetAllCategoriesCommand;
import com.prologic.idkey.api.command.MoveKeyCommand;
import com.prologic.idkey.api.command.UpdateKeyCommand;
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
	private String keyNo;
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
		btnSave.setTextColor(Color.WHITE);
		//btnDelete.setBackgroundResource(R.drawable.round_rect_grey);
		//btnSave.setBackgroundResource(R.drawable.round_rect_grey);
		btnDelete.setText("Delete");
		btnDelete.setTextColor(Color.WHITE);
		keyNo = "";
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
				keyNo = keyArray[7];		

				Log.i(TAG, keyArray[2]);
			}			
		}

		if(currentKey != null)
		{
			etKeyName.setText(currentKey.getName());
			//etKeyName.setEnabled(false);
			txtKeyTitle.setText("Key"+keyNo);
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
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		
		activityRunning.set(false);
	}

	@Override
	protected void suspendRunningTask() 
	{
		super.suspendRunningTask();
		if(categoryTask != null && categoryTask.getStatus() == AsyncTask.Status.RUNNING)
		{
			categoryTask.cancel(true);
			categoryTask = null;
		}
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

		int spinnerPosition = spinnerCategory.getSelectedItemPosition();
		int changedCategoryId = -1;
		String currentText = etKeyName.getText().toString();	
		
		if(spinnerPosition > -1 )
		{
			int currentSelectedKeyId = listCategories.get(spinnerPosition).getId();
			if(currentKey.getCategoryId() != currentSelectedKeyId)
			{
				changedCategoryId = currentSelectedKeyId;
			}
		}
		
		if((currentText.equalsIgnoreCase("") || currentText.equals(currentKey.getName())) && changedCategoryId == -1 )
		{
			showOkAlertDailog("Please Change Id or Category", "Save Key", false);

		}else 
		{
			new UpdateKeyTask(context,currentKey.getId(),currentText,changedCategoryId).execute();
		}
	}

	private void deleteKey()
	{
		new DeleteKeyTask(context, currentKey.getIqeToken(), currentKey.getId()).execute();
	}

	public void onClickKeyCancelDelete(View v)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Delete Key");

		alertDialogBuilder.setMessage("Are you sure you want to delete?")	
		.setCancelable(true)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) 
			{
				dialog.dismiss();			

				deleteKey();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {


			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();		
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

	private class UpdateKeyTask extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		private String name;
		private int newCategoryId;
		//private MoveKeyCommand moveKeyCommand;
		private int keyId;
		private UpdateKeyCommand updateKeyCommand;		
		private CustomProgressDailog progressDialog;

		public UpdateKeyTask(Context context,int keyId,String name,int newCategoryId) 
		{
			this.context = context;
			this.name = name;
			this.keyId = keyId;
			this.newCategoryId = newCategoryId;

			progressDialog = new CustomProgressDailog(context);
			progressDialog.setTitle("Loading");
			progressDialog.setMessage("Please wait...");
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			updateKeyCommand = new UpdateKeyCommand(keyId, name,newCategoryId);
			updateKeyCommand.execute(ApiConnection.getInstance(context));

			return null;
		}

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			progressDialog.show();

		}
		
		
		@Override
		protected void onPostExecute(Void result) {			
			super.onPostExecute(result);
			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			
			showOkAlertDailog(updateKeyCommand.getMessage(), "Update Key", updateKeyCommand.isUpdatedSuccessfully(), new IDailogOKClickListener() {
				
				@Override
				public void onOkClick() {
					if(updateKeyCommand.isUpdatedSuccessfully())
					{
						finish();
					}
				}
				
				@Override
				public void onCancelClick() {
					
					
				}
			});
		}
	}

	private String [] parseDeleteKey(String data)
	{
		String [] parsedData = new String[2];
		try {
			JSONObject jsonObject = new JSONObject(data);
			if(jsonObject.has("comment"))
			{
				parsedData[0] =  jsonObject.getString("comment");				
			}
			if(jsonObject.has("error"))
			{
				parsedData[1] =  String.valueOf(jsonObject.getInt("error"));				
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return parsedData;
	}

	private class DeleteKeyTask extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		private int userKeyId;
		private String sqeId;		
		private String message;
		private boolean status;
		private CustomProgressDailog progressDialog;

		public DeleteKeyTask(Context context,String sqeId,int userKeyId) 
		{
			this.context = context;
			this.sqeId = sqeId;
			this.userKeyId = userKeyId;
			message = "";
			status = false;
			progressDialog = new CustomProgressDailog(context);
			progressDialog.setTitle("Loading");
			progressDialog.setMessage("Please wait...");			
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				String iqdata = iqRemote.deleteKey(sqeId, true);
				String [] iqDeleteArray = parseDeleteKey(iqdata);
				if(iqDeleteArray != null && iqDeleteArray[0] != null)
				{
					message = iqDeleteArray[0];					
				}
				if(iqDeleteArray != null && iqDeleteArray[1] != null)
				{
					if(Integer.valueOf(iqDeleteArray[1]) == 0)
					{

						status = WebService.getInstance().deleteKey(userKeyId);
						message = status?"Key deleted succesfully":"Key Delete failed";							
					}
				}
			} catch (IOException e) 
			{
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}catch (Exception e) 
			{

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if(progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			
			afterDeleteKey();
		}

		private void afterDeleteKey()
		{
			showOkAlertDailog(message, "Delete Key", status,new IDailogOKClickListener() {

				@Override
				public void onOkClick() {
					finish();					
				}

				@Override
				public void onCancelClick() 
				{	

				}
			});
		}

	}

}
