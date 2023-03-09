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
    private Button homeRedirectBtn, loginBtn, registerBtn;
    private EditText usernameEdt, passwordEdt;
    private final LoginActivity activity = LoginActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        usernameEdt = findViewById(R.id.idEdtUsername);
        passwordEdt = findViewById(R.id.idEdtPassword);
        loginBtn = findViewById(R.id.login);
        registerBtn = findViewById(R.id.register);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent r = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(r);
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

                    String usernameCheck = SaveSharedPreference.getUserName(LoginActivity.this);
                    String passwordCheck = SaveSharedPreference.getPassword(LoginActivity.this);

                    // Pull info from shared preferences



                    // If what's entered matches what has been saved, redirect
                    if (username == usernameCheck && password == passwordCheck) {
                        Intent e = new Intent(LoginActivity.this, SuccessActivity.class);
                        startActivity(e);
                    } else { // if input mismatch, display message
                        Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                    }

                }

                // Pull from sharedpreferences
                String usernameCheck = SaveSharedPreference.getUserName(LoginActivity.this);
                String passwordCheck = SaveSharedPreference.getPassword(LoginActivity.this);

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
