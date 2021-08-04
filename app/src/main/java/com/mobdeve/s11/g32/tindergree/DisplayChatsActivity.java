package com.mobdeve.s11.g32.tindergree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DisplayChatsActivity extends AppCompatActivity {

    private RecyclerView rv_matches;
    private MatchAdapter matchAdapter;
    private ArrayList<Profile> profiles;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

}
