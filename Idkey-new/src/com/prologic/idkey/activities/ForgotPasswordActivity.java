package com.prologic.idkey.activities;

import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.ForgotPasswordCommand;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActivity extends MainActivity implements OnClickListener {

	private Button btnForgotPassword;
	private Button btnClose;
	private EditText etEmail;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.forget_password);

		etEmail = (EditText) findViewById(R.id.et_forgot_email);
		btnForgotPassword = (Button) findViewById(R.id.btn_forgot_password_lay);
		btnForgotPassword.setOnClickListener(this);
		//btnClose = (Button) findViewById(R.id.btn_forgot_close);
		//btnClose.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		if(v == btnForgotPassword)
		{
			String email = etEmail.getText().toString();
			if(email.length() == 0)
			{
				showOkAlertDailog("Please fill up proper email and password", "Sign In", false);
				return;
			}
			
			if(isOnline())
			{
				new ForgotPasswordTask(context, email).execute();
			}
			else 
			{
				showOkAlertDailog("No Internet connection", "Connection", false);
			}
		}
	}

	public class ForgotPasswordTask extends AsyncTask<Void, Void, Void>
	{
		private String email;
		private Context context;
		private ForgotPasswordCommand forgotPasswordCommand;

		private ProgressDialog progressDialog;
		public ForgotPasswordTask(Context context,String email) 
		{
			this.email = email;
			this.context = context;		

			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle("Forgot password");
			progressDialog.setMessage("Please wait...");
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			forgotPasswordCommand = new ForgotPasswordCommand(email);
			forgotPasswordCommand.execute(ApiConnection.getInstance(context));
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			
			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}

			if(forgotPasswordCommand != null)
			{
				showOkAlertDailog(forgotPasswordCommand.getMessage(), "Forgot password", true);
			}
			
			forgotPasswordCommand = null;	
		}


	}


}
