package com.prologic.idkey.activities;

import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.command.SignInCommand;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends MainActivity implements OnClickListener
{
	private Button btnLogIn;
	private Button btnForgotPassword;
	private EditText etEmail;
	private EditText etPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.log_in);
		etEmail = (EditText) findViewById(R.id.et_login_email);
		etPassword = (EditText) findViewById(R.id.et_login_pass);
		btnLogIn = (Button) findViewById(R.id.btn_log_in);
		btnForgotPassword = (Button) findViewById(R.id.btn_forgot_pass);

		btnLogIn.setOnClickListener(this);
		btnForgotPassword.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.btn_log_in:
			logIn();
			break;
		case R.id.btn_forgot_pass:
			setCurrent(com.prologic.idkey.activities.ForgotPasswordActivity.class, null);
			break;

		default:
			break;
		}
	}

	private void logIn()
	{
		String email = etEmail.getText().toString();
		String pass = etPassword.getText().toString();

		if(email.length() == 0 || pass.length() == 0 )
		{
			showOkAlertDailog("Please fill up proper email and password", "Sign In", false);
			return;
		}
		if(pass.length() < 6)
		{
			showOkAlertDailog("Password is too short (minimum 6 characters)", "Sign Up", false);
			return;
		}
		if(isOnline())
		{

			new LogInTask(context, email, pass).execute();
		}
		else 
		{
			showOkAlertDailog("No Internet connection", "Connection", false);
		}
	}

	private  class LogInTask extends AsyncTask<Void, Void, Void>
	{
		private Context context;
		private String email;
		private String pass;
		private SignInCommand signInCommand;
		private ProgressDialog progressDialog;

		public LogInTask(Context context,String email,String pass) 
		{
			this.context = context;
			this.email = email;
			this.pass = pass;

			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle("Log In");
			progressDialog.setMessage("Please wait...");

		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog.show();

		}
		@Override
		protected Void doInBackground(Void... params) {

			signInCommand = new SignInCommand(email, pass);
			signInCommand.execute(ApiConnection.getInstance(context));
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}


			if(signInCommand != null)
			{
				if(!signInCommand.isSignInSuccessfull()){
					showOkAlertDailog(signInCommand.getMessage(), "Log In", signInCommand.isSignInSuccessfull());
				}else 
				{
					Toast.makeText(context, "Log in successfully", Toast.LENGTH_SHORT).show();
					setCurrent(com.prologic.idkey.activities.HomeScreenActivity.class, null);
					finish();

				}
			}
			signInCommand = null;	

		}

	} 

}
