package com.prologic.idkey.activities;



import java.util.concurrent.atomic.AtomicBoolean;

import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public abstract class MainActivity extends Activity implements OnClickListener {

	protected Context context;
	protected Resources resources;
	protected static final String TAG = MainActivity.class.getName();
	private CustomProgressDailog progressDailog;
	protected AtomicBoolean activityRunning = new AtomicBoolean(false);
	protected LayoutInflater inflater;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		context = MainActivity.this;
		resources = this.getResources();
		progressDailog = new CustomProgressDailog(context);
		activityRunning.set(true);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	protected void onResume() 
	{	
		super.onResume();
		activityRunning.set(true);
	}
	
	protected void requestCustomTitle()
	{
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}

	protected void  setCurrent(Class<?> klass, Bundle bundle)
	{
		Intent intent = new Intent(context, klass);
		if(bundle != null)
			intent.putExtras(bundle);
		startActivity(intent);

	}
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		//		case R.id.btn_back:
		//			pressedBackButton();
		//			break;

		default:
			break;
		}

	}
	protected void pressedBackButton()
	{
		onBackPressed();

	}
	@Override
	protected void onStop() {
		super.onStop();		
		suspendRunningTask();
		activityRunning.set(false);
	}
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		activityRunning.set(false);
	}
	protected void suspendRunningTask(){}
	public  boolean isOnline() {

		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = null;

		netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(netInfo != null && netInfo.isConnectedOrConnecting()){
			return true;
		}

		else{
			netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				return true;
			}
		}
		return false;
	}
	public void showOkAlertDailog(String message,String title,boolean status)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message)	
		.setCancelable(true)
		.setIcon((status) ? R.drawable.success : R.drawable.fail)
		.setPositiveButton(getResources().getString(android.R.string.ok),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) 
			{
				dialog.dismiss();				
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	public void showOkCancelAlertDailog(String message,String title,final String posButtonName,final String negButtonName,boolean status,final IDailogOKClickListener listener)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message)	
		.setCancelable(true)
		.setIcon((status) ? R.drawable.success : R.drawable.fail)
		.setPositiveButton(posButtonName,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) 
			{

				dialog.dismiss();			
				if(listener != null)
					listener.onOkClick();
			}
		})
		.setNegativeButton(negButtonName, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();			
				if(listener != null)
					listener.onCancelClick();
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	public void showOkAlertDailog(String message,String title,boolean status,final IDailogOKClickListener clickListener)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message)	
		.setCancelable(true)
		.setIcon((status) ? R.drawable.success : R.drawable.fail)
		.setPositiveButton(getResources().getString(android.R.string.ok),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) 
			{
				dialog.dismiss();			
				clickListener.onOkClick();
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	protected void showCustomAlertDailog(final String title,final String posButtonName,final String negButtonName,final View v,
			final ICustomDailogClickListener clickListener)
	{		

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(title);


		alertDialogBuilder.setCancelable(true)
		.setView(v)
		.setPositiveButton(posButtonName,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) 
			{
				dialog.dismiss();	

				clickListener.onOkClick(v);
			}
		});
		alertDialogBuilder.setNegativeButton(negButtonName, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
				dialog.dismiss();
				clickListener.onCancelClick(v);
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}


	public interface IDailogOKClickListener 
	{
		void onOkClick();
		void onCancelClick();	

	}

	public interface ICustomDailogClickListener 
	{
		void onOkClick(final View customView);
		void onCancelClick(final View customView);	

	}

	public void onClickClose(View v)
	{
		//finish();
		goHome();

	}
	
	protected void goHome() {
		Intent intent = new Intent(context, com.prologic.idkey.activities.HomeScreenActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);	
	}
	protected void logInSuccessfully() 
	{
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.prologic.idkey.ACTION_LOGOUT");
		sendBroadcast(broadcastIntent);

	}
	public void onClickMainHome(View v)
	{
		Intent intent = new Intent(context, com.prologic.idkey.activities.HomeScreenActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}
	public void onClickMainKeys(View v)
	{
		setCurrent(com.prologic.idkey.activities.KeyListingActivity.class, null);

	}
	public void onClickMainIdAKey(View v)
	{
		setCurrent(com.prologic.idkey.activities.IdentifyKeyActivity.class, null);
	}
	public void onClickMainAddNew(View v)
	{
		setCurrent(com.tasawr.camera.CameraPictureSnapActivity.class, null);
	}
	public void onClickMainAbout(View v)
	{
		setCurrent(com.prologic.idkey.activities.AboutActivity.class, null);

	}

	protected void showProgressDaoilog(String title,String message,boolean isCancelable)
	{
		if(progressDailog != null)
		{
			progressDailog.setCancelable(isCancelable);
			if(title != null)
				progressDailog.setTitle(title);
			if(message != null)
				progressDailog.setMessage(message);

			progressDailog.show();
		}

	}
	protected void hideProgressDaoilog()
	{
		if(progressDailog != null && progressDailog.isShowing())
			progressDailog.dismiss();

	}
	
	protected void showShortToast(String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		
	}

	protected void showLongToast(String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		
	}
	
	public void logOut()
	{
		Intent intent = new Intent(this, com.prologic.idkey.activities.SignInActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	

}
