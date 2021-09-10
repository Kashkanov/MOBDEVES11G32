package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s11.g32.tindergree.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.xml.parsers.SAXParser;

import id.zelory.compressor.Compressor;

public class PostRegisterUploadPhotos extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    private ArrayList<File> userPhotosToUpload;

    private ImageButton ibPostRegister1,ibPostRegister2,ibPostRegister3,
                        ibPostRegister4,ibPostRegister5,ibPostRegister6;

    private ImageButton btnPostRegister;

    private ProgressBar pb;

    private int currentButtonClicked; // tracks which add image button is tapped

    private static final int CAMERA_PICK_CODE = 0;
    private static final int IMAGE_PICK_CODE = 1;
    private static final int PERMISSION_CODE_GALLERY = 1000;
    private static final int PERMISSION_CODE_CAMERA = 1001;
    private Uri mUri;

    private boolean ibPostRegister1HasImage,ibPostRegister2HasImage,
                    ibPostRegister3HasImage,ibPostRegister4HasImage,
                    ibPostRegister5HasImage,ibPostRegister6HasImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register_upload_photos);
        changeStatusBarColor();

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        askPermissions();

        // Comment these lines if production Firebase should be used instead of emulator
        try {
            FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
            firestore.useEmulator("10.0.2.2", 8080);

            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build();
            firestore.setFirestoreSettings(settings);
        }
        catch (IllegalStateException e) {
            Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
        }

        initData();

        userPhotosToUpload = new ArrayList<>();
        for (int i = 0; i < 6; i++) userPhotosToUpload.add(null);

        initData();

        setImageOnClickListeners();

    }

    private void askPermissions(){
        if(ContextCompat.checkSelfPermission(PostRegisterUploadPhotos.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostRegisterUploadPhotos.this,
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

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    private void setImageOnClickListeners(){
        ibPostRegister1.setOnClickListener(this);
        ibPostRegister2.setOnClickListener(this);
        ibPostRegister3.setOnClickListener(this);
        ibPostRegister4.setOnClickListener(this);
        ibPostRegister5.setOnClickListener(this);
        ibPostRegister6.setOnClickListener(this);
        btnPostRegister.setOnClickListener(this);
    }

    private void initData(){
        ibPostRegister1 = findViewById(R.id.ib_post_register_photo1);
        ibPostRegister2 = findViewById(R.id.ib_post_register_photo2);
        ibPostRegister3 = findViewById(R.id.ib_post_register_photo3);
        ibPostRegister4 = findViewById(R.id.ib_post_register_photo4);
        ibPostRegister5 = findViewById(R.id.ib_post_register_photo5);
        ibPostRegister6 = findViewById(R.id.ib_post_register_photo6);

        btnPostRegister = findViewById(R.id.ib_post_register_next);

        ibPostRegister1HasImage = false;
        ibPostRegister2HasImage = false;
        ibPostRegister3HasImage = false;
        ibPostRegister4HasImage = false;
        ibPostRegister5HasImage = false;
        ibPostRegister6HasImage = false;

        pb = findViewById(R.id.pb_post_register_upload);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.ib_post_register_photo1:
                this.currentButtonClicked = 1;
                chooseImage(PostRegisterUploadPhotos.this);
                break;
            case R.id.ib_post_register_photo2:
                this.currentButtonClicked = 2;
                chooseImage(PostRegisterUploadPhotos.this);
                break;
            case R.id.ib_post_register_photo3:
                this.currentButtonClicked = 3;
                chooseImage(PostRegisterUploadPhotos.this);
                break;
            case R.id.ib_post_register_photo4:
                this.currentButtonClicked = 4;
                chooseImage(PostRegisterUploadPhotos.this);
                break;
            case R.id.ib_post_register_photo5:
                this.currentButtonClicked = 5;
                chooseImage(PostRegisterUploadPhotos.this);
                break;
            case R.id.ib_post_register_photo6:
                this.currentButtonClicked = 6;
                chooseImage(PostRegisterUploadPhotos.this);
                break;
            case R.id.ib_post_register_next:
                uploadImage();
                break;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if (requestCode == CAMERA_PICK_CODE) {

                    Bitmap image = (Bitmap) data.getExtras().get("data");

                    Uri uriLocal = getImageUri(PostRegisterUploadPhotos.this,image);

                    File originalImageFile = new File(getRealPathFromURI(uriLocal));
                    File compressedImageFile = null;

                    // Compress the image
                    try {
                        compressedImageFile = new Compressor(PostRegisterUploadPhotos.this).compressToFile(originalImageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    switch(this.currentButtonClicked) {
                        case 1:
                            this.userPhotosToUpload.set(0, compressedImageFile);
                            setImage(ibPostRegister1, requestCode, resultCode, Uri.fromFile(compressedImageFile), 1);
                            break;
                        case 2:
                            this.userPhotosToUpload.set(1, compressedImageFile);
                            setImage(ibPostRegister2, requestCode, resultCode, Uri.fromFile(compressedImageFile), 2);
                            break;
                        case 3:
                            this.userPhotosToUpload.set(2, compressedImageFile);
                            setImage(ibPostRegister3, requestCode, resultCode, Uri.fromFile(compressedImageFile), 3);
                            break;
                        case 4:
                            this.userPhotosToUpload.set(3, compressedImageFile);
                            setImage(ibPostRegister4, requestCode, resultCode, Uri.fromFile(compressedImageFile), 4);
                            break;
                        case 5:
                            this.userPhotosToUpload.set(4, compressedImageFile);
                            setImage(ibPostRegister5, requestCode, resultCode, Uri.fromFile(compressedImageFile), 5);
                            break;
                        case 6:
                            this.userPhotosToUpload.set(5, compressedImageFile);
                            setImage(ibPostRegister6, requestCode, resultCode, Uri.fromFile(compressedImageFile), 6);
                            break;
                    }

                } else if (requestCode == IMAGE_PICK_CODE) {

                        Uri selectedImageUri = data.getData();
                        File originalImageFile2 = new File(getRealPathFromURI(selectedImageUri));
                        File compressedImageFile2 = null;

                        // Compress the image
                        try {
                            compressedImageFile2 = new Compressor(PostRegisterUploadPhotos.this).compressToFile(originalImageFile2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        switch (this.currentButtonClicked) {
                            case 1:
                                this.userPhotosToUpload.set(0, compressedImageFile2);
                                setImage(ibPostRegister1, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 1);
                                break;
                            case 2:
                                this.userPhotosToUpload.set(1, compressedImageFile2);
                                setImage(ibPostRegister2, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 2);
                                break;
                            case 3:
                                this.userPhotosToUpload.set(2, compressedImageFile2);
                                setImage(ibPostRegister3, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 3);
                                break;
                            case 4:
                                this.userPhotosToUpload.set(3, compressedImageFile2);
                                setImage(ibPostRegister4, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 4);
                                break;
                            case 5:
                                this.userPhotosToUpload.set(4, compressedImageFile2);
                                setImage(ibPostRegister5, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 5);
                                break;
                            case 6:
                                this.userPhotosToUpload.set(5, compressedImageFile2);
                                setImage(ibPostRegister6, requestCode, resultCode, Uri.fromFile(compressedImageFile2), 6);
                                break;
                        }



                }




         }


    private void setImage(ImageButton ibPostRegister,int requestCode, int resultCode, Uri compressedImageUri,int imageButtonNumber) {

            ibPostRegister.setImageURI(compressedImageUri);

            ibPostRegister.setAdjustViewBounds(true);
            ibPostRegister.setScaleType(ImageView.ScaleType.FIT_XY);
            ibPostRegister.setPadding(15,15,15,15);

            switch (imageButtonNumber)
            {
                case 1:
                    ibPostRegister1HasImage = true;
                    break;
                case 2:
                    ibPostRegister2HasImage = true;
                    break;
                case 3:
                    ibPostRegister3HasImage = true;
                    break;
                case 4:
                    ibPostRegister4HasImage = true;
                    break;
                case 5:
                    ibPostRegister5HasImage = true;
                    break;
                case 6:
                    ibPostRegister6HasImage = true;
                    break;

        }
    }

    private void uploadImage() {
        // TODO: Enable loading circle view and disable the next button.

       if(checkIfNoProfilePicture()){
           return;
       };

       pb.setVisibility(View.VISIBLE);

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
            if (this.userPhotosToUpload.get(i) == null) continue;

            // Get the filename of each images and track them
            String filename = Uri.fromFile(this.userPhotosToUpload.get(i)).getLastPathSegment();

            // Prepare document
            Map<String, Object> filenameDocument = new HashMap<>();
            filenameDocument.put("filename", filename);
            filenameDocument.put("uid", uid);
            filenameDocument.put("uriTemp", Uri.fromFile(this.userPhotosToUpload.get(i)).toString());
            // Save information about the profile picture set at the upper left.
            if (i == 0)
                filenameDocument.put("isProfilePicture", true);
            else
                filenameDocument.put("isProfilePicture", false);

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
                                                        Log.d(SwipeActivity.firebaseLogKey, "All images uploaded!");
                                                        pb.setVisibility(View.GONE);
                                                        Intent i = new Intent(PostRegisterUploadPhotos.this,PostRegisterUserDetails.class);
                                                        startActivity(i);

                                                        finish();
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
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private boolean checkIfNoProfilePicture(){

        boolean hasNoProfilePicture = false;

        if(ibPostRegister1HasImage == false){

            hasNoProfilePicture = true;

            Toast.makeText(getApplicationContext(), "Upload a profile picture!", Toast.LENGTH_SHORT).show();

        }
        return hasNoProfilePicture;
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
}