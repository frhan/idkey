package com.prologic.idkey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.prologic.idkey.IdKeyPreferences;
import com.prologic.idkey.R;
import com.prologic.idkey.api.command.Session;

/**
 * Created by farhan on 7/8/13.
 */
public class SettingsActivity extends MainActivity implements OnCheckedChangeListener 
{
	private ToggleButton tbRemember;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		tbRemember = (ToggleButton) findViewById(R.id.toggle_button_remember);
		tbRemember.setOnCheckedChangeListener(this);
		tbRemember.setChecked(IdKeyPreferences.isLogin());

	}
	public void onClickForgotPassword(View v) 
	{
		setCurrent(com.prologic.idkey.activities.ForgotPasswordActivity.class, null);
	}
	public void onClickChangePassWord(View v) 
	{
		setCurrent(com.prologic.idkey.activities.ChnagePasswordActivity.class, null);

	}

	public void onClickLogOut(View v)
	{
		Intent intent = new Intent(this, com.prologic.idkey.activities.SignInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView == tbRemember)
		{
			if(isChecked)
			{

				IdKeyPreferences.setEmail(Session.getInstance().getEmail());
				IdKeyPreferences.setPassword(Session.getInstance().getPassword());
				IdKeyPreferences.setLogin(true);

				IdKeyPreferences.save(context);
			}
			else
			{
				IdKeyPreferences.setEmail(Session.getInstance().getEmail());
				IdKeyPreferences.setPassword("");
				IdKeyPreferences.setLogin(false);

				IdKeyPreferences.save(context);
			}
		}

	}
}