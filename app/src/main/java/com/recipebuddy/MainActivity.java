package com.example.RecipeBuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // creating variables for our edittext, button and dbhandler

    private Button loginRedirectBtn, registerRedirectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginRedirectBtn = findViewById(R.id.loginRedirect);
        registerRedirectBtn = findViewById(R.id.regRedirect);

        // login button
        loginRedirectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        // redirect button
        registerRedirectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent q = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(q);
            }
        });

    }
}