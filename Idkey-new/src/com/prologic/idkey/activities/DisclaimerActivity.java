package com.prologic.idkey.activities;

import com.prologic.idkey.IdKeyPreferences;
import com.prologic.idkey.R;

import android.os.Bundle;
import android.view.View;

public class DisclaimerActivity extends MainActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.disclaimer_view);
		
	}

	public void onClickToaAgree(View v) 
	{
		IdKeyPreferences.setFirstTime(false);
		IdKeyPreferences.save(context);
		setCurrent(com.prologic.idkey.activities.SignUpActivity.class,null);
		finish();
	}

	public void onClickCancel(View v)
	{
		finish();
	}
}
