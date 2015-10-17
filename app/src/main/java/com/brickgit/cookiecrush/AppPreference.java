package com.brickgit.cookiecrush;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {

	public static final String APP_PREFERENCES = "com.brickgit.cookiecrush.preferences";
	public static final String HIGHEST_SCORE = "highest_score";

	public static int getHighestScore(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		return preferences.getInt(HIGHEST_SCORE, 0);
	}

	public static boolean setHighestScore(Context context, int highestScore) {
		SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(HIGHEST_SCORE, highestScore);
		return editor.commit();
	}
}
