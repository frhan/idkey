package com.prologic.idkey.activities;



import com.prologic.idkey.IdKeyPreferences;
import com.prologic.idkey.api.command.Session;

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

		String authToken = IdKeyPreferences.getAuthToken();

		Session session = Session.getInstance();
		session.setAuthToken(authToken);
		session.setEmail(IdKeyPreferences.getEmail());
		session.setPassword(IdKeyPreferences.getPassword());

	}
	@Override
	public void onTerminate() {
		super.onTerminate();
		Session.getInstance().clearSession();
	}

}
