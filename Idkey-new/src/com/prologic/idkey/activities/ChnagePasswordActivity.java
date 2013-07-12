package com.prologic.idkey.activities;

import com.prologic.idkey.R;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class ChnagePasswordActivity extends MainActivity
{
	private EditText etOldPAssword;
	private EditText etNewPassword;
	private EditText etRepeatNewPAssword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password);
	}
	
	public void onClickChangePassword(View v)
	{
		
	}

}
