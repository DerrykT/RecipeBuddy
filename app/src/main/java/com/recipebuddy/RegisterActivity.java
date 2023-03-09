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

public class RegisterActivity extends Activity {
    // variable initialization
    private EditText usernameEdt, passwordEdt;
    private Button addUserBtn, homeRedirectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        usernameEdt = findViewById(R.id.idEdtUsername);
        passwordEdt = findViewById(R.id.idEdtPassword);
        addUserBtn = findViewById(R.id.idBtnAddUser);
        homeRedirectBtn = findViewById(R.id.homeRedirect);

        // back button
        homeRedirectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent e = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(e);
            }
        });

        // register user
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdt.getText().toString();
                String password = passwordEdt.getText().toString();

                // check if inputs are empty and display message if they are
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter all the data.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if username and the password match, throw an error
                if (username.equals(password)) {
                    Toast.makeText(RegisterActivity.this, "Username and password cannot match.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if either are under the minimum length requirement for security reasons
                if (username.length() < 5 || password.length() < 5) {
                    Toast.makeText(RegisterActivity.this, "Username and password must be 5 or more characters in length.", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences("my.app.RecipeBuddy_preferences", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                myEdit.putString("username", username);
                myEdit.putString("password", password);
                myEdit.commit();

                // send user back to main menu
                Intent e = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(e);

                // display success message and reset
                Toast.makeText(RegisterActivity.this, "User has been added.", Toast.LENGTH_SHORT).show();
                usernameEdt.setText("");
                passwordEdt.setText("");
            }
        });
    }
}
