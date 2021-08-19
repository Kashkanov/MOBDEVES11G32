package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobdeve.s11.g32.tindergree.Adapters.MatchAdapter;
import com.mobdeve.s11.g32.tindergree.Models.OtherPic;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ImageButton ib_kachatpic;
    private TextView tv_kachatname;
    private TextView tv_kachatdesc;
    private ArrayList<OtherPic> otherpics;
    public static final String KEY_KACHATNAME = "KEY_KACHATNAME";
    public static final String KEY_KACHATPIC = "KEY_KACHATPIC";
    public static final String KEY_KACHATDESC = "KEY_KACHATDESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_chat);

        this.ib_kachatpic = findViewById(R.id.ib_kachatpic);
        this.tv_kachatname = findViewById(R.id.tv_kachatname);
        this.tv_kachatdesc = findViewById(R.id.tv_kachatdesc);

        Intent i = getIntent();
        String kachatname =i.getStringExtra(MatchAdapter.KEY_MATCHNAME);
        this.tv_kachatname.setText(kachatname);
        String kachatdesc = i.getStringExtra(MatchAdapter.KEY_MATCHDESC);
        this.tv_kachatdesc.setText(kachatdesc);
        int kachatpic = i.getIntExtra(MatchAdapter.KEY_MATCHPIC,0);
        this.ib_kachatpic.setImageResource(kachatpic);
        Bundle bundleObject = getIntent().getExtras();
        this.otherpics = (ArrayList<OtherPic>) bundleObject.getSerializable("KEY_OTHERPICS");

        this.ib_kachatpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MatchesProfilePageActivity.class);
                i.putExtra(KEY_KACHATNAME,kachatname);
                i.putExtra(KEY_KACHATPIC,kachatpic);
                i.putExtra(KEY_KACHATDESC,kachatdesc);
                Bundle bundle = new Bundle();
                bundle.putSerializable("KEY_OTHERPICS",otherpics);
                i.putExtras(bundle);
                v.getContext().startActivity(i);

            }
        });
    }

    //TODO: implement chatting feature
    //TODO: pass array of otherpics
}