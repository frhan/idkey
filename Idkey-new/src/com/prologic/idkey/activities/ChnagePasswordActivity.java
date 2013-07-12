package com.prologic.idkey.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.prologic.idkey.CustomProgressDailog;
import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.ChangePasswordCommand;


public class ChnagePasswordActivity extends MainActivity
{
	private EditText etOldPassword;
	private EditText etNewPassword;
	private EditText etRepeatNewPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password);
		
		etNewPassword = (EditText) findViewById(R.id.et_change_new_password);
		etOldPassword = (EditText) findViewById(R.id.et_change_old_password);
		etRepeatNewPassword = (EditText) findViewById(R.id.et_change_password_repeat);
	}

	public void onClickChangePassword(View v)
	{
		String oldPassword = etOldPassword.getText().toString();
		String newPassword = etNewPassword.getText().toString();		
		String newPasswordRepeat = etRepeatNewPassword.getText().toString();

		if(oldPassword.length() == 0 || newPassword.length() == 0 || newPasswordRepeat.length() == 0)
		{
			showOkAlertDailog("Password can not be blank", "Change Password", false);
		}
		else if(!newPassword.equals(newPasswordRepeat))
		{
			showOkAlertDailog("New Password doesn't match", "Change Password", false);
		}else if (isOnline()) 
		{
			new ChangePasswordTask(context, oldPassword, newPassword).execute();
			

		}else {
			showOkAlertDailog("No Internet Connection", "Network", false);
		}

	}
	public void onClickClose(View v) 
	{
		finish();		
	}

	private class ChangePasswordTask extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		private String oldPassword;
		private String newPassword;
		private ChangePasswordCommand changePasswordCommand;
		private CustomProgressDailog progressDialog;
		public ChangePasswordTask(Context context,String oldPassword,String newPassword) 
		{
			this.context = context;
			this.oldPassword = oldPassword;
			this.newPassword = newPassword;

			progressDialog = new CustomProgressDailog(context);
			progressDialog.setTitle("Change password");
			progressDialog.setMessage("Please wait...");

		}
		@Override
		protected void onPreExecute() {	
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			changePasswordCommand = new ChangePasswordCommand(oldPassword, newPassword);
			changePasswordCommand.execute(ApiConnection.getInstance(context));

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);			

			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			showOkAlertDailog(changePasswordCommand.getMessage(), "Change Password", changePasswordCommand.isChangeSuccessfully());

		}

	}

}
