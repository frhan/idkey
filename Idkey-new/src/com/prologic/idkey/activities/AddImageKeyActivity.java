package com.prologic.idkey.activities;

import java.io.File;
import java.util.ArrayList;

import com.prologic.idkey.Utilities;
import com.prologic.idkey.objects.Category;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AddImageKeyActivity extends KeyActivity
{
	private Bitmap currentBitmap;
	private File currentFile = null;
	private String photoPath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setImage();
	}

	protected void setImage()
	{
		if(getIntent().getExtras() != null)
		{
			photoPath = getIntent().getExtras().getString(AddKeyCameraActivity.IMAGE_PATH);
			currentFile = (File) getIntent().getExtras().get(AddKeyActivity.IMAGE_FILE);
		}

		if(photoPath != null)
		{
			setImageViewImage();

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

	public void onClickUse(View v)
	{

		if(currentFile != null)
		{
			try {

				String name = etKeyName.getText().toString();
				int categoryId = -1;

				Category category = (Category) btnKeyCategory.getTag();
				if(category != null)
				{
					categoryId = category.getId();
				}

				if(name.length() == 0 )
				{
					showOkAlertDailog("Please put a name", "Add ID", false);
				}else if(isOnline())
				{
					ArrayList<File> files = new ArrayList<File>();
					files.add(currentFile);
					new AddKeyTask(context, files,etKeyName.getText().toString(),categoryId).execute();
				}else {
					showOkAlertDailog("Please check your internet connection", "Internet error", false);
				}

			} catch (Exception e) {
				Log.e(TAG,e.getMessage());
			}
		}

	}
}
