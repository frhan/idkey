package com.prologic.idkey.activities;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_key_layout);
		ivAddImage = (ImageView) findViewById(R.id.iv_add_key_image);
		spinnerCategory = (Spinner) findViewById(R.id.spinner_key_category);
		etKeyId = (EditText) findViewById(R.id.et_key_id);

		photoPath = getIntent().getExtras().getString(AddKeyCameraActivity.IMAGE_PATH);
		listCategories = new ArrayList<Category>();
		spinnerAdapter = new CategorySpinnerAdapter(this,R.layout.spinner_view,listCategories);
		spinnerCategory.setAdapter(spinnerAdapter);

		if(photoPath != null)
		{
			//currentBitmap = Utilities.decodeFile(photoPath, 256);

			//ivAddImage.setImageBitmap(currentBitmap);

			loadCategories();
			setImageFile();
			initSdk();
		}

	}
	private void loadCategories()
	{
		new CategoryListTask(this).execute();
	}
	
	private IQRemote iqremote;
	private void initSdk()
	{
		iqremote = new  IQRemote(getResources().getString(R.string.iqe_app_key), 
				getResources().getString(R.string.iqe_app_secret));

	}
	public void onClickUse(View v)
	{
		try {
			//need to optimize
			ArrayList<File> files = new ArrayList<File>();
			files.add(getImageFile());
			String name = etKeyId.getText().toString();
			int categoryId = -1;
			if(spinnerCategory.getSelectedItemPosition()> -1)
			{
				categoryId = listCategories.get(spinnerCategory.getSelectedItemPosition()).getId();
			}
			if(name.length() == 0 )
			{
				showOkAlertDailog("Please put a name", "Add ID", false);
			}else if(isOnline()){
				new AddKeyTask(context, files,etKeyId.getText().toString(),categoryId).execute();
			}else {
				showOkAlertDailog("Please check your internet connection", "Internet error", false);
			}

		} catch (Exception e) {
			Log.e(TAG,e.getMessage());
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
	}

	private Bitmap transformBitmapToThumb(Bitmap origBmp) {
		int thumbSize = getResources()
				.getDimensionPixelSize(R.dimen.image_size);
		return Utils.cropBitmap(origBmp, thumbSize);
	}

	private File currentFile = null;
	private void  setImageFile()
	{
		Bitmap thumb  = null ;
		Bitmap origBmp = BitmapFactory.decodeFile(photoPath);
		currentBitmap = thumb = transformBitmapToThumb(origBmp);
		origBmp.recycle();
		ivAddImage.setImageBitmap(currentBitmap);
		currentFile = Utils.saveBmpToFile(context, thumb);		
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
			showOkAlertDailog(message, "Add key", isAddedSuccessfully);
			addKeyCommand = null;
		}

	}
}
