package com.recipebuddy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.recipebuddy.components.AppDatabase
import com.recipebuddy.components.HomeScreen
import com.recipebuddy.components.ProfileHomeScreen

lateinit var db: AppDatabase
class KotlinMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(this)

        setContent {
            HomeScreen()
        }

    }
}