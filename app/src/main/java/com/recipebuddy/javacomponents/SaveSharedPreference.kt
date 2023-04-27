package com.recipebuddy.javacomponents

import android.content.Context
import android.content.SharedPreferences

object SaveSharedPreference {
    const val DEF_USER_NAME = "username"
    const val DEF_PASSWORD = "password"
    fun getSharedPreferences(ctx: Context): SharedPreferences {
        return ctx.getSharedPreferences("my.app.RecipeBuddy_preferences", Context.MODE_PRIVATE)
    }

    fun setUserName(ctx: Context, username: String?) {
        val edit = getSharedPreferences(ctx).edit()
        edit.putString(DEF_USER_NAME, username)
        edit.commit()
    }

    fun setPassword(ctx: Context, password: String?) {
        val edit = getSharedPreferences(ctx).edit()
        edit.putString(DEF_PASSWORD, password)
        edit.commit()
    }

    fun getUserName(ctx: Context): String? {
        return getSharedPreferences(ctx).getString(DEF_USER_NAME, "")
    }

    fun getPassword(ctx: Context): String? {
        return getSharedPreferences(ctx).getString(DEF_PASSWORD, "")
    }

    fun clearUser(ctx: Context) {
        val edit = getSharedPreferences(ctx).edit()
        edit.clear()
        edit.commit()
    }
}