package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.s11.g32.tindergree.DataHelpers.CardDataHelper;
import com.mobdeve.s11.g32.tindergree.Adapters.MatchAdapter;
import com.mobdeve.s11.g32.tindergree.Models.CardProfile;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class DisplayChatsActivity extends AppCompatActivity {

    private RecyclerView rv_matches;
    private MatchAdapter matchAdapter;
    private ArrayList<CardProfile> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        profiles = new ArrayList<>();

        changeStatusBarColor();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chats);
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
        this.rv_matches = findViewById(R.id.rv_matches);

        GridLayoutManager manager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);

        this.rv_matches.setLayoutManager(manager);
        this.matchAdapter = new MatchAdapter(this.profiles);
        this.rv_matches.setAdapter(this.matchAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

}
