package com.recipebuddy.javacomponents

import android.app.Activity
import android.widget.EditText
import android.os.Bundle
import com.recipebuddy.R
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.recipebuddy.MainActivity
import com.recipebuddy.database.*

class LoginActivity : Activity() {
    // variable initialization
    lateinit var loginBtn: Button
    lateinit var registerBtn: Button
    lateinit var usernameEdt: EditText
    lateinit var passwordEdt: EditText

    lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        usernameEdt = findViewById(R.id.idEdtUsername)
        passwordEdt = findViewById(R.id.idEdtPassword)
        loginBtn = findViewById(R.id.login)
        registerBtn = findViewById(R.id.register)
        db = AppDatabase.getInstance(this)
        val dao = db.readData()
        var found = true
        registerBtn.setOnClickListener(View.OnClickListener {
            val r = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(r)
        })
        loginBtn.setOnClickListener(View.OnClickListener {
            var username = usernameEdt.text.toString()
            var password = passwordEdt.text.toString()

            // if inputs are empty, display message
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Please enter all the data.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            // if either have spaces, replace with underscores to make sure true names match
            if (username.matches(".*\\s.*".toRegex()) || password.matches(".*\\s.*".toRegex())) {
                Toast.makeText(this@LoginActivity, "spaces", Toast.LENGTH_SHORT).show()
                val spacedname = username.replace("\\s+".toRegex(), "_")
                val spacedpass = password.replace("\\s+".toRegex(), "_")

                Thread {
                    val passReturn = dao.getPassword(spacedname)

                    // If what's entered matches what has been saved, redirect
                    if (spacedpass == passReturn) {
                        val e = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(e)
                    } else { // if input mismatch, display message
                        found = false
                    }
                }.start()
                Thread().interrupt()

            }

            Thread {
                var passReturn = dao.getPassword(username)
                // If input matches, redirect
                if (password == passReturn) {
                    val e = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(e)
                } else { // if input mismatch, display message
                    found = false
                }
            }.start()
            Thread().interrupt()

            if (found == false) {
                Toast.makeText(this@LoginActivity, "User not found.", Toast.LENGTH_SHORT).show()
            }

        })
    }
}