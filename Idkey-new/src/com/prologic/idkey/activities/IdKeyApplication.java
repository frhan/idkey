package com.prologic.idkey.activities;



import com.prologic.idkey.IdKeyPreferences;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class IdKeyApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		IdKeyPreferences.initPreferences(this);
	}

}
