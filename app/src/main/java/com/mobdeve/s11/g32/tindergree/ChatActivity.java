package com.mobdeve.s11.g32.tindergree;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    private ImageView iv_kachatpic;
    private TextView tv_kachatname;
    private TextView tv_kachatdesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.iv_kachatpic = findViewById(R.id.iv_kachatpic);
        this.tv_kachatname = findViewById(R.id.tv_kachatname);
        this.tv_kachatdesc = findViewById(R.id.tv_kachatdesc);

        Intent i = getIntent();
        String kachatname =i.getStringExtra(MatchAdapter.KEY_MATCHNAME);
        this.tv_kachatname.setText(kachatname);
        String kachatdesc = i.getStringExtra(MatchAdapter.KEY_MATCHDESC);
        this.tv_kachatdesc.setText(kachatdesc);
        int kachatpic = i.getIntExtra(MatchAdapter.KEY_MATCHPIC,0);
        this.iv_kachatpic.setImageResource(kachatpic);
    }

    //TODO: implement chatting feature
}