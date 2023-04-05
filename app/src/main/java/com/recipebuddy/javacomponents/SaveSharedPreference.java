package com.recipebuddy.javacomponents;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveSharedPreference {

    static final String DEF_USER_NAME = "username";
    static final String DEF_PASSWORD = "password";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences("my.app.RecipeBuddy_preferences", Context.MODE_PRIVATE);
    }

    public static void setUserName(Context ctx, String username) {
            SharedPreferences.Editor edit = getSharedPreferences(ctx).edit();
            edit.putString(DEF_USER_NAME, username);
            edit.commit();

    }

    public static void setPassword(Context ctx, String password) {
        SharedPreferences.Editor edit = getSharedPreferences(ctx).edit();
        edit.putString(DEF_PASSWORD, password);
        edit.commit();
    }

    public static String getUserName(Context ctx) {
            return getSharedPreferences(ctx).getString(DEF_USER_NAME,"");
    }

    public static String getPassword(Context ctx) {
            return getSharedPreferences(ctx).getString(DEF_PASSWORD,"");
    }

    public static void clearUser(Context ctx) {
            SharedPreferences.Editor edit = getSharedPreferences(ctx).edit();
            edit.clear();
            edit.commit();
    }

}
