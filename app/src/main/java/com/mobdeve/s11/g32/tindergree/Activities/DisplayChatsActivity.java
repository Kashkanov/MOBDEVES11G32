package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.s11.g32.tindergree.DataHelpers.CardDataHelper;
import com.mobdeve.s11.g32.tindergree.Adapters.MatchAdapter;
import com.mobdeve.s11.g32.tindergree.Models.Profile;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class DisplayChatsActivity extends AppCompatActivity {

    private RecyclerView rv_matches;
    private MatchAdapter matchAdapter;
    private ArrayList<Profile> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent(); // This returns the intent that started this activity.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chats);
        this.initRecyclerView();
    }

    public void initRecyclerView(){
        CardDataHelper carddataHelper = new CardDataHelper();

        this.profiles = carddataHelper.loadProfileData();
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
