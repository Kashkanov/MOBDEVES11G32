package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.s11.g32.tindergree.DataHelpers.CardDataHelper;
import com.mobdeve.s11.g32.tindergree.Adapters.MatchAdapter;
import com.mobdeve.s11.g32.tindergree.DataHelpers.Keys;
import com.mobdeve.s11.g32.tindergree.Models.CardProfile;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class DisplayChatsActivity extends AppCompatActivity {

    private RecyclerView rvMatches;
    private MatchAdapter matchAdapter;
    private ArrayList<CardProfile> profiles;
    private Toolbar toolbar;
    private SharedPreferences sp;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_TindergreeDark);
        }
        else{
            setTheme(R.style.Theme_Tindergree);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chats);
        changeStatusBarColor();
        profiles = new ArrayList<>();
        toolbar = findViewById(R.id.display_chats_toolbar);
        setToolbar();
        this.sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pb = findViewById(R.id.pb_display_chats_matches);

        this.initRecyclerView();


    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    public void initRecyclerView(){
        CardDataHelper carddataHelper = new CardDataHelper();

        carddataHelper.loadProfiles(this, profiles);

    }

    public void finalizeReyclerView() {
        this.rvMatches = findViewById(R.id.rv_matches);

        GridLayoutManager manager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);

        this.rvMatches.setLayoutManager(manager);
        this.matchAdapter = new MatchAdapter(this.profiles);
        this.rvMatches.setAdapter(this.matchAdapter);

        pb.setVisibility(View.GONE);
    }

    private void setToolbar(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayChatsActivity.this,SwipeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadDayNight(){
        Boolean darkBool = this.sp.getBoolean(Keys.KEY_DARK_BOOL.name(), false);
        if(darkBool) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        this.loadDayNight();
    }

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }

    @Override
    protected void onResume(){
        super.onResume();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        this.loadDayNight();
    }


}
