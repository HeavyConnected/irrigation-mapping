package com.heavyconnect.heavyconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.heavyconnect.heavyconnect.entities.User;

/**
 * Created by andremenezes on 8/4/15.
 */
public class StorageUtils {
    public static final String SP_NAME = "heavyprefs";
    public static final String SP_USER_KEY = "user";
    public static final String SP_IS_LOGGED_IN_KEY = "loggedIn";
    public static final String SP_TOKEN_KEY = "tk";

    public static void clearPrefs(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, 0);
        prefs.edit().clear().commit();
    }

    public static void storeUserData(Context context, User user){
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, 0);
        SharedPreferences.Editor spEditor = prefs.edit();
        spEditor.putString(SP_USER_KEY, (new Gson()).toJson(user));
        spEditor.commit();
    }

    public static User getUserData(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, 0);
        String jsonString = prefs.getString(SP_USER_KEY, null);

        if(jsonString == null)
            return null;

        return (new Gson()).fromJson(jsonString, User.class);
    }

    public static void putIsLoggedIn(Context context, boolean isLoggedIn){
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, 0);
        SharedPreferences.Editor spEditor = prefs.edit();
        spEditor.putBoolean(SP_IS_LOGGED_IN_KEY, isLoggedIn);
        spEditor.commit();
    }

    public static boolean getIsLoggedIn(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SP_NAME, 0);
        return prefs.getBoolean(SP_IS_LOGGED_IN_KEY, false);
    }

}
