package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s11.g32.tindergree.Adapters.MatchRequestAdapter;
import com.mobdeve.s11.g32.tindergree.Adapters.OtherPicsAdapter;
import com.mobdeve.s11.g32.tindergree.Models.OtherPic;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class ProfilePage2Activity extends AppCompatActivity {

    private ImageView iv_profprofpic;
    private TextView tv_profname;
    private TextView tv_profdesc;
    private ArrayList<OtherPic> otherpics;
    private RecyclerView rv_otherpics;
    private OtherPicsAdapter otherPicsAdapter;

    // This is mostly similar to OtherPicsActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        changeStatusBarColor();

        this.iv_profprofpic = findViewById(R.id.iv_profprofpic);
        this.tv_profname = findViewById(R.id.tv_profname);
        this.tv_profdesc = findViewById(R.id.tv_profdesc);

        Intent i = getIntent();
        int profprofpic = i.getIntExtra(MatchRequestAdapter.KEY_MATCHREQPIC,0);
        this.iv_profprofpic.setImageResource(profprofpic);
        String profname = i.getStringExtra(MatchRequestAdapter.KEY_MATCHREQNAME);
        this.tv_profname.setText(profname);
        String profdesc = i.getStringExtra(MatchRequestAdapter.KEY_MATCHREQDESC);
        this.tv_profdesc.setText(profdesc);

        initRecyclerView();
    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }



    public void initRecyclerView(){
        this.rv_otherpics = findViewById(R.id.rv_profotherpics);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);

        this.rv_otherpics.setLayoutManager(manager);
        this.otherPicsAdapter = new OtherPicsAdapter(this.otherpics);
        this.rv_otherpics.setAdapter(this.otherPicsAdapter);
    }
}