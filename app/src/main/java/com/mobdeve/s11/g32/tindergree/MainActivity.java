package com.mobdeve.s11.g32.tindergree;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //temporarily set to activity_login just to test.
        //A new activity should be created for the activity_login.
        setContentView(R.layout.activity_login);
        hideBar();
    }

    private void hideBar(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}