package com.youtube.parse;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.parse.ParseUser;

public class SPManager {

	public static int FreeMothlyTokens = 25;
	public static String ApplicationId_ = "USEYOUROWN";
	public static String ClientId_ = "USEYOUROWN";

	static final String PREF_USER_NAME = "username";
	static final String PREF_USER_PASSWORD = "password";
	static final String PREF_REMEMBER_ME = "remember";

	static final String PREF_PRIORITY = "priority";
	static final String PREF_SIGNUP_DATE = "signup_date";
	static final String PREF_QUEUE_LIMIT = "queue_limit";

	static final String PREF_DAY_LIMIT = "day_limit";
	static final String PREF_DAY_LIMIT_DATE = "day_limit_date";

	static final String PREF_VIDEO_DURATION_LIMIT = "video_duration_limit";
	static int priority = 5;
	public static int DL_Video_Duration_Limit = 10;

	public static String MUSIC_ROOT_DIR = "";
	public static ParseUser current_user = null;// ParseUser.getCurrentUser();
	
	public static final String YOUTUBE_DEVELOPER_KEY = "USEYOUROWN";

	static SharedPreferences getSharedPreferences(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public static int getQueueLimit(Context ctx) {
		return getSharedPreferences(ctx).getInt(PREF_QUEUE_LIMIT, 0);
	}

	// User name functions
	public static void setUserNameAndPassword(Context ctx, String userName,
			String password) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.putString(PREF_USER_NAME, userName);
		editor.putString(PREF_USER_PASSWORD, password);
		editor.putBoolean(PREF_REMEMBER_ME, true);
		editor.commit();
	}

	public static String getUserName(Context ctx) {
		return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
	}

	public static String getUserPassword(Context ctx) {
		return getSharedPreferences(ctx).getString(PREF_USER_PASSWORD, "");
	}

	public static boolean getRememberMe(Context ctx) {
		return getSharedPreferences(ctx).getBoolean(PREF_REMEMBER_ME, false);
	}

	public static void clear(Context ctx) {
		Editor editor = getSharedPreferences(ctx).edit();
		editor.clear(); // clear all stored data
		editor.commit();
	}
}