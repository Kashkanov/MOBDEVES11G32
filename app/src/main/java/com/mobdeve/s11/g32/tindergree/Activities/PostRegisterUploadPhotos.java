package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.mobdeve.s11.g32.tindergree.R;

public class PostRegisterUploadPhotos extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_register_upload_photos);

        mAuth = FirebaseAuth.getInstance();

        // TODO: Implement Gallery pick implicit intent. I need the URI of the image.
    }
}