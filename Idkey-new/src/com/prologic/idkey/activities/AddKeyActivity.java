package com.prologic.idkey.activities;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.iqengines.sdk.IQRemote;
import com.iqengines.sdk.Utils;
import com.prologic.idkey.CategorySpinnerAdapter;
import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;
import com.prologic.idkey.Utilities;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.AddKeyCommand;
import com.prologic.idkey.api.command.GetAllCategoriesCommand;
import com.prologic.idkey.objects.Category;

public class AddKeyActivity extends MainActivity 
{
	private ImageView ivAddImage;
	private String photoPath;
	private Bitmap currentBitmap;
	private List<Category> listCategories;
	private Spinner spinnerCategory;
	private CategorySpinnerAdapter spinnerAdapter;
	private EditText etKeyId;
	private File currentFile = null;
	public static final String IMAGE_FILE = "image_file";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_key_layout);
		ivAddImage = (ImageView) findViewById(R.id.iv_add_key_image);
		spinnerCategory = (Spinner) findViewById(R.id.spinner_key_category);
		etKeyId = (EditText) findViewById(R.id.et_key_id);


		photoPath = getIntent().getExtras().getString(AddKeyCameraActivity.IMAGE_PATH);
		currentFile = (File) getIntent().getExtras().get(AddKeyActivity.IMAGE_FILE);

		listCategories = new ArrayList<Category>();
		spinnerAdapter = new CategorySpinnerAdapter(this,R.layout.spinner_view,listCategories);
		spinnerCategory.setAdapter(spinnerAdapter);

		if(photoPath != null)
		{
			setImageViewImage();
			loadCategories();
			//setImageFile();
			initSdk();
		}

	}

	private void setImageViewImage()
	{
		try {
			currentBitmap = Utilities.decodeFile(photoPath, 512);
			ivAddImage.setImageBitmap(currentBitmap);
		} catch (OutOfMemoryError e) {
			Log.e(TAG, e.getMessage());
		}

	}


	private void loadCategories()
	{
		if(isOnline())
		{
			new CategoryListTask(this).execute();
		}
	}

	private IQRemote iqremote;
	private void initSdk()
	{
		iqremote = new  IQRemote(getResources().getString(R.string.iqe_app_key), 
				getResources().getString(R.string.iqe_app_secret));

	}
	public void onClickUse(View v)
	{

		if(currentFile != null)
		{
			try {

				String name = etKeyId.getText().toString();
				int categoryId = -1;
				if(spinnerCategory.getSelectedItemPosition()> -1)
				{
					categoryId = listCategories.get(spinnerCategory.getSelectedItemPosition()).getId();
				}
				if(name.length() == 0 )
				{
					showOkAlertDailog("Please put a name", "Add ID", false);
				}else if(isOnline())
				{
					ArrayList<File> files = new ArrayList<File>();
					files.add(currentFile);
					new AddKeyTask(context, files,etKeyId.getText().toString(),categoryId).execute();
				}else {
					showOkAlertDailog("Please check your internet connection", "Internet error", false);
				}

			} catch (Exception e) {
				Log.e(TAG,e.getMessage());
			}
		}

	}
	public void onClickKeyCancelDelete(View v)
	{
		finish();
	}

	private void updateCategoryList(List<Category> listCategories)
	{
		this.listCategories.clear();
		if(listCategories != null)
		{
			this.listCategories.addAll(listCategories);
			spinnerAdapter.notifyDataSetChanged();
			for(int i = 0; i<this.listCategories.size(); i++)
			{
				if(this.listCategories.get(i).getName().equalsIgnoreCase("unassigned"))
				{
					spinnerCategory.setSelection(i);
					break;					
				}
			}
		}

	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		System.gc();
		if(currentBitmap != null)
		{
			currentBitmap.recycle();
		}
	}

	private Bitmap transformBitmapToThumb(Bitmap origBmp) {
		int thumbSize = getResources()
				.getDimensionPixelSize(R.dimen.image_size);
		return Utils.cropBitmap(origBmp, thumbSize);
	}



	private void  setImageFile()
	{
		try {
			Bitmap thumb  = null ;
			Bitmap origBmp = BitmapFactory.decodeFile(photoPath);
			thumb = transformBitmapToThumb(origBmp);
			origBmp.recycle();
			currentFile = Utils.saveBmpToFile(context, thumb);
		} catch (OutOfMemoryError e) {
			Log.e(TAG,e.getMessage());
			currentFile = null;
		}

	}


	private File getImageFile() 
	{
		return currentFile;
	}

	private class CategoryListTask extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		private GetAllCategoriesCommand allCategoriesCommand;
		private CustomProgressDailog progressDialog;

		public CategoryListTask(Context context) 
		{
			this.context = context;
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
		protected Void doInBackground(Void... params) 
		{
			allCategoriesCommand = new GetAllCategoriesCommand();
			allCategoriesCommand.execute(ApiConnection.getInstance(context));

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
		}
	}

	private String[] parseIqeAddResult(String result)
	{
		Log.i(TAG, result);
		String [] resultArray = new String[4];

		try {
			JSONObject jsonObject = new JSONObject(result);
			if(jsonObject.has("comment"))
			{
				resultArray[1] = jsonObject.getString("comment");			

			}
			if(jsonObject.has("obj_id"))
			{
				resultArray[0] = "SUCCESS";
				resultArray[2] = jsonObject.getString("obj_id");	
				if(jsonObject.has("resource_url"))
				{
					resultArray[3] = jsonObject.getString("resource_url");	
				}		
			}else {
				resultArray[0] = "ERROR";
			}


		} catch (JSONException e) 
		{
			Log.e(TAG, e.getMessage());
		}
		return resultArray;
	}

	private void onSuccessAddingKey(String message,boolean status)
	{
		showOkCancelAlertDailog(message, "Key Add", "Go to Home", "Add key Now", status, new IDailogOKClickListener() {

			@Override
			public void onOkClick() {

				Intent intent = new Intent(context, com.prologic.idkey.activities.HomeScreenActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

			}

			@Override
			public void onCancelClick() 
			{			
				finish();				
			}
		});
	}

	private class AddKeyTask extends AsyncTask<Void, Void, String>
	{

		private Context context;
		private ArrayList<File> imageFiles;
		private String name;
		private AddKeyCommand addKeyCommand;
		private int categoryId;
		private boolean isAddedSuccessfully;
		private CustomProgressDailog progressDialog;

		public AddKeyTask(Context context,ArrayList<File> imageFiles,String name,int categoryId) 
		{
			this.context = context;
			this.imageFiles = imageFiles;	
			this.name = name;
			this.categoryId = categoryId;
			this.isAddedSuccessfully = false;
			progressDialog = new CustomProgressDailog(context);
			progressDialog.setTitle("Loading");
			progressDialog.setMessage("Please wait...");
		}
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			progressDialog.show();
		}
		@Override
		protected String doInBackground(Void... params) 
		{
			String message = "Key Addding Error";
			try {
				String result = iqremote.upload(imageFiles, name,null,null,true,null);
				String [] keyResultArray = parseIqeAddResult(result);


				if(keyResultArray != null && keyResultArray[0].equalsIgnoreCase("SUCCESS"))
				{
					addKeyCommand = new AddKeyCommand(keyResultArray[2], keyResultArray[3], name, categoryId);
					addKeyCommand.execute(ApiConnection.getInstance(context));
					isAddedSuccessfully = addKeyCommand.isAddedSuccessfully(); 
					message = addKeyCommand.getMessage();
				}else {
					message =  keyResultArray[1];
				}				
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			return message;
		}
		@Override
		protected void onPostExecute(String message) 
		{
			super.onPostExecute(message);
			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			onSuccessAddingKey(message, isAddedSuccessfully);
			addKeyCommand = null;
		}

	}
}
