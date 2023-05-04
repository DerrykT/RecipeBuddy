package com.recipebuddy

import android.database.CursorWindow
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import com.recipebuddy.components.HomeScreen
import com.recipebuddy.util.DatabaseManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.setAccessible(true)
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
            e.printStackTrace()
        }

        DatabaseManager.initDatabase(this)

        DatabaseManager.populate(this)

        setContent {
            Box() {
                HomeScreen()
            }
        }

    }
}