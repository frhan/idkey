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
		setCurrent(com.prologic.idkey.activities.KeyListingActivities.class, null);
		
	}
	
	

/*	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.btn_home_tutorial:

			break;
		case R.id.btn_home_my_acc:

			break;

		case R.id.btn_home_settings:

			break;
		case R.id.btn_home_add_new_key:

			break;
		case R.id.btn_home_id_a_key:

			break;			
		case R.id.btn_home_key_listing:

			break;	
		case R.id.btn_home_categories:
			setCurrent(com.prologic.idkey.activities.CategoryActivity.class, null);
			break;

		default:
			break;
		}
	}*/

}
