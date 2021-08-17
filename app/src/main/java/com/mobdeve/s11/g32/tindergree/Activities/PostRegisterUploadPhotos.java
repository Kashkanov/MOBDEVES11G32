package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.R;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import javax.xml.parsers.SAXParser;

import id.zelory.compressor.Compressor;

public class PostRegisterUploadPhotos extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;

    private ImageButton ibPostRegister1,ibPostRegister2,ibPostRegister3,
                        ibPostRegister4,ibPostRegister5,ibPostRegister6;

    private int currentButtonClicked; // tracks which add image button is tapped

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register_upload_photos);
        FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        ibPostRegister1 = findViewById(R.id.ib_post_register_photo1);
        ibPostRegister2 = findViewById(R.id.ib_post_register_photo2);
        ibPostRegister3 = findViewById(R.id.ib_post_register_photo3);
        ibPostRegister4 = findViewById(R.id.ib_post_register_photo4);
        ibPostRegister5 = findViewById(R.id.ib_post_register_photo5);
        ibPostRegister6 = findViewById(R.id.ib_post_register_photo6);

        setImageOnClickListeners();

    }

    private void setImageOnClickListeners(){
            ibPostRegister1.setOnClickListener(this);
            ibPostRegister2.setOnClickListener(this);
            ibPostRegister3.setOnClickListener(this);
            ibPostRegister4.setOnClickListener(this);
            ibPostRegister5.setOnClickListener(this);
            ibPostRegister6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.ib_post_register_photo1:
                this.currentButtonClicked = 1;
                changeImageOnClick();
                break;
            case R.id.ib_post_register_photo2:
                this.currentButtonClicked = 2;
                changeImageOnClick();
                break;
            case R.id.ib_post_register_photo3:
                this.currentButtonClicked = 3;
                changeImageOnClick();
                break;
            case R.id.ib_post_register_photo4:
                this.currentButtonClicked = 4;
                changeImageOnClick();
                break;
            case R.id.ib_post_register_photo5:
                this.currentButtonClicked = 5;
                changeImageOnClick();
                break;
            case R.id.ib_post_register_photo6:
                this.currentButtonClicked = 6;
                changeImageOnClick();
                break;
        }

    }

    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }else{
                    Toast.makeText(this,"Permission was denied.",Toast.LENGTH_SHORT).show();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED)
            return;

        Uri selectedImageUri = data.getData();
        File originalImageFile = new File(getRealPathFromURI(selectedImageUri));
        File compressedImageFile = null;

        // Compress the image
        try {
            compressedImageFile = new Compressor(PostRegisterUploadPhotos.this).compressToFile(originalImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch(this.currentButtonClicked)
       {
           case 1:
               setImage(ibPostRegister1,requestCode, resultCode, Uri.fromFile(compressedImageFile));
               break;
           case 2:
               setImage(ibPostRegister2,requestCode, resultCode, Uri.fromFile(compressedImageFile));
               break;
           case 3:
               setImage(ibPostRegister3,requestCode, resultCode, Uri.fromFile(compressedImageFile));
               break;
           case 4:
               setImage(ibPostRegister4,requestCode, resultCode, Uri.fromFile(compressedImageFile));
               break;
           case 5:
               setImage(ibPostRegister5,requestCode, resultCode, Uri.fromFile(compressedImageFile));
               break;
           case 6:
               setImage(ibPostRegister6,requestCode, resultCode, Uri.fromFile(compressedImageFile));
               break;
       }

    }

    private void setImage(ImageButton ibPostRegister,int requestCode, int resultCode, Uri compressedImageUri){
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            ibPostRegister.setImageURI(compressedImageUri);

            ibPostRegister.setAdjustViewBounds(true);
            ibPostRegister.setScaleType(ImageView.ScaleType.FIT_XY);
            ibPostRegister.setPadding(15,15,15,15);
        }
    }

    private void uploadImage(File compressedImageFile) {
        // TODO: Cloud Storage implementation
    }

    private void changeImageOnClick(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                //Permission not granted, request

                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //show popup for runtime permission
                requestPermissions(permissions,PERMISSION_CODE);

            }else{
                //permission granted
                pickImageFromGallery();
            }
        }else{
            //system os less than marshallow
            pickImageFromGallery();
        }
    }

}