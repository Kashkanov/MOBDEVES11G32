package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.mobdeve.s11.g32.tindergree.R;

public class SettingsActivity extends AppCompatActivity {

    private Button logoutBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Use Firebase emulator instead
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        logoutBtn = findViewById(R.id.btn_settings_log_out);

        Intent i = getIntent();

        // Sign out the user on click and destroy activities
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Log.d(SwipeActivity.firebaseLogKey, "User signed out.");
                Intent authenticateIntent = new Intent(SettingsActivity.this, RegisterActivity.class);
                startActivity(authenticateIntent);

                // End Register Activity.
                Intent intent = new Intent("finish_main_activity");
                sendBroadcast(intent);

                finish(); // Destroy activity
            }
        });
    }
}