package com.example.RecipeBuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SuccessActivity extends Activity {
    private Button homeRedirectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_layout);

        homeRedirectBtn = findViewById(R.id.homeRedirect);

        // back button
        homeRedirectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent e = new Intent(SuccessActivity.this, MainActivity.class);
                startActivity(e);
            }
        });
    }
}
