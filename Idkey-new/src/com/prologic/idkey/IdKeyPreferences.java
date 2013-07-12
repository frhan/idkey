package com.prologic.idkey;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class IdKeyPreferences {

	private static final String PREFS_NAME = "com.idkey.Settings_preferences";
	private static String EMAIL = "email";
	private static String PASSWORD = "password";
	private static String ISLOGIN = "is_login";
	private static String email = "";
	private static String password = "";
	private static boolean isLogin = false;

	public static String getEmail() {
		return email;
	}
	public static void setEmail(String email) {
		IdKeyPreferences.email = email;
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		IdKeyPreferences.password = password;
	}
	public static boolean isLogin() {
		return isLogin;
	}
	public static void setLogin(boolean isLogin) {
		IdKeyPreferences.isLogin = isLogin;
	}
	
	public static  void save(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(EMAIL,email);
		editor.putBoolean(ISLOGIN, isLogin);
		editor.putString(PASSWORD, password);

		editor.commit();
	}
	public  static void initPreferences(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		
		boolean defValue = false;
		isLogin = settings.getBoolean(ISLOGIN, defValue);
		email = settings.getString(EMAIL, "");
		password = settings.getString(PASSWORD, "");
		Log.i("Rubbbama", "email:"+email);

	}
}
