package com.example.RecipeBuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    // onCreate method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is not logged in (aka no user created)
        // If there is no created user, redirect the user to the login activity
        if (SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);

            setContentView(R.layout.activity_main);

        }

        // If the user is logged in, then display the home page
        // *For testing purposes this redirects to the successactivity*
        // I will update to redirect login back here and have this else
        // display the home page once I get to working with the files
        else {
            Intent s = new Intent(MainActivity.this, SuccessActivity.class);
            startActivity(s);
        }

    }

    // Might not need this for onPause, but clearing user should be used for logout button
    // May potentially store the verified user info in onPause once logged in??
    // Idea: create "starter variables" for user and password to be used for logging in
    // Save them if they are verified and the user logs in?
    @Override
    protected void onPause() {
        super.onPause();
        SaveSharedPreference.clearUser(MainActivity.this);

    }
}