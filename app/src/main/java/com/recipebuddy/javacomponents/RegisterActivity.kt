package com.recipebuddy.javacomponents

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import com.recipebuddy.R
import com.recipebuddy.components.AppDatabase
import com.recipebuddy.components.Insertion
import com.recipebuddy.components.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Boolean.TRUE
import kotlin.concurrent.thread



class RegisterActivity : Activity() {
    lateinit var db: AppDatabase

    // variable initialization
    lateinit var usernameEdt: EditText
    lateinit var passwordEdt: EditText
    lateinit var addUserBtn: Button
    lateinit var homeRedirectBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        // initialization
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        db = AppDatabase.getInstance(this)
        val dao: Insertion = db.insertion()
        usernameEdt = findViewById(R.id.idEdtUsername)
        passwordEdt = findViewById(R.id.idEdtPassword)
        addUserBtn = findViewById(R.id.idBtnAddUser)
        homeRedirectBtn = findViewById(R.id.homeRedirect)

        // back button
        homeRedirectBtn.setOnClickListener {
            val e = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(e)
        }

        // register user
        addUserBtn.setOnClickListener(View.OnClickListener {

            val username: String = usernameEdt.getText().toString()
            val password: String = passwordEdt.getText().toString()

            // check if inputs are empty and display message if they are
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all the data.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            // if username and the password match, throw an error
            if (username == password) {
                Toast.makeText(this, "Username and password cannot match.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            // if either are under the minimum length requirement for security reasons
            if (username.length < 5 || password.length < 5) {
                Toast.makeText(this, "Username and password must be 5 or more characters in length.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val user = Users(username, password)

            Thread {
                dao.insertUser(user)
            }.start()
            Thread().interrupt()

            SaveSharedPreference.setUserName(this, username);
            SaveSharedPreference.setPassword(this, password);
            // send user back to login
            val e = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(e)

            // display success message and reset
            Toast.makeText(this@RegisterActivity, "User has been added.", Toast.LENGTH_SHORT).show()
            usernameEdt.setText("")
            passwordEdt.setText("")

        })
    }
}