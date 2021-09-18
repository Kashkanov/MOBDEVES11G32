package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobdeve.s11.g32.tindergree.DataHelpers.Keys;
import com.mobdeve.s11.g32.tindergree.R;

public class FilterActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private ConstraintLayout clDogFilter;
    private ConstraintLayout clCatFilter;
    private ConstraintLayout clNoFilter;
    private boolean ivDogIsChecked,ivCatIsChecked,ivNoneIsChecked;
    private ImageView ivDogCheck,ivCatCheck,ivNoCheck;
    private TextView tvDogOption,tvCatOption,tvNoOption;
    private String currentCheck;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initData();
    }

    private void initData(){

        clDogFilter = findViewById(R.id.cl_filter_dog_container);
        clCatFilter = findViewById(R.id.cl_filter_cat_container);
        clNoFilter = findViewById(R.id.cl_filter_none_container);

        ivDogCheck = findViewById(R.id.iv_filter_check_dog);
        ivCatCheck = findViewById(R.id.iv_filter_check_cat);
        ivNoCheck= findViewById(R.id.iv_filter_check_both);

        tvDogOption = findViewById(R.id.tv_filter_dog);
        tvCatOption = findViewById(R.id.tv_filter_cat);
        tvNoOption = findViewById(R.id.tv_filter_none);

        toolbar = findViewById(R.id.filter_toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        this.sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.spEditor = this.sp.edit();
        this.loadData();

        clDogFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivDogIsChecked == false){
                    dogCheck();
                }
            }
        });

        clCatFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivCatIsChecked == false){
                   catCheck();
                }
            }
        });

        clNoFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivNoneIsChecked == false) {
                    noCheck();
                }

            }
        });



    }

    private void loadData(){

        String filterChecked = this.sp.getString(Keys.KEY_FILTER.name(),"none");
        Log.w("VALUE OF FILTERCHECKED",filterChecked);

        if(filterChecked.trim().equals("dog")){
            dogCheck();
        }
        else if(filterChecked.trim().equals("cat")){
            catCheck();
        }
        else if(filterChecked.trim().equals("none")){
            noCheck();
        }

    }

    private void saveData(){
        //put string value here based on what is checked
        this.spEditor.putString(Keys.KEY_FILTER.name(),currentCheck);
        this.spEditor.apply();
    }

    private void dogCheck(){
        tvDogOption.setTextColor(Color.parseColor("#FF914D"));
        ivDogCheck.setVisibility(View.VISIBLE);
        ivDogIsChecked = true;

        tvCatOption.setTextColor(Color.parseColor("#FF000000"));
        ivCatCheck.setVisibility(View.GONE);
        ivCatIsChecked = false;

        tvNoOption.setTextColor(Color.parseColor("#FF000000"));
        ivNoCheck.setVisibility(View.GONE);
        ivNoneIsChecked = false;

        currentCheck = "dog";
    }

    private void catCheck(){
        tvCatOption.setTextColor(Color.parseColor("#FF914D"));
        ivCatCheck.setVisibility(View.VISIBLE);
        ivCatIsChecked = true;

        tvDogOption.setTextColor(Color.parseColor("#FF000000"));
        ivDogCheck.setVisibility(View.GONE);
        ivDogIsChecked = false;

        tvNoOption.setTextColor(Color.parseColor("#FF000000"));
        ivNoCheck.setVisibility(View.GONE);
        ivNoneIsChecked = false;

        currentCheck = "cat";

    }

    private void noCheck(){
        tvNoOption.setTextColor(Color.parseColor("#FF914D"));
        ivNoCheck.setVisibility(View.VISIBLE);
        ivNoneIsChecked = true;

        tvDogOption.setTextColor(Color.parseColor("#FF000000"));
        ivDogCheck.setVisibility(View.GONE);
        ivDogIsChecked = false;

        tvCatOption.setTextColor(Color.parseColor("#FF000000"));
        ivCatCheck.setVisibility(View.GONE);
        ivCatIsChecked = false;

        currentCheck = "none";
    }

    @Override
    protected void onPause(){
        super.onPause();
        this.saveData();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadData();
    }



}