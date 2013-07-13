package com.prologic.idkey;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class IdKeyPreferences {

	private static final String PREFS_NAME = "com.idkey.Settings_preferences";
	private static String EMAIL = "email";
	private static String PASSWORD = "password";
	private static String ISLOGIN = "is_login";
	private static String IS_FIRST_TIME = "is_first_time";
	private static String IS_SIGNED_UP = "is_sign_up";
	private static String AUTH_TOKEN = "auth_toekn";
	private static String REMEMBER_PASSWORD = "remember_password";

	private static String email = "";
	private static String password = "";
	private static String authToken = "";
	private static boolean isLogin = false;
	private static boolean isFirstTime = true;
	private static boolean isSignedUp = false;
	private static boolean isRememberPassword = false;

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
	public static void setAuthToken(String token) {
		IdKeyPreferences.authToken = token;
	}
	public static String getAuthToken() {
		return authToken;
	}
	public static boolean isLogin() {
		return isLogin;
	}
	public static void setLogin(boolean isLogin) {
		IdKeyPreferences.isLogin = isLogin;
	}
	public static boolean isRemember() {
		return isRememberPassword;
	}
	public static void setRemember(boolean isRemember) {
		IdKeyPreferences.isRememberPassword = isRemember;
	}
	public static boolean isFirstTime() {
		return isFirstTime;
	}

	public static void setFirstTime(boolean isFirstTime) {
		IdKeyPreferences.isFirstTime = isFirstTime;
	}
	public static boolean isSignedUP() {
		return isSignedUp;
	}
	public static void setSignedUp(boolean 	isSignedUp) {
		IdKeyPreferences.isSignedUp = isSignedUp;
	}

	public static  void save(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(EMAIL,email);
		editor.putBoolean(ISLOGIN, isLogin);
		editor.putString(PASSWORD, password);
		editor.putBoolean(IS_FIRST_TIME, isFirstTime);
		editor.putBoolean(IS_SIGNED_UP, isSignedUp);
		editor.putString(AUTH_TOKEN, authToken);
		editor.putBoolean(REMEMBER_PASSWORD, isRememberPassword);

		editor.commit();
	}
	public  static void initPreferences(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

		isLogin = settings.getBoolean(ISLOGIN, isLogin);
		isFirstTime = settings.getBoolean(IS_FIRST_TIME, isFirstTime);
		isSignedUp =  settings.getBoolean(IS_SIGNED_UP,isSignedUp);
		isRememberPassword = settings.getBoolean(REMEMBER_PASSWORD, isRememberPassword);
		email = settings.getString(EMAIL,email);
		password = settings.getString(PASSWORD, password);
		authToken = settings.getString(AUTH_TOKEN, authToken);
		Log.i("ID-KEY", "email:"+email);

	}
}
