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
import com.recipebuddy.components.HomeScreen
import com.recipebuddy.components.ScreenManager
import com.recipebuddy.javacomponents.LoginActivity
import com.recipebuddy.javacomponents.SaveSharedPreference
import com.recipebuddy.util.DatabaseManager
import com.recipebuddy.database.*

lateinit var DEFAULT_BITMAP: Bitmap

class MainActivity : ComponentActivity() {
    var db: AppDatabase? = null
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getInstance(this);

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
        }
            ScreenManager.selectedHomeScreen = ScreenManager.RECIPE_HOME_SCREEN

            setContent {
                Box() {
                    HomeScreen()
                }
            }

    }
}