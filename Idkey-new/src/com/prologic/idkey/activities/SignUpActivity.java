package com.prologic.idkey.activities;

import com.prologic.idkey.R;
import com.prologic.idkey.api.ApiConnection;
import com.prologic.idkey.api.WebService;
import com.prologic.idkey.api.command.SignUpCommand;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends MainActivity implements OnClickListener 
{
	private Button btnSignUp;
	private Button btnLogIn;
	private EditText etEmail;
	private EditText etPassword;
	private EditText etPasswordConf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sign_up);	
		etEmail = (EditText) findViewById(R.id.et_register_email);
		etPassword = (EditText) findViewById(R.id.et_register_password);
		etPasswordConf = (EditText) findViewById(R.id.et_register_password_conf);
		btnSignUp = (Button) findViewById(R.id.btn_reg_sign_up);
		btnLogIn = (Button)findViewById(R.id.btn_register_log_in);
		btnSignUp.setOnClickListener(this);
		btnLogIn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v == btnSignUp)
		{
			doSignUpTask();
		}else if(v == btnLogIn)
		{			
			setCurrent(com.prologic.idkey.activities.SignInActivity.class, null);
		}

	}

	private void doSignUpTask() {

		String email = etEmail.getText().toString();
		String pass = etPassword.getText().toString();
		String pasConf = etPasswordConf.getText().toString();

		if(email.length() == 0 || pass.length() == 0 || pasConf.length() == 0 )
		{
			showOkAlertDailog("Please fill up proper email and password", "Sign Up", false);
			return;
		}
		if(pass.length() < 6)
		{
			showOkAlertDailog("Password is too short (minimum 6 characters)", "Sign Up", false);
			return;
		}

		if(!pass.equals(pasConf))
		{
			showOkAlertDailog("Password doesn't match", "Sign Up", false);
			return;
		}		 

		if(isOnline())
		{
			SignUpTask signUpTask = new SignUpTask(context,email,pass);
			signUpTask.execute();

		}else 
		{
			showOkAlertDailog("No Internet connection", "Connection", false);
		}

	}
	public class SignUpTask extends AsyncTask<Void, Void, Void>
	{
		private String email;
		private String pass;
		private Context context;
		private SignUpCommand signUpCommand;
		private ProgressDialog progressDialog;
		public SignUpTask(Context context,String email,String pass) 
		{
			this.context = context;
			this.email = email;
			this.pass = pass;
			progressDialog = new ProgressDialog(context);
			progressDialog.setTitle("Sign Up");
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
			//WebService.getInstance().signUp("russel053009@yahoo.com", "please1");
			signUpCommand = new SignUpCommand(email, pass);
			signUpCommand.execute(ApiConnection.getInstance(context));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);

			if(progressDialog != null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}

			if(signUpCommand != null)
			{
				if(!signUpCommand.isSignUpSuccessfull())
				{
					showOkAlertDailog(signUpCommand.getMessage(), "Sign Up", signUpCommand.isSignUpSuccessfull());
				}else {
					Toast.makeText(context, "Sign up successfully", Toast.LENGTH_SHORT).show();
					setCurrent(com.prologic.idkey.activities.HomeScreenActivity.class, null);
					finish();

				}
			}
			signUpCommand = null;	
			
		}
	}
}
