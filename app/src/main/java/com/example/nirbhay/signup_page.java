package com.example.nirbhay;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Pattern;

public class signup_page extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, phoneEditText;
    private EditText nameEditText, addressEditText;
    private TextView dobTextView, ageTextView;
    private String selectedDob = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        emailEditText = findViewById(R.id.emailbox);
        passwordEditText = findViewById(R.id.passwordbox);
        phoneEditText = findViewById(R.id.phonebox);
        nameEditText = findViewById(R.id.namebox);
        addressEditText = findViewById(R.id.addressbox);
        dobTextView = findViewById(R.id.dobTextView);
        ageTextView = findViewById(R.id.ageTextView);

        TextView txtSignin = findViewById(R.id.signinoption);
        txtSignin.setOnClickListener(v -> startActivity(new Intent(signup_page.this, signin_page.class)));

        addValidationListeners();

        dobTextView.setOnClickListener(v -> showDatePicker());

        Button btn = findViewById(R.id.nextbutton1);
        btn.setOnClickListener(v -> {
            if (validateAllFields()) {
                saveUser();
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDob = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dobTextView.setText("DOB: " + selectedDob);

                    int age = calculateAge(selectedYear, selectedMonth, selectedDay);
                    ageTextView.setText(String.valueOf(age));
                }, year, month, day);

        datePickerDialog.show();
    }

    private int calculateAge(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - year;
        if (today.get(Calendar.MONTH) < month ||
                (today.get(Calendar.MONTH) == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
            age--;
        }
        return age;
    }

    private void addValidationListeners() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!isValidEmail(s.toString().trim())) emailEditText.setError("Invalid email");
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!isValidPassword(s.toString().trim()))
                    passwordEditText.setError("Password must be 8+ chars, 1 uppercase, 1 digit, 1 special");
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!isValidPhoneNumber(s.toString().trim())) phoneEditText.setError("Enter 10-digit phone number");
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private boolean validateAllFields() {
        if (!isValidEmail(emailEditText.getText().toString().trim())) {
            emailEditText.setError("Invalid email");
            return false;
        }
        if (!isValidPassword(passwordEditText.getText().toString().trim())) {
            passwordEditText.setError("Password must be strong");
            return false;
        }
        if (!isValidPhoneNumber(phoneEditText.getText().toString().trim())) {
            phoneEditText.setError("Invalid phone");
            return false;
        }
        if (selectedDob == null) {
            dobTextView.setError("Please select DOB");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        if (!Pattern.compile("[A-Z]").matcher(password).find()) return false;
        if (!Pattern.compile("[a-z]").matcher(password).find()) return false;
        if (!Pattern.compile("[0-9]").matcher(password).find()) return false;
        return Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return Pattern.compile("^\\d{10}$").matcher(phoneNumber).matches();
    }

    private void saveUser() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String usersJson = prefs.getString("users", "{}");

        try {
            JSONObject usersObj = new JSONObject(usersJson);

            String email = emailEditText.getText().toString().trim();
            if (usersObj.has(email)) {
                showToast("This email is already registered");
                return;
            }

            JSONObject userDetails = new JSONObject();
            userDetails.put("password", passwordEditText.getText().toString().trim());
            userDetails.put("phone", phoneEditText.getText().toString().trim());
            userDetails.put("name", nameEditText.getText().toString().trim());
            userDetails.put("address", addressEditText.getText().toString().trim());
            userDetails.put("dob", selectedDob);
            userDetails.put("age", ageTextView.getText().toString().trim());

            usersObj.put(email, userDetails);
            prefs.edit().putString("users", usersObj.toString()).apply();

            // Save current user for easy access
            prefs.edit()
                    .putString("current_user_email", email)
                    .putString("name", nameEditText.getText().toString().trim())
                    .putString("email", email)
                    .putString("phone", phoneEditText.getText().toString().trim())
                    .putString("age", ageTextView.getText().toString().trim())
                    .putString("address", addressEditText.getText().toString().trim())
                    .apply();

            showToast("Signup successful!");
            startActivity(new Intent(signup_page.this, Homepage.class));
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Error saving user");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}