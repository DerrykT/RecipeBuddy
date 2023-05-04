package com.recipebuddy

import android.content.Intent
import android.database.CursorWindow
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.recipebuddy.components.HomeScreen
import com.recipebuddy.javacomponents.LoginActivity
import com.recipebuddy.javacomponents.SaveSharedPreference
import com.recipebuddy.util.DatabaseManager
import java.io.ByteArrayOutputStream

lateinit var DEFAULT_BITMAP: Bitmap

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DEFAULT_BITMAP = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.cookies_recipe_image
        )

        try {
            val field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.setAccessible(true)
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
            e.printStackTrace()
        }

        DatabaseManager.initDatabase(this)

        DatabaseManager.populate(this)

        if (SaveSharedPreference.getUserName(this@MainActivity)?.length ?: null == 0) {
            val i = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(i)
        } else {
            setContent {
                Box() {
                    HomeScreen()
                }
            }
        }

    }
}