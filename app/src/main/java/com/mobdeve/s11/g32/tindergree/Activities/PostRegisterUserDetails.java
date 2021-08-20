package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.HashMap;
import java.util.Map;

public class PostRegisterUserDetails extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private ConstraintLayout clDogOption,clCatOption;
    private ImageView ivDogCheck,ivCatCheck;
    private TextView tvDogOption,tvCatOption;
    private boolean ivDogIsChecked,ivCatIsChecked;
    private Button btnPostRegisterDetails;
    private EditText etBirthday, etBreed, etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register_user_details);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        firestore.setFirestoreSettings(settings);

        initData();


    }

    private void initData(){
        clDogOption = findViewById(R.id.cl_post_register_user_details_dog);
        clCatOption = findViewById(R.id.cl_post_register_user_details_cat);
        ivDogCheck = findViewById(R.id.iv_post_register_user_details_check_dog);
        ivCatCheck = findViewById(R.id.iv_post_register_user_details_check_cat);
        tvDogOption = findViewById(R.id.tv_post_register_user_details_dog);
        tvCatOption = findViewById(R.id.tv_post_register_user_details_cat);
        etBirthday = findViewById(R.id.et_post_register_user_details_birthday);
        etBreed = findViewById(R.id.et_post_register_user_details_breed);
        etName = findViewById(R.id.et_post_register_user_details_name);
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
                // TODO: Call the Firestore save data function here
                saveDataToFirestore();
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
        userData.put("birthday", etBirthday.getText().toString());
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
}