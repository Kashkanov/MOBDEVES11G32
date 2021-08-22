package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostRegisterUserDetails extends AppCompatActivity {

    private ConstraintLayout clDogOption,clCatOption;
    private ImageView ivDogCheck,ivCatCheck;
    private TextView tvDogOption,tvCatOption,tvErrorMessage,tvBirthday;
    private boolean ivDogIsChecked,ivCatIsChecked;
    private Button btnPostRegisterDetails;
    private EditText etBreed,etName,etAboutPet;

    private DatePickerDialog datePickerDialog;
    ConstraintLayout clBirthdayContainer;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register_user_details);

        try {
            mAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();

            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build();
            firestore.setFirestoreSettings(settings);
        }
        catch (IllegalStateException e) {
            Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
        }


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

        initDatePicker();

        tvBirthday = findViewById(R.id.tv_post_register_user_details_birthday_value);
        clBirthdayContainer = findViewById(R.id.cl_post_register_user_details_birthday);


        tvBirthday.setText(getTodaysDate());

        etBreed = findViewById(R.id.et_post_register_user_details_breed);
        etName = findViewById(R.id.et_post_register_user_details_name);
        etAboutPet = findViewById(R.id.et_post_register_user_details_about_pet);

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

                saveDataToFirestore();
            }
        });

        clBirthdayContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
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

    private void saveDataToFirestore() {
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> userData = new HashMap<>();
        if (ivDogIsChecked == true)
            userData.put("animal", "dog");
        else if (ivCatIsChecked == true)
            userData.put("animal", "cat");
        userData.put("birthday", tvBirthday.getText().toString());
        userData.put("breed", etBreed.getText().toString());
        userData.put("petName", etName.getText().toString());
        userData.put("uid", uid);

        firestore.collection("Pets").document(uid).set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(SwipeActivity.firebaseLogKey, "DocumentSnapshot successfully written!");

                        Intent i = new Intent(PostRegisterUserDetails.this,SwipeActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(SwipeActivity.firebaseLogKey, "Error writing document", e);
                    }
                });
    }

    private boolean checkEmptyFields(){
        boolean hasEmpty = false;

        String birthday = tvBirthday.getText().toString();
        String breed = etBreed.getText().toString();
        String name = etName.getText().toString();
        String aboutPet = etAboutPet.getText().toString();

        if((ivDogIsChecked || ivCatIsChecked) == false){
            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.requestFocus();
            hasEmpty = true;
        }
        else if(birthday.isEmpty()){
            tvBirthday.setError("Please enter your pets birthday!");
            tvBirthday.requestFocus();
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
        else if(aboutPet.length() < 8){
            etAboutPet.setError("Please enter at least 8 characters!");
            etAboutPet.requestFocus();
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