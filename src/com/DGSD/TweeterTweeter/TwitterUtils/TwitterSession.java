package com.DGSD.TweeterTweeter.TwitterUtils;

import java.util.HashSet;

import twitter4j.auth.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TwitterSession {
	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String TWEET_AUTH_KEY = "auth_key";
	private static final String TWEET_AUTH_SECRET_KEY = "auth_secret_key";
	private static final String TWEET_USER_NAME = "user_name";
	private static final String TWEET_IMAGE = "user_image";
	private static final String SHARED = "Twitter_Preferences";

	public TwitterSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);

		editor = sharedPref.edit();
	}

	public void storeAccessToken(AccessToken accessToken, String username, String image) {
		editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
		editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
		editor.putString(TWEET_USER_NAME, username);
		editor.putString(TWEET_IMAGE, image);
		
		editor.commit();
	}

	public void resetAccessToken() {
		editor.putString(TWEET_AUTH_KEY, null);
		editor.putString(TWEET_AUTH_SECRET_KEY, null);
		editor.putString(TWEET_USER_NAME, null);

		editor.commit();
	}

	public String getUsername() {
		return sharedPref.getString(TWEET_USER_NAME, "");
	}
	
	public String getUserImage() {
		return sharedPref.getString(TWEET_IMAGE, "");
	}

	public AccessToken getAccessToken() {
		String token 		= sharedPref.getString(TWEET_AUTH_KEY, null);
		String tokenSecret 	= sharedPref.getString(TWEET_AUTH_SECRET_KEY, null);

		if (token != null && tokenSecret != null) 
			return new AccessToken(token, tokenSecret);
		else
			return null;
	}
}