package com.prologic.idkey.activities;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
			currentBitmap = Utilities.decodeFile(photoPath, 256);

			ivAddImage.setImageBitmap(currentBitmap);
		}

		new CategoryListTask(this).execute();
		setImageFile();
		initSdk();

	}
	private IQRemote iqremote;
	private void initSdk()
	{
		iqremote = new  IQRemote("4cb870a67b43493aa668e94ca9095b18", "c001b534b5f34f788653cf26c2ea9172");

	}
	public void onClickUse(View v)
	{
		ArrayList<File> files = new ArrayList<File>();
		files.add(getImageFile());
		new AddKeyTask(context, files,etKeyId.getText().toString()).execute();
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
		thumb = transformBitmapToThumb(origBmp);
		origBmp.recycle();
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
	
	private void parseIqeAddResult(String result)
	{
		
	}

	private class AddKeyTask extends AsyncTask<Void, Void, Void>
	{

		private Context context;
		private ArrayList<File> imageFiles;
		private String name;
		public AddKeyTask(Context context,ArrayList<File> imageFiles,String name) 
		{
			this.context = context;
			this.imageFiles = imageFiles;	
			this.name = name;
		}
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) 
		{

			try {
				String result = iqremote.upload(imageFiles, name,null,null,true,null);
				Log.i(TAG, result);
			} catch (IOException e) {

				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
		}

	}
}
