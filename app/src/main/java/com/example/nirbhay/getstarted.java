package com.example.nirbhay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class getstarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstarted);
        TextView button2 = (TextView) findViewById(R.id.button2);
        button2.setOnClickListener(v -> {
            Intent intent = new Intent(getstarted.this, signin_page.class);
            startActivity(intent);
        });
        TextView button3 = (TextView) findViewById(R.id.button3);
        button3.setOnClickListener(v -> {
            Intent intent = new Intent(getstarted.this, signup_page.class);
            startActivity(intent);
        });
    }

}