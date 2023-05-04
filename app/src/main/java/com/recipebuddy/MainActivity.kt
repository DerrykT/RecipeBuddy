package com.recipebuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.recipebuddy.components.HomeScreen
import com.recipebuddy.components.ProfileHomeScreen
import com.recipebuddy.util.DatabaseManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DatabaseManager.initDatabase(this)

//        DatabaseManager.populate(this)

        setContent {
            Box() {
                HomeScreen()
            }
        }

    }
}