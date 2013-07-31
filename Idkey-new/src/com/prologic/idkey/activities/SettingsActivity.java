package com.prologic.idkey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
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
	private TextView txtEmail;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		tbRemember = (ToggleButton) findViewById(R.id.toggle_button_remember);
		tbRemember.setOnCheckedChangeListener(this);
		boolean isProtect = IdKeyPreferences.isProtect();
		tbRemember.setChecked(isProtect);
		txtEmail  = (TextView) findViewById(R.id.txt_settings_email);
		
		if(Session.getInstance().getEmail() != null)
		{
			txtEmail.setText(Session.getInstance().getEmail());
		}
		
		//protectPassword(IdKeyPreferences.isProtect());

	}
	public void onClickForgotPassword(View v) 
	{
		setCurrent(com.prologic.idkey.activities.ForgotPasswordActivity.class, null);
	}
	public void onClickChangePassWord(View v) 
	{
		setCurrent(com.prologic.idkey.activities.ChangePasswordActivity.class, null);

	}

	public void onClickLogOut(View v)
	{
		Session.getInstance().clearSession();
		IdKeyPreferences.setLogin(false);
		IdKeyPreferences.save(context);

		logOut();
	}
	
	private void protectPassword(boolean protect)
	{
		if(protect)
		{
			IdKeyPreferences.setProtect(true);
			IdKeyPreferences.setPassword("");
			IdKeyPreferences.save(context);
		}
		else
		{				
			IdKeyPreferences.setProtect(false);
			IdKeyPreferences.setPassword(Session.getInstance().getPassword());
			IdKeyPreferences.save(context);
		}
		
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView == tbRemember)
		{
			protectPassword(isChecked);
		}

	}
}