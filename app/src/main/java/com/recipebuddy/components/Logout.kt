package com.recipebuddy.components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.recipebuddy.javacomponents.LoginActivity
import com.recipebuddy.javacomponents.SaveSharedPreference

@Composable
fun Logout() {
    val context = LocalContext.current
    SaveSharedPreference.clearUser(context)
    context.startActivity((Intent(context, LoginActivity::class.java)))
}