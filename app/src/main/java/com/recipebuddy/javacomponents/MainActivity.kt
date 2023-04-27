package com.recipebuddy.javacomponents

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.recipebuddy.R
import com.recipebuddy.components.AppDatabase
import com.recipebuddy.components.Insertion
import com.recipebuddy.KotlinMainActivity

class MainActivity : AppCompatActivity() {
    var db: AppDatabase? = null

    // onCreate method
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getInstance(this);
        val dao: Insertion = db.insertion()

        // Check if user is not logged in (aka no user created)
        // If there is no created user, redirect the user to the login activity
        if (SaveSharedPreference.getUserName(this@MainActivity)?.length ?: null == 0) {
            val i = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(i)
            setContentView(R.layout.activity_main)
        } else {
            val s = Intent(this@MainActivity, KotlinMainActivity::class.java)
            startActivity(s)
        }
    }

    // Might not need this for onPause, but clearing user should be used for logout button
    // May potentially store the verified user info in onPause once logged in??
    // Idea: create "starter variables" for user and password to be used for logging in
    // Save them if they are verified and the user logs in?
    protected override fun onPause() {
        super.onPause()
        SaveSharedPreference.clearUser(this@MainActivity)
    }
}