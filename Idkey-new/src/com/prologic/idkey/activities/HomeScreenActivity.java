package com.prologic.idkey.activities;

import com.prologic.idkey.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class HomeScreenActivity extends MainActivity implements OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_screen);
	}
	
	public void onClickCategories(View v)
	{
		setCurrent(com.prologic.idkey.activities.CategoryActivity.class, null);
		
	}
	public void onClickSettingsButton(View v)
	{
		
		setCurrent(com.prologic.idkey.activities.SettingsActivity.class, null);
	}
	
	public void onClickKeyList(View v) 
	{
		setCurrent(com.prologic.idkey.activities.KeyListingActivity.class, null);
		
	}
	public void onClickAddNewKey(View v)
	{
		setCurrent(com.tasawr.camera.CameraPictureSnapActivity.class, null);
		
	}
	public void onClickTutorial(View v)
	{
		setCurrent(com.prologic.idkey.activities.TutorialActivity.class, null);
	}
	public void onClickIdentifyKey(View v)
	{
		//setCurrent(com.prologic.idkey.activities.IdentifyKeyActivity.class, null);
	}

}
