package com.example.nirbhay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

// ✅ YouTube Player Imports (Added)
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;


@RequiresApi(api = Build.VERSION_CODES.M)
public class Homepage extends AppCompatActivity {

    private Button helpButton, callButton, mapButton, mapButton1, mapButton2;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int CALL_PERMISSION_REQUEST_CODE = 101;

    // User details
    private String userName, userAge, userEmail, userPhone, userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        loadUserDetails();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(v -> makeCall());

        mapButton = findViewById(R.id.mapbutton);
        mapButton.setOnClickListener(v -> openMap("police"));

        mapButton1 = findViewById(R.id.mapbutton1);
        mapButton1.setOnClickListener(v -> openMap("bus"));

        mapButton2 = findViewById(R.id.mapbutton2);
        mapButton2.setOnClickListener(v -> openMap("hospital"));

        helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> sendEmergencyMessage());

        TextView tvName = findViewById(R.id.tvName);
        tvName.setText("Welcome " + userName);

        Toast.makeText(this, "Welcome " + userName, Toast.LENGTH_SHORT).show();


        YouTubePlayerView playerView1 = findViewById(R.id.youtube_player_view);
        YouTubePlayerView playerView2 = findViewById(R.id.youtube_player_view1);
        YouTubePlayerView playerView3 = findViewById(R.id.youtube_player_view2);

        getLifecycle().addObserver(playerView1);
        getLifecycle().addObserver(playerView2);
        getLifecycle().addObserver(playerView3);

        loadYouTubeVideo(playerView1, "DKSo9j-s1LU");
        loadYouTubeVideo(playerView2, "sxfddTbk7iw");
        loadYouTubeVideo(playerView3, "WGVvw7DdlPY");

    }

    // Load user profile
    private void loadUserDetails() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        userName = prefs.getString("name", "User");
        userAge = prefs.getString("age", "");
        userEmail = prefs.getString("email", "");
        userPhone = prefs.getString("phone", "");
        userAddress = prefs.getString("address", "");
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(@NonNull android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.homepage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_account) {
            startActivity(new Intent(this, AccountActivity.class));
            return true;
        } else if (id == R.id.action_emergency) {
            startActivity(new Intent(this, EmergencyContactsActivity.class));
            return true;
        } else if (id == R.id.action_privacy) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
            prefs.edit().clear().apply();
            startActivity(new Intent(Homepage.this, signin_page.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasRequiredPermissions() {
        int smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return smsPermission == PackageManager.PERMISSION_GRANTED &&
                locationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRequiredPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_CODE);
    }

    private void sendEmergencyMessage() {
        if (!hasRequiredPermissions()) {
            requestRequiredPermissions();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String contactsStr = prefs.getString("emergency_contacts", "");
        if (contactsStr.isEmpty()) {
            Toast.makeText(this, "No emergency contacts saved!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] emergencyContacts = contactsStr.split(",");
        final String baseMessage = "Emergency! Please help!";

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            String fullMessage = baseMessage;
            if (location != null) {
                fullMessage += " My location: https://www.google.com/maps/place/" +
                        location.getLatitude() + "," + location.getLongitude();
            } else {
                fullMessage += " Location not available!";
            }

            SmsManager smsManager = SmsManager.getDefault();
            for (String contact : emergencyContacts) {
                contact = contact.trim();
                if (!contact.isEmpty()) {
                    smsManager.sendTextMessage(contact, null, fullMessage, null, null);
                }
            }
            Toast.makeText(Homepage.this, "Emergency message sent!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) sendEmergencyMessage();
            else Toast.makeText(this, "Permissions denied. Cannot send emergency message.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) makeCall();
            else Toast.makeText(this, "Permission denied. Cannot make a call.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    private void makeCall() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String contactsStr = prefs.getString("emergency_contacts", "");

        if (contactsStr.isEmpty()) {
            Toast.makeText(this, "No emergency contacts saved!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] emergencyContacts = contactsStr.split(",");
        if (emergencyContacts.length == 0) {
            Toast.makeText(this, "No valid emergency contacts found!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
            return;
        }

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_CODE);
            return;
        }

        android.os.Handler handler = new android.os.Handler();
        android.telephony.TelephonyManager telephonyManager =
                (android.telephony.TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        final boolean[] callAnswered = {false};
        final long[] callStartTime = {0};
        final boolean[] callInProgress = {false};

        android.telephony.PhoneStateListener phoneStateListener = new android.telephony.PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);

                switch (state) {
                    case android.telephony.TelephonyManager.CALL_STATE_OFFHOOK:
                        callInProgress[0] = true;
                        callStartTime[0] = System.currentTimeMillis();
                        break;

                    case android.telephony.TelephonyManager.CALL_STATE_IDLE:
                        if (callInProgress[0]) {
                            long duration = (System.currentTimeMillis() - callStartTime[0]) / 1000;

                            if (duration >= 20) {   // 20 seconds
                                callAnswered[0] = true;
                            }

                            callInProgress[0] = false;
                            callStartTime[0] = 0;
                        }

                        break;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, android.telephony.PhoneStateListener.LISTEN_CALL_STATE);

        Runnable callNext = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                if (callAnswered[0]) {
                    telephonyManager.listen(phoneStateListener, android.telephony.PhoneStateListener.LISTEN_NONE);
                    Toast.makeText(Homepage.this, "Emergency contact likely answered. Stopping further calls.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (index < emergencyContacts.length) {
                    final String contact = emergencyContacts[index].trim();
                    index++;

                    if (!contact.isEmpty()) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact));
                            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                            Toast.makeText(Homepage.this, "Calling: " + contact, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(Homepage.this, "Failed to call: " + contact, Toast.LENGTH_SHORT).show();
                        }
                    }

                    handler.postDelayed(this, 35000);
                } else {
                    if (!callAnswered[0]) {
                        try {
                            Intent callBackup = new Intent(Intent.ACTION_CALL, Uri.parse("tel:8799293948"));
                            callBackup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callBackup);
                            Toast.makeText(Homepage.this, "No one answered. Calling backup number (9316615839)...", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(Homepage.this, "Failed to call backup number!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    telephonyManager.listen(phoneStateListener, android.telephony.PhoneStateListener.LISTEN_NONE);
                }
            }
        };

        handler.post(callNext);
    }

    private void openMap(String type) {
        String url = "";
        switch (type) {
            case "police": url = "https://www.google.com/maps/search/police+stations+near+me"; break;
            case "bus": url = "https://www.google.com/maps/search/bus+stations+nearby"; break;
            case "hospital": url = "https://www.google.com/maps/search/nearby+hospitals"; break;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public void saveEmergencyContacts(String contactsCommaSeparated) {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        prefs.edit().putString("emergency_contacts", contactsCommaSeparated).apply();
        Toast.makeText(this, "Emergency contacts saved!", Toast.LENGTH_SHORT).show();
    }


    // ✅ Added YouTube Helper Methods (No other code changed)

    private void loadYouTubeVideo(YouTubePlayerView playerView, String videoUrl) {
        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(extractVideoId(videoUrl), 0);
            }
        });
    }


    private String extractVideoId(String url) {
        if (url.contains("shorts/")) {
            return url.substring(url.indexOf("shorts/") + 7, url.indexOf("?"));
        } else if (url.contains("v=")) {
            return url.substring(url.indexOf("v=") + 2);
        } else {
            return url;
        }
    }

}
