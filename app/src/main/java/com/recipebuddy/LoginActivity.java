package com.example.RecipeBuddy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends Activity {
    // variable initialization
    private Button homeRedirectBtn, loginBtn;
    private EditText usernameEdt, passwordEdt;
    private final LoginActivity activity = LoginActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        usernameEdt = findViewById(R.id.idEdtUsername);
        passwordEdt = findViewById(R.id.idEdtPassword);
        homeRedirectBtn = findViewById(R.id.homeRedirect);
        loginBtn = findViewById(R.id.login);

        // back button
        homeRedirectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent e = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(e);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = usernameEdt.getText().toString();
                String password = passwordEdt.getText().toString();
                // if inputs are empty, display message
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter all the data.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if either have spaces, replace with underscores to make sure true names match
                if (username.matches(".*\\s.*") || password.matches(".*\\s.*")) {
                    Toast.makeText(LoginActivity.this, "spaces", Toast.LENGTH_SHORT).show();
                    String spacedname = username.replaceAll("\\s+", "_");
                    String spacedpass = password.replaceAll("\\s+", "_");

                    // Pull info from shared preferences
                    SharedPreferences sh = getSharedPreferences("my.app.RecipeBuddy_preferences", MODE_PRIVATE);
                    String usernameCheck = sh.getString("username", "");
                    String passwordCheck = sh.getString("password", "");

                    // If what's entered matches what has been saved, redirect
                    if (username == usernameCheck && password == passwordCheck) {
                        Intent e = new Intent(LoginActivity.this, SuccessActivity.class);
                        startActivity(e);
                    } else { // if input mismatch, display message
                        Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                    }

                }

                // Pull from sharedpreferences
                SharedPreferences sh = getSharedPreferences("my.app.RecipeBuddy_preferences", MODE_PRIVATE);
                String usernameCheck = sh.getString("username", "");
                String passwordCheck = sh.getString("password", "");

                // If input matches, redirect

                if (username.equals(usernameCheck) && password.equals(passwordCheck)) {
                    Intent e = new Intent(LoginActivity.this, SuccessActivity.class);
                    startActivity(e);
                } else { // if input mismatch, display message
                    Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
