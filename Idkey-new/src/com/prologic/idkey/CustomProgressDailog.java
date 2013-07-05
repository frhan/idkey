package com.prologic.idkey;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CustomProgressDailog extends ProgressDialog
{
	private IProgressDailogListener listener;

	public CustomProgressDailog(Context context,IProgressDailogListener listener)
	{
		super(context);
		this.setCancelable(true);
		this.listener = listener;	

		this.setOnCancelListener(cancelListener);
	}
	public CustomProgressDailog(Context context)
	{
		super(context);
		this.setCancelable(true);				
	}
	public CustomProgressDailog(Context context,String title,String message)
	{
		super(context);
		this.setCancelable(true);				
		setTitle(title);
		setMessage(message);
	}

	public void setCancelListener(OnCancelListener cancelListener) {
		this.cancelListener = cancelListener;
	}

	private OnCancelListener cancelListener = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {

			listener.onCancelDailog();			
		}
	};

	public interface IProgressDailogListener
	{
		void onCancelDailog();
	}


}
