package com.prologic.idkey.activities;

import com.prologic.idkey.IdKeyPreferences;
import com.prologic.idkey.R;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class SplashScreenActivity extends MainActivity
{

	protected boolean _active = true;
	protected int _splashTime = 3000; // time to display the splash screen in ms
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);


		// thread for displaying the SplashScreen
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while(_active && (waited < _splashTime)) {
						sleep(100);
						if(_active) {
							waited += 100;
						}
					}
				} 
				catch(InterruptedException e) 
				{
					// do nothing
				} 
				finally
				{

					if(IdKeyPreferences.isFirstTime())
					{
						setCurrent(com.prologic.idkey.activities.DisclaimerActivity.class, null);
						finish();		                
					}
					else if(!IdKeyPreferences.isSignedUP() && !IdKeyPreferences.isLogin())
					{
						setCurrent(com.prologic.idkey.activities.SignUpActivity.class, null);
						finish();
					}
					else if(IdKeyPreferences.isSignedUP() || !IdKeyPreferences.isLogin())
					{
						setCurrent(com.prologic.idkey.activities.SignInActivity.class, null);
						finish();
					}
					else
					{
						setCurrent(com.prologic.idkey.activities.HomeScreenActivity.class, null);
						finish();
					}
				}
			}
		};
		splashTread.start();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			_active = false;
		}
		return true;
	}
}
