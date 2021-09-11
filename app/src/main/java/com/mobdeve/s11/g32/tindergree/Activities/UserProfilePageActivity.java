package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import id.zelory.compressor.Compressor;

public class UserProfilePageActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etAboutYourPet,etBreed,etName;
    private TextView tvBirthday;
    private ImageButton ibProfile1,ibProfile2,ibProfile3,
                        ibProfile4,ibProfile5,ibProfile6;
    ConstraintLayout clDogOption,clCatoption,clBirthdayContainer;
    private boolean ivDogIsChecked,ivCatIsChecked;
    private ImageView ivDogCheck,ivCatCheck;
    private Button btnUpdate;
    private DatePickerDialog datePickerDialog;

    private ArrayList<File> userPhotosToUpload;

    private static final int CAMERA_PICK_CODE = 0;
    private static final int IMAGE_PICK_CODE = 1;
    private static final int PERMISSION_CODE_GALLERY = 1000;
    private static final int PERMISSION_CODE_CAMERA = 1001;

    private int currentButtonClicked; // tracks which add image button is tapped

    private boolean ibProfile1HasImage,ibProfile2HasImage,
            ibProfile3HasImage,ibProfile4HasImage,
            ibProfile5HasImage,ibProfile6HasImage;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);
        changeStatusBarColor();

        askPermissions();
        initData();
        setImageOnClickListeners();

        userPhotosToUpload = new ArrayList<>();
        for (int i = 0; i < 6; i++) userPhotosToUpload.add(null);
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

        ibProfile1HasImage= false;
        ibProfile2HasImage = false;
        ibProfile3HasImage = false;
        ibProfile4HasImage = false;
        ibProfile5HasImage = false;
        ibProfile6HasImage = false;

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
                if (checkEmptyFields()) // Empty fields, stop.
                    return;

            }
        });

        clBirthdayContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });

        toolbar = findViewById(R.id.user_profile_page_toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setImageOnClickListeners(){
        ibProfile1.setOnClickListener(this);
        ibProfile2.setOnClickListener(this);
        ibProfile3.setOnClickListener(this);
        ibProfile4.setOnClickListener(this);
        ibProfile5.setOnClickListener(this);
        ibProfile6.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
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

    private void updateProfile() {
        // TODO
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.ib_user_profile_photo1:
                this.currentButtonClicked = 1;
                chooseImage(UserProfilePageActivity.this);
                break;
            case R.id.ib_user_profile_photo2:
                this.currentButtonClicked = 2;
                chooseImage(UserProfilePageActivity.this);
                break;
            case R.id.ib_user_profile_photo3:
                this.currentButtonClicked = 3;
                chooseImage(UserProfilePageActivity.this);
                break;
            case R.id.ib_user_profile_photo4:
                this.currentButtonClicked = 4;
                chooseImage(UserProfilePageActivity.this);
                break;
            case R.id.ib_user_profile_photo5:
                this.currentButtonClicked = 5;
                chooseImage(UserProfilePageActivity.this);
                break;
            case R.id.ib_user_profile_photo6:
                this.currentButtonClicked = 6;
                chooseImage(UserProfilePageActivity.this);
                break;
            case R.id.btn_user_profile_update:
//                updateInformation(); //TODO Implement this
                break;
        }

    }

    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {"Take photo with camera", "Choose from gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take photo with camera")){
                    // Open the camera and get the photo
                    if(Build.VERSION.SDK_INT>=24){
                        try{
                            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            m.invoke(null);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, CAMERA_PICK_CODE);
                }
                else if(optionsMenu[i].equals("Choose from gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , IMAGE_PICK_CODE);
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void setImage(ImageButton ibPostRegister, int requestCode, int resultCode, Uri compressedImageUri, int imageButtonNumber) {

        ibPostRegister.setImageURI(compressedImageUri);

        ibPostRegister.setAdjustViewBounds(true);
        ibPostRegister.setScaleType(ImageView.ScaleType.FIT_XY);
        ibPostRegister.setPadding(15,15,15,15);

        switch (imageButtonNumber)
        {
            case 1:
                ibProfile1HasImage = true;
                break;
            case 2:
                ibProfile2HasImage = true;
                break;
            case 3:
                ibProfile3HasImage = true;
                break;
            case 4:
                ibProfile4HasImage = true;
                break;
            case 5:
                ibProfile5HasImage = true;
                break;
            case 6:
                ibProfile6HasImage = true;
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_PICK_CODE) {

            Bitmap image = (Bitmap) data.getExtras().get("data");

            Uri uriLocal = getImageUri(UserProfilePageActivity.this,image);

            File originalImageFile = new File(getRealPathFromURI(uriLocal));
            File compressedImageFile = null;

            // Compress the image
            try {
                compressedImageFile = new Compressor(UserProfilePageActivity.this).compressToFile(originalImageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            switch(this.currentButtonClicked) {
                case 1:
                    this.userPhotosToUpload.set(0, compressedImageFile);
                    setImage(ibProfile1, requestCode, resultCode, Uri.fromFile(compressedImageFile), 1);
                    break;
                case 2:
                    this.userPhotosToUpload.set(1, compressedImageFile);
                    setImage(ibProfile2, requestCode, resultCode, Uri.fromFile(compressedImageFile), 2);
                    break;
                case 3:
                    this.userPhotosToUpload.set(2, compressedImageFile);
                    setImage(ibProfile3, requestCode, resultCode, Uri.fromFile(compressedImageFile), 3);
                    break;
                case 4:
                    this.userPhotosToUpload.set(3, compressedImageFile);
                    setImage(ibProfile4, requestCode, resultCode, Uri.fromFile(compressedImageFile), 4);
                    break;
                case 5:
                    this.userPhotosToUpload.set(4, compressedImageFile);
                    setImage(ibProfile5, requestCode, resultCode, Uri.fromFile(compressedImageFile), 5);
                    break;
                case 6:
                    this.userPhotosToUpload.set(5, compressedImageFile);
                    setImage(ibProfile6, requestCode, resultCode, Uri.fromFile(compressedImageFile), 6);
                    break;
            }

        } else if (requestCode == IMAGE_PICK_CODE) {

            Uri selectedImageUri = data.getData();
            File originalImageFile2 = new File(getRealPathFromURI(selectedImageUri));
            File compressedImageFile2 = null;

            // Compress the image
            try {
                compressedImageFile2 = new Compressor(UserProfilePageActivity.this).compressToFile(originalImageFile2);
            } catch (IOException e) {
                e.printStackTrace();
            }

            switch (this.currentButtonClicked) {
                case 1:
                    this.userPhotosToUpload.set(0, compressedImageFile2);
                    setImage(ibProfile1, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 1);
                    break;
                case 2:
                    this.userPhotosToUpload.set(1, compressedImageFile2);
                    setImage(ibProfile2, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 2);
                    break;
                case 3:
                    this.userPhotosToUpload.set(2, compressedImageFile2);
                    setImage(ibProfile3, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 3);
                    break;
                case 4:
                    this.userPhotosToUpload.set(3, compressedImageFile2);
                    setImage(ibProfile4, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 4);
                    break;
                case 5:
                    this.userPhotosToUpload.set(4, compressedImageFile2);
                    setImage(ibProfile5, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 5);
                    break;
                case 6:
                    this.userPhotosToUpload.set(5, compressedImageFile2);
                    setImage(ibProfile6, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 6);
                    break;
            }



        }




    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public Uri getImageUri(Context ctx, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ctx.getContentResolver(),
                bitmap, "Temp", null);
        return Uri.parse(path);
    }

    private void askPermissions(){
        if(ContextCompat.checkSelfPermission(UserProfilePageActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserProfilePageActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, PERMISSION_CODE_CAMERA);

        }
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            //Permission not granted, request

            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            //show popup for runtime permission
            requestPermissions(permissions, PERMISSION_CODE_GALLERY);
        }
    }
}