package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s11.g32.tindergree.Adapters.CardAdapter;
import com.mobdeve.s11.g32.tindergree.Adapters.OtherPicsAdapter;
import com.mobdeve.s11.g32.tindergree.Models.OtherPic;
import com.mobdeve.s11.g32.tindergree.R;

import java.util.ArrayList;

public class OtherPicsActivity extends AppCompatActivity {

    private ImageView iv_profprofpic;
    private TextView tv_profname;
    private TextView tv_profdesc;
    private ArrayList<OtherPic> otherpics;
    private RecyclerView rv_otherpics;
    private OtherPicsAdapter otherPicsAdapter;
    private ProgressBar pb_otherPicsLoading;

    private String profUid, profname, profdesc;

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    private int maxNumberofImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        changeStatusBarColor();

        // Initialize Firebase Auth
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Comment these lines if production Firebase should be used instead of emulator
        try {
            FirebaseStorage.getInstance().useEmulator("10.0.2.2", 9199);
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
            firestore.useEmulator("10.0.2.2", 8080);
        }
        catch (IllegalStateException e) {
            Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
        }

        this.pb_otherPicsLoading = findViewById(R.id.pb_otherPicsLoading);
        this.rv_otherpics = findViewById(R.id.rv_profotherpics);
        pb_otherPicsLoading.setVisibility(View.VISIBLE);
        rv_otherpics.setVisibility(View.GONE);

        this.iv_profprofpic = findViewById(R.id.iv_profprofpic);
        this.tv_profname = findViewById(R.id.tv_profname);
        this.tv_profdesc = findViewById(R.id.tv_profdesc);

        Intent i = getIntent(); // Required: Pet name, Description, UID
        this.profname = i.getStringExtra(CardAdapter.KEY_PROFNAME);
        this.tv_profname.setText(profname);
        this.profdesc = i.getStringExtra(CardAdapter.KEY_PROFDESC);
        this.tv_profdesc.setText(profdesc);
        this.profUid = i.getStringExtra(CardAdapter.KEY_PROFUID);

        otherpics = new ArrayList<>();
        fetchProfilePicture();
        fetchImages();
    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    // TODO: Continue off this
    private void fetchProfilePicture() {
        StorageReference storageRef = storage.getReference();

        firestore.collection("filenames")
                .whereEqualTo("uid", this.profUid)
                .whereEqualTo("isProfilePicture", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String filename = document.getData().get("filename").toString();

                                // Got the filename, now fetch the image file from storage
                                StorageReference profilePicRef = storageRef.child("Users/" + profUid + "/" + filename);

                                final long MAX_BYE_SIZE = 1024 * 10024;
                                profilePicRef.getBytes(MAX_BYE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // Show the profile picture to the app
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                                        iv_profprofpic.setImageBitmap(bitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });

                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Failed to get profile picture");
                        }
                    }
                });
    }

    private void fetchImages() {
        firestore.clearPersistence();
        StorageReference storageRef = storage.getReference();

        // First, fetch the filenames of the images
        firestore.collection("filenames")
                .whereEqualTo("uid", this.profUid)
                .whereEqualTo("isProfilePicture", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            maxNumberofImages = task.getResult().size();
                            if (maxNumberofImages == 0) { // The user has no other pictures besides their profile picture
                                Log.d(SwipeActivity.firebaseLogKey, "No other pictures found.");
                                rv_otherpics.setVisibility(View.INVISIBLE);
                                pb_otherPicsLoading.setVisibility(View.INVISIBLE);
                                return;
                            }

                            // For each filename, fetch the image file from Cloud Storage
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(SwipeActivity.firebaseLogKey, document.getId() + " => " + document.getData());
                                String filename = document.getData().get("filename").toString();

                                // Fetch the image now
                                StorageReference imageRef = storageRef.child("Users/" + profUid + "/" + filename);

                                final long MAX_BYE_SIZE = 1024 * 10024;
                                imageRef.getBytes(MAX_BYE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // Fetched the image. Save to app.
                                        Log.d(SwipeActivity.firebaseLogKey, "Image fetched. Ready to save to app.");

                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                                        OtherPic a = new OtherPic(bitmap);
                                        otherpics.add(a);
                                        maxNumberofImages--;

                                        if (maxNumberofImages == 0) {
                                            initRecyclerView();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initRecyclerView(){
        Log.d(SwipeActivity.firebaseLogKey, "All images of the user has been fetched. Setting up RecyclerView now...");

        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);

        this.rv_otherpics.setLayoutManager(manager);
        this.otherPicsAdapter = new OtherPicsAdapter(this.otherpics);
        this.rv_otherpics.setAdapter(this.otherPicsAdapter);

        pb_otherPicsLoading.setVisibility(View.GONE);
        rv_otherpics.setVisibility(View.VISIBLE);
    }
}