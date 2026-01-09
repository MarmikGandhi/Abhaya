package com.example.nirbhay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etAge, etAddress;
    private ImageView profileImage;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Bind views
        profileImage = findViewById(R.id.profileImage);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAge = findViewById(R.id.etAge);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);

        // Load saved details
        loadUserDetails();

        // Save button
        btnSave.setOnClickListener(v -> saveUserDetails());
    }

    private void loadUserDetails() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        etName.setText(prefs.getString("name", ""));
        etEmail.setText(prefs.getString("email", ""));
        etPhone.setText(prefs.getString("phone", ""));
        etAge.setText(prefs.getString("age", ""));
        etAddress.setText(prefs.getString("address", ""));
    }

    private void saveUserDetails() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", etName.getText().toString().trim());
        editor.putString("email", etEmail.getText().toString().trim());
        editor.putString("phone", etPhone.getText().toString().trim());
        editor.putString("age", etAge.getText().toString().trim());
        editor.putString("address", etAddress.getText().toString().trim());

        editor.apply();
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
    }
}
