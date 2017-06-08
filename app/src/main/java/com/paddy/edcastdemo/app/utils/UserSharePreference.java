package com.paddy.edcastdemo.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by prashant on 6/7/17.
 */

public class UserSharePreference {
    private static final String IS_LOGGED_IN = "isLogin";
    private static final String USER_ID = "id";
    private static SharedPreferences mSharedPreferences;
    private final String MyPREFERENCES = "UserPreference";
    private Context mContext;

    public UserSharePreference(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }

    /**
     * clear all preference
     */
    public void clearPreferences() {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().clear().apply();
        }
    }

    public boolean isLoggedIn() {
        return mSharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        mSharedPreferences.edit().putBoolean(IS_LOGGED_IN, loggedIn).apply();
    }

    public String getUserId() {
        return mSharedPreferences.getString(USER_ID, "");
    }

    public void setUserId(String userId) {
        mSharedPreferences.edit().putString(USER_ID, userId).apply();
    }

}
