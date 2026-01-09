package com.example.nirbhay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EmergencyContactsActivity extends AppCompatActivity {

    private LinearLayout contactsContainer;
    private Button btnAddContact, btnSaveContacts;
    private ImageView currentImageView;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        contactsContainer = findViewById(R.id.contactsContainer);
        btnAddContact = findViewById(R.id.btnAddContact);
        btnSaveContacts = findViewById(R.id.btnSaveContacts);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (currentImageView != null && selectedImageUri != null) {
                            currentImageView.setImageURI(selectedImageUri);
                            currentImageView.setTag(selectedImageUri.toString());
                        }
                    }
                });

        // Load saved contacts
        loadContacts();

        btnAddContact.setOnClickListener(v -> addContactRow(null, null));
        btnSaveContacts.setOnClickListener(v -> saveContacts());
    }

    private void addContactRow(@Nullable String name, @Nullable String number) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View row = inflater.inflate(R.layout.contact_row, contactsContainer, false);

        EditText nameEt = row.findViewById(R.id.contactName);
        EditText numberEt = row.findViewById(R.id.contactNumber);
        ImageButton btnRemove = row.findViewById(R.id.btnRemove);

        if (name != null) nameEt.setText(name);
        if (number != null) numberEt.setText(number);

        btnRemove.setOnClickListener(v -> contactsContainer.removeView(row));
        contactsContainer.addView(row);
    }

    private void saveContacts() {
        int count = contactsContainer.getChildCount();
        StringBuilder contactsBuilder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            View row = contactsContainer.getChildAt(i);
            EditText numberEt = row.findViewById(R.id.contactNumber);

            String number = numberEt.getText().toString().trim();

            if (!number.isEmpty()) {
                if (contactsBuilder.length() > 0) {
                    contactsBuilder.append(",");
                }
                contactsBuilder.append(number);
            }
        }

        String contactsStr = contactsBuilder.toString();

        SharedPreferences prefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        prefs.edit().putString("emergency_contacts", contactsStr).apply();

        Toast.makeText(this, "Contacts saved: " + contactsStr, Toast.LENGTH_LONG).show();
    }

    private void loadContacts() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        String contactsStr = prefs.getString("emergency_contacts", "");

        if (!contactsStr.isEmpty()) {
            String[] numbers = contactsStr.split(",");
            for (String number : numbers) {
                addContactRow(null, number.trim());
            }
        } else {
            addContactRow(null, null);
        }
    }
}
