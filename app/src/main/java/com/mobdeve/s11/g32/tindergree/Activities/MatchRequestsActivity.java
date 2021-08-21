package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.mobdeve.s11.g32.tindergree.Adapters.MatchAdapter;
import com.mobdeve.s11.g32.tindergree.Adapters.MatchRequestAdapter;
import com.mobdeve.s11.g32.tindergree.DataHelpers.MatchRequestHelper;
import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class MatchRequestsActivity extends AppCompatActivity {

    private RecyclerView rv_matchrequestarea;
    private ArrayList<MatchRequest> mr;
    private MatchRequestAdapter matchRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_requests);
        this.mr = new ArrayList<MatchRequest>();
        this.initrecyclerview();
    }

    public void initrecyclerview(){
        this.rv_matchrequestarea = findViewById(R.id.rv_matchrequestarea);
        MatchRequestHelper matchRequestHelper = new MatchRequestHelper();
        this.mr = matchRequestHelper.loadMatchRequestData();

        LinearLayoutManager manager = new LinearLayoutManager(this,GridLayoutManager.VERTICAL,false);

        this.rv_matchrequestarea.setLayoutManager(manager);
        this.matchRequestAdapter = new MatchRequestAdapter(this.mr);
        this.rv_matchrequestarea.setAdapter(this.matchRequestAdapter);
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