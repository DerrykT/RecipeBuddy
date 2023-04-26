package com.recipebuddy

import android.os.Bundle
import android.util.Log
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
import com.recipebuddy.components.Recipe_Info
import kotlin.concurrent.thread

lateinit var db: AppDatabase // declare the variable here
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "recipe_database"
        ).build()
        Log.d("DBTESTING", "DB CREATED")


        Thread(Runnable {
            val recipeInfo = Recipe_Info(0, "Spaghetti Carbonara", "A classic pasta dish made with eggs, cheese, and bacon.", "1. Cook spaghetti according to package directions. 2. Fry bacon in a pan until crispy. 3. Beat eggs and cheese together in a bowl. 4. Drain spaghetti and add to the pan with bacon. 5. Pour egg mixture over spaghetti and stir until eggs are cooked. Serve hot.", 5.0)

            db.insertion().insertRecipeInfo(recipeInfo)
            Log.d("DBTESTING", "inserted")

            val testOutput = db.readData().getRecipeNames().joinToString("-")

            Log.d("DBTESTING", testOutput)
        }).start()

        setContent(){
            HomeScreen()
        }

    }
}