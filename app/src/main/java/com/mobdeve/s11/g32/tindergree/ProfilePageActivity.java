package com.mobdeve.s11.g32.tindergree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilePageActivity extends AppCompatActivity {

    private ImageView iv_profprofpic;
    private TextView tv_profname;
    private TextView tv_profdesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        this.iv_profprofpic = findViewById(R.id.iv_profprofpic);
        this.tv_profname = findViewById(R.id.tv_profname);
        this.tv_profdesc = findViewById(R.id.tv_profdesc);

        Intent i = getIntent();
        int profprofpic = i.getIntExtra(ChatActivity.KEY_KACHATPIC,0);
        this.iv_profprofpic.setImageResource(profprofpic);
        String profname = i.getStringExtra(ChatActivity.KEY_KACHATNAME);
        this.tv_profname.setText(profname);
        String profdesc = i.getStringExtra(ChatActivity.KEY_KACHATDESC);
        this.tv_profdesc.setText(profdesc);
    }


    //TODO:find a way to recieve arraylist of otherpics
    //TODO:inflate rv_otherpics
    /*
    public void initRecyclerView(){

    }*/
}