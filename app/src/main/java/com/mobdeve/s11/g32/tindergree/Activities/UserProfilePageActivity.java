package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s11.g32.tindergree.R;

import java.util.Calendar;

public class UserProfilePageActivity extends AppCompatActivity {

    private EditText etAboutYourPet,etBreed,etName;
    private TextView tvBirthday;
    private ImageButton ibProfile1,ibProfile2,ibProfile3,
                        ibProfile4,ibProfile5,ibProfile6;
    ConstraintLayout clDogOption,clCatoption,clBirthdayContainer;
    private boolean ivDogIsChecked,ivCatIsChecked;
    private ImageView ivDogCheck,ivCatCheck;
    private Button btnUpdate;
    private DatePickerDialog datePickerDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);
        changeStatusBarColor();

        initData();
    }

    private void initData(){

        etAboutYourPet = findViewById(R.id.et_user_profile_about_your_pet);
        tvBirthday= findViewById(R.id.tv_user_profile_page_birthday_value);
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
        clBirthdayContainer = findViewById(R.id.cl_user_profile_page_birthday);

        ivDogCheck = findViewById(R.id.iv_user_profile_check_dog);
        ivCatCheck = findViewById(R.id.iv_user_profile_check_cat);

        initDatePicker();
        tvBirthday.setText(getTodaysDate());

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

        clBirthdayContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });

    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    private boolean checkEmptyFields(){

        boolean hasEmpty = false;

        String aboutYourPet = etAboutYourPet.getText().toString();
        String birthday = tvBirthday.getText().toString();
        String breed = etBreed.getText().toString();
        String name = etName.getText().toString();

        if(aboutYourPet.length() < 8){
            etAboutYourPet.setError("Please enter at least 8 characters!");
            etAboutYourPet.requestFocus();
            hasEmpty = true;
        }
        else if(birthday.isEmpty()){
            tvBirthday.setError("Please enter your pets birthday!");
            tvBirthday.requestFocus();
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


    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day,month,year);
                tvBirthday.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this,dateSetListener,year,month,day);

    }

    public String makeDateString(int day, int month, int year){

        return getMonthFormat(month) + " " + day + " " + year;

    }

    private String getMonthFormat(int month){

        String monthReturned = "";

        switch(month)
        {
            case 1:
                monthReturned = "January";
                break;
            case 2:
                monthReturned =  "February";
                break;
            case 3:
                monthReturned =  "March";
                break;
            case 4:
                monthReturned =  "April";
                break;
            case 5:
                monthReturned =  "May";
                break;
            case 6:
                monthReturned =  "June";
                break;
            case 7:
                monthReturned = "July";
                break;
            case 8:
                monthReturned =  "August";
                break;
            case 9:
                monthReturned =  "September";
                break;
            case 10:
                monthReturned =  "October";
                break;
            case 11:
                monthReturned =  "November";
                break;
            case 12:
                monthReturned =  "December";
                break;
        }
        return monthReturned;
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }

    private String getTodaysDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month +1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day,month,year);

    }


}