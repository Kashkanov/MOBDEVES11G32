package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s11.g32.tindergree.R;

public class PostRegisterUserDetails extends AppCompatActivity {

    private ConstraintLayout clDogOption,clCatOption;
    private ImageView ivDogCheck,ivCatCheck;
    private TextView tvDogOption,tvCatOption;
    private boolean ivDogIsChecked,ivCatIsChecked;
    private Button btnPostRegisterDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register_user_details);
        initData();


    }

    private void initData(){
        clDogOption = findViewById(R.id.cl_post_register_user_details_dog);
        clCatOption = findViewById(R.id.cl_post_register_user_details_cat);
        ivDogCheck = findViewById(R.id.iv_post_register_user_details_check_dog);
        ivCatCheck = findViewById(R.id.iv_post_register_user_details_check_cat);
        tvDogOption = findViewById(R.id.tv_post_register_user_details_dog);
        tvCatOption = findViewById(R.id.tv_post_register_user_details_cat);
        btnPostRegisterDetails = findViewById(R.id.btn_post_register_user_details_continue);

        ivDogIsChecked = false;
        ivCatIsChecked = false;

        setOnClickListeners();
    }

    private void setOnClickListeners(){
        clDogOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ivDogIsChecked == false){
                    tvDogOption.setTextColor(Color.parseColor("#FF914D"));
                    ivDogCheck.setVisibility(v.VISIBLE);
                    ivDogIsChecked = true;

                    tvCatOption.setTextColor(Color.parseColor("#FF000000"));
                    ivCatCheck.setVisibility(v.GONE);
                    ivCatIsChecked = false;
                }
            }
        });

        clCatOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCatOption.setTextColor(Color.parseColor("#FF914D"));
                if(ivCatIsChecked == false){

                    ivCatCheck.setVisibility(v.VISIBLE);
                    ivCatIsChecked = true;

                    tvDogOption.setTextColor(Color.parseColor("#FF000000"));
                    ivDogCheck.setVisibility(v.GONE);
                    ivDogIsChecked = false;
                }
            }
        });

        btnPostRegisterDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostRegisterUserDetails.this,SwipeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
    @Override public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}