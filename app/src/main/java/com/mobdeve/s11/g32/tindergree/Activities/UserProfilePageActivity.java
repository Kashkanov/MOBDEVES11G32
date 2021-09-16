package com.mobdeve.s11.g32.tindergree.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatDelegate;
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
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.Toast;

import com.mobdeve.s11.g32.tindergree.DataHelpers.Keys;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s11.g32.tindergree.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class UserProfilePageActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    private EditText etAboutYourPet,etBreed,etName;
    private TextView tvBirthday;
    private ImageButton ibProfile1,ibProfile2,ibProfile3,
                        ibProfile4,ibProfile5,ibProfile6;
    ConstraintLayout clDogOption,clCatOption,clBirthdayContainer;
    private TextView tvDogOption,tvCatOption;
    private boolean ivDogIsChecked,ivCatIsChecked;
    private ImageView ivDogCheck,ivCatCheck;
    private Button btnUpdate;
    private DatePickerDialog datePickerDialog;

    private ArrayList<File> userPhotosToUpload;

    private static final int CAMERA_PICK_CODE = 0;
    private static final int IMAGE_PICK_CODE = 1;
    private static final int PERMISSION_CODE_GALLERY = 1000;
    private static final int PERMISSION_CODE_CAMERA = 1001;

    private SharedPreferences sp;

    private int currentButtonClicked; // tracks which add image button is tapped

    private boolean ibProfile1HasImage,ibProfile2HasImage,
            ibProfile3HasImage,ibProfile4HasImage,
            ibProfile5HasImage,ibProfile6HasImage;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_TindergreeDark);
        }
        else{
            setTheme(R.style.Theme_Tindergree);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);
        changeStatusBarColor();

        this.sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        loadDayNight();

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Comment these lines if production Firebase should be used instead of emulator
        if (SwipeActivity.useEmulator) {
            try {
                FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
                FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
                firestore.useEmulator("10.0.2.2", 8080);

                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(false)
                        .build();
                firestore.setFirestoreSettings(settings);
            } catch (IllegalStateException e) {
                Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
            }
        }

        askPermissions();
        initData();
        setImageOnClickListeners();

        userPhotosToUpload = new ArrayList<>();
        for (int i = 0; i < 6; i++) userPhotosToUpload.add(null);
    }

    private void loadDayNight(){
        Boolean darkBool = this.sp.getBoolean(Keys.KEY_DARK_BOOL.name(), false);
        if(darkBool) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
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
        clCatOption = findViewById(R.id.cl_user_profile_cat);
        tvDogOption = findViewById(R.id.tv_user_profile_dog);
        tvCatOption = findViewById(R.id.tv_user_profile_cat);
        clBirthdayContainer = findViewById(R.id.cl_user_profile_page_birthday);

        ivDogCheck = findViewById(R.id.iv_user_profile_check_dog);
        ivCatCheck = findViewById(R.id.iv_user_profile_check_cat);

        ibProfile1HasImage= false;
        ibProfile2HasImage = false;
        ibProfile3HasImage = false;
        ibProfile4HasImage = false;
        ibProfile5HasImage = false;
        ibProfile6HasImage = false;

        ivDogIsChecked = false;
        ivCatIsChecked = false;

        initDatePicker();
        tvBirthday.setText(getTodaysDate());

        clDogOption.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if (ivDogIsChecked == false) {
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

        btnUpdate = findViewById(R.id.btn_user_profile_update);

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
        if(birthday.isEmpty()){
            tvBirthday.setError("Please enter your pets birthday!");
            tvBirthday.requestFocus();
            hasEmpty= true;
        }
        if(breed.isEmpty()){
            etBreed.setError("Please enter your pets breed!");
            etBreed.requestFocus();
            hasEmpty = true;
        }
        if(name.isEmpty()){
            etName.setError("Please enter your pets name!");
            etName.requestFocus();
            hasEmpty = true;
        }
        if (ivCatIsChecked == false && ivDogIsChecked == false) {
            Toast.makeText(UserProfilePageActivity.this, "Please select an animal type.",Toast.LENGTH_SHORT).show();
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
        Log.d(SwipeActivity.firebaseLogKey, "START update profile.");
        uploadImage();
    }

    private void resetPreviousProfilePicture(String newProfileImagefilename) {
        String uid = mAuth.getUid();

        firestore.collection("filenames")
                .whereEqualTo("uid", uid)
                .whereEqualTo("isProfilePicture", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // Get filename of previous profile picture
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentKey = document.getId();
                                String filename = document.getData().get("filename").toString();
                                Log.d(SwipeActivity.firebaseLogKey, document.getId() + " => " + documentKey);

                                if (filename.compareTo(newProfileImagefilename) == 0) {
                                    Log.d(SwipeActivity.firebaseLogKey, "New profile picture, SKIP reset.");
                                    continue;
                                }

                                // Use that filename to reference the document and change its status
                                DocumentReference prevProfilePicRef = firestore.collection("filenames").document(documentKey);

                                prevProfilePicRef
                                        .update("isProfilePicture", false)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(SwipeActivity.firebaseLogKey, "Updated previous profile picture status! (no longer a profile picture)");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(SwipeActivity.firebaseLogKey, "Error updating previous profile picture status.", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
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
        userData.put("petDescription", etAboutYourPet.getText().toString());
        userData.put("uid", uid);

        Log.d(SwipeActivity.firebaseLogKey, "Saving profile information to Firestore...");

        firestore.collection("Pets").document(uid).set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(SwipeActivity.firebaseLogKey, "Updated profile information on Firestore!");
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

    private void uploadImage() {
        // TODO: Enable loading circle view and disable the next button.

        Log.d(SwipeActivity.firebaseLogKey, "Now attempting to upload the images...");

        FirebaseUser user = mAuth.getCurrentUser();
        StorageReference storageRef = storage.getReference(); // Points to the root reference

        String uid = user.getUid();

        // Remove nulls
        this.userPhotosToUpload.removeAll(Collections.singleton(null));

        // Track the number of images yet to upload
        final int[] numImagesToUpload = {this.userPhotosToUpload.size()};

        Log.d(SwipeActivity.firebaseLogKey, "Total images to upload: " + String.valueOf(this.userPhotosToUpload.size()));

        // Upload each image
        for (int i = 0; i < this.userPhotosToUpload.size(); i++) {

            // No image at that slot
            if (this.userPhotosToUpload.get(i) == null) {
                numImagesToUpload[0]--;
                continue;
            }

            if (this.userPhotosToUpload.get(i) == null && i == this.userPhotosToUpload.size()) {
                Log.d(SwipeActivity.firebaseLogKey, "No images to upload");
                saveDataToFirestore();
                return;
            }

            // Get the filename of each images and track them
            String filename = Uri.fromFile(this.userPhotosToUpload.get(i)).getLastPathSegment();

            // Prepare document
            Map<String, Object> filenameDocument = new HashMap<>();
            filenameDocument.put("filename", filename);
            filenameDocument.put("uid", uid);
            filenameDocument.put("uriTemp", Uri.fromFile(this.userPhotosToUpload.get(i)).toString());
            // Save information about the profile picture set at the upper left.
            if (i == 0 && ibProfile1HasImage == true)
                filenameDocument.put("isProfilePicture", true);
            else
                filenameDocument.put("isProfilePicture", false);

            if (ibProfile1HasImage && i == 0) // Reset previous profile image file status
                resetPreviousProfilePicture(filename);

            // URL to the profile picture
            final Uri[] profilePictureUrl = new Uri[1];

            // Add document to Firestore
            firestore.collection("filenames")
                    .add(filenameDocument)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(SwipeActivity.firebaseLogKey, "DocumentSnapshot added with ID: " + documentReference.getId());

                            // Get the file name on the saved document
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(SwipeActivity.firebaseLogKey, "DocumentSnapshot data: " + document.getData());
                                            String uploadedFilename = document.getString("filename");
                                            String uriStringTemp = document.getString("uriTemp");

                                            // Proceed to image upload
                                            // Create reference to a path in Cloud Storage (Users/UID/<filename>)
                                            StorageReference imageUploadReference = storageRef.child("Users/" + uid + "/" + uploadedFilename);
                                            Uri uploadImageUri = Uri.parse(uriStringTemp);

                                            UploadTask uploadTask = imageUploadReference.putFile(uploadImageUri);

                                            // Save the URL of the profile picture
                                            if (document.getBoolean("isProfilePicture")) {
                                                Log.d(SwipeActivity.firebaseLogKey, "Profile picture upload detected.");
                                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                        if (!task.isSuccessful()) {
                                                            throw task.getException();
                                                        }
                                                        // Continue with the task to get the download URL
                                                        return imageUploadReference.getDownloadUrl();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if (task.isSuccessful()) {
                                                            profilePictureUrl[0] = task.getResult();
                                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                    .setPhotoUri(Uri.parse(profilePictureUrl[0].toString()))
                                                                    .build();

                                                            // Update account with new profile picture
                                                            user.updateProfile(profileUpdates)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                                Log.d(SwipeActivity.firebaseLogKey, "User profile updated with new profile picture. " +
                                                                                        "\nLink: " + Uri.parse(profilePictureUrl[0].toString()));
                                                                        }
                                                                    });
                                                        } else {
                                                            // Handle failures
                                                        }
                                                    }
                                                });
                                            }

                                            // Register observers to listen for when the upload is done or if it fails
                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    exception.printStackTrace();
                                                    // TODO: Handle failure of image upload task. Re-enable the next button.
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Log.d(SwipeActivity.firebaseLogKey, "Image uploaded.");
                                                    numImagesToUpload[0]--;

                                                    // Proceed to next Activity if all images have been uploaded
                                                    if (numImagesToUpload[0] == 0) {
                                                        saveDataToFirestore(); // Update user information
                                                        Log.d(SwipeActivity.firebaseLogKey, "All images uploaded!");
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d(SwipeActivity.firebaseLogKey, "No such document");
                                        }
                                    } else {
                                        Log.d(SwipeActivity.firebaseLogKey, "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(SwipeActivity.firebaseLogKey, "Error adding document", e);
                        }
                    });
        }
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
                if (checkEmptyFields()) // Empty fields, stop.
                    return;
                updateProfile(); //TODO Implement this
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