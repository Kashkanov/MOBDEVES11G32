package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.mobdeve.s11.g32.tindergree.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO (1) Update this to activity_main once the activity for activity_login has been created
        setContentView(R.layout.activity_post_register_upload_photos);
        hideBar();
    }

    private void hideBar(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}