package com.example.nirbhay;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.privacyToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Handle Toolbar back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Back Button at bottom of ScrollView
        Button btnBack = findViewById(R.id.btnBackPrivacy);
        btnBack.setOnClickListener(v -> finish());
    }
}
