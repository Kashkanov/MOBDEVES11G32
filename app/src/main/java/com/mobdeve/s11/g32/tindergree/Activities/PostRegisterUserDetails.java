package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s11.g32.tindergree.R;

public class PostRegisterUserDetails extends AppCompatActivity {

    private ConstraintLayout cl_dog_option,cl_cat_option;
    private ImageView iv_dog_check,iv_cat_check;
    private TextView tv_dog_option,tv_cat_option;
    private boolean iv_dog_isChecked,iv_cat_isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register_user_details);

        cl_dog_option = findViewById(R.id.cl_post_register_user_details_dog);
        cl_cat_option = findViewById(R.id.cl_post_register_user_details_cat);
        iv_dog_check = findViewById(R.id.iv_post_register_user_details_check_dog);
        iv_cat_check = findViewById(R.id.iv_post_register_user_details_check_cat);
        tv_dog_option = findViewById(R.id.tv_post_register_user_details_dog);
        tv_cat_option = findViewById(R.id.tv_post_register_user_details_cat);

        iv_dog_isChecked = false;
        iv_cat_isChecked = false;

        cl_dog_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(iv_dog_isChecked == false){
                    tv_dog_option.setTextColor(Color.parseColor("#FF914D"));
                    iv_dog_check.setVisibility(v.VISIBLE);
                    iv_dog_isChecked = true;

                    tv_cat_option.setTextColor(Color.parseColor("#FF000000"));
                    iv_cat_check.setVisibility(v.GONE);
                    iv_cat_isChecked = false;
                }
            }
        });

        cl_cat_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_cat_option.setTextColor(Color.parseColor("#FF914D"));
                if(iv_cat_isChecked == false){

                    iv_cat_check.setVisibility(v.VISIBLE);
                    iv_cat_isChecked = true;

                    tv_dog_option.setTextColor(Color.parseColor("#FF000000"));
                    iv_dog_check.setVisibility(v.GONE);
                    iv_dog_isChecked = false;
                }
            }
        });

    }


}