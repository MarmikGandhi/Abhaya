package com.example.nirbhay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class signin_page extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);

        emailEditText = findViewById(R.id.emailbox);
        passwordEditText = findViewById(R.id.passwordbox);
        Button btn = findViewById(R.id.nextbutton);

        btn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            checkLogin(email, password);
        });
    }

    private void checkLogin(String email, String password) {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String usersJson = prefs.getString("users", "{}");

        try {
            JSONObject usersObj = new JSONObject(usersJson);

            if (!usersObj.has(email)) {
                showToast("No account registered with this email");
                return;
            }

            JSONObject userDetails = usersObj.getJSONObject(email);
            String savedPassword = userDetails.getString("password");

            if (!password.equals(savedPassword)) {
                showToast("Incorrect password");
                return;
            }

            showToast("Signin successful!");
            prefs.edit().putString("loggedInUser", email).apply();

            startActivity(new Intent(signin_page.this, Homepage.class));
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Error reading users");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
