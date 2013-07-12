package com.prologic.idkey.activities;

import android.os.Bundle;
import android.view.View;

import com.prologic.idkey.R;

/**
 * Created by farhan on 7/8/13.
 */
public class SettingsActivity extends MainActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
	}
	public void onClickSettingsClose(View v)
	{
		finish();
	}
	public void onClickForgotPassword(View v) 
	{
		setCurrent(com.prologic.idkey.activities.ForgotPasswordActivity.class, null);
	}
	public void onClickChangePassWord(View v) 
	{
		
		
	}
}