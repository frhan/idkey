package com.prologic.idkey;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;

public abstract class MainActivity extends Activity implements OnClickListener {

	protected Context context;
	protected Resources resources;
	protected static final String TAG = MainActivity.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		context = MainActivity.this;
		resources = this.getResources();

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


	public interface IDailogOKClickListener 
	{
		void onOkClick();
		void onCancelClick();	

	}


}
