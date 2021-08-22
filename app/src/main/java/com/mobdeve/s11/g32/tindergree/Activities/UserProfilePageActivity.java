package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mobdeve.s11.g32.tindergree.R;

public class UserProfilePageActivity extends AppCompatActivity {

    private EditText etAboutYourPet,etBirthday,etBreed,etName;
    private ImageButton ibProfile1,ibProfile2,ibProfile3,
                        ibProfile4,ibProfile5,ibProfile6;
    ConstraintLayout clDogOption,clCatoption;
    private boolean ivDogIsChecked,ivCatIsChecked;
    private ImageView ivDogCheck,ivCatCheck;
    private Button btnUpdate;

    //TODO For the date, there should be a date picker instead of the user inputting text.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        initData();
    }

    private void initData(){

        etAboutYourPet = findViewById(R.id.et_user_profile_about_your_pet);
        etBirthday = findViewById(R.id.et_user_profile_birthday);
        etBreed = findViewById(R.id.et_user_profile_breed);
        etName = findViewById(R.id.et_user_profile_name);

        ibProfile1 = findViewById(R.id.ib_user_profile_photo1);
        ibProfile2 = findViewById(R.id.ib_user_profile_photo2);
        ibProfile3 = findViewById(R.id.ib_user_profile_photo3);
        ibProfile4 = findViewById(R.id.ib_user_profile_photo4);
        ibProfile5 = findViewById(R.id.ib_user_profile_photo5);
        ibProfile6 = findViewById(R.id.ib_user_profile_photo6);

        clDogOption = findViewById(R.id.cl_user_profile_dog);
        clCatoption = findViewById(R.id.cl_user_profile_cat);

        ivDogCheck = findViewById(R.id.iv_user_profile_check_dog);
        ivCatCheck = findViewById(R.id.iv_user_profile_check_cat);

        /*
        TODO Based on whether the user is a cat or dog: set the boolean ivDogIsChecked or ivCatisChecked to true
        Then set ivDogCheck to visible/gone and same for ivCatCheck
         */

        btnUpdate = findViewById(R.id.btn_user_profile_update);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO call necessary function here and move checkEmptyFields to that function
                checkEmptyFields();
            }
        });

    }

    private boolean checkEmptyFields(){

        boolean hasEmpty = false;

        String aboutYourPet = etAboutYourPet.getText().toString();
        String birthday = etBirthday.getText().toString();
        String breed = etBreed.getText().toString();
        String name = etName.getText().toString();

        if(aboutYourPet.length() < 8){
            etAboutYourPet.setError("Please enter at least 8 characters!");
            etAboutYourPet.requestFocus();
            hasEmpty = true;
        }
        else if(birthday.isEmpty()){
            etBirthday.setError("Please enter your pets birthday!");
            etBirthday.requestFocus();
            hasEmpty= true;
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