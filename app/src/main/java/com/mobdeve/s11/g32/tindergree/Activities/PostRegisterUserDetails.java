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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s11.g32.tindergree.R;

public class PostRegisterUserDetails extends AppCompatActivity {

    private ConstraintLayout clDogOption,clCatOption;
    private ImageView ivDogCheck,ivCatCheck;
    private TextView tvDogOption,tvCatOption,tvErrorMessage;
    private boolean ivDogIsChecked,ivCatIsChecked;
    private Button btnPostRegisterDetails;
    private EditText etBirthday,etBreed,etName;

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
        tvErrorMessage = findViewById(R.id.tv_post_register_user_details_pet_error_message);
        btnPostRegisterDetails = findViewById(R.id.btn_post_register_user_details_continue);
        etBirthday = findViewById(R.id.et_post_register_user_details_birthday);
        etBreed = findViewById(R.id.et_post_register_user_details_breed);
        etName = findViewById(R.id.et_post_register_user_details_name);


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
                    tvErrorMessage.setVisibility(View.GONE);

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
                    tvErrorMessage.setVisibility(View.GONE);

                    tvDogOption.setTextColor(Color.parseColor("#FF000000"));
                    ivDogCheck.setVisibility(v.GONE);
                    ivDogIsChecked = false;
                }
            }
        });

        btnPostRegisterDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkEmptyFields())
                    return;

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

    private boolean checkEmptyFields(){
        boolean hasEmpty = false;

        String birthday = etBirthday.getText().toString();
        String breed = etBreed.getText().toString();
        String name = etName.getText().toString();

        if((ivDogIsChecked || ivCatIsChecked) == false){
            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.requestFocus();
            hasEmpty = true;
        }
        else if(birthday.isEmpty()){
            etBirthday.setError("Please enter your pets birthday!");
            etBirthday.requestFocus();
            hasEmpty = true;
        }
        else if(breed.isEmpty()){
            etBreed.setError("Please enter your pets breed!");
            etBreed.requestFocus();
            hasEmpty = true;
        }
        else if(name.isEmpty()){
            etName.setError("Please enter your pets name!");
            etName.requestFocus();
            hasEmpty = true;
        }

        return hasEmpty;
    }
}