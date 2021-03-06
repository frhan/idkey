package com.prologic.idkey.activities;

import com.prologic.idkey.R;
import com.prologic.idkey.Utilities;
import com.prologic.idkey.objects.SquareImageView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeScreenActivity extends MainActivity implements OnClickListener
{
	private Button btnTutorial;
	//private SquareImageView ivSquare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_screen);
		btnTutorial  = (Button) findViewById(R.id.btn_home_tutorial);
		//ivSquare = (SquareImageView) findViewById(R.id.iv_square);
		//ivSquare.getLayoutParams().width = 3*Utilities.getScreenSize(context)[0]/2;

		btnTutorial.getLayoutParams().width = Utilities.getScreenSize(context)[0]/2 - 10;
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
	//	setCurrent(com.prologic.idkey.activities.AddImageKeyActivity.class, null);

	}
	public void onClickTutorial(View v)
	{
		setCurrent(com.prologic.idkey.activities.TutorialActivity.class, null);
	}
	public void onClickIdentifyKey(View v)
	{
		setCurrent(com.prologic.idkey.activities.IdentifyKeyActivity.class, null);
	}

}
