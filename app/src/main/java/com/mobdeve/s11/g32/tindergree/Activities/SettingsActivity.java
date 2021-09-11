package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.mobdeve.s11.g32.tindergree.R;

public class SettingsActivity extends AppCompatActivity {

    private Button btnLogout;
    private ConstraintLayout clDeveloperNotes,clProfileActivity;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changeStatusBarColor();

        this.toolbar = findViewById(R.id.settings_toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Use Firebase emulator instead
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnLogout = findViewById(R.id.btn_settings_log_out);
        clDeveloperNotes = findViewById(R.id.cl_settings_developer_notes);
        clProfileActivity = findViewById(R.id.cl_settings_profile_activity);
        Intent i = getIntent();

        // Sign out the user on click and destroy activities
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Log.d(SwipeActivity.firebaseLogKey, "User signed out.");
                Intent authenticateIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(authenticateIntent);

                // End Register Activity.
                Intent intent = new Intent("finish_main_activity");
                sendBroadcast(intent);

                finish(); // Destroy activity
            }
        });

        clDeveloperNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,DeveloperNotesActivity.class);
                startActivity(intent);
            }
        });

        clProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,UserProfilePageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    protected void onPause(){
         super.onPause();
         overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}