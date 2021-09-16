package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobdeve.s11.g32.tindergree.DataHelpers.Keys;
import com.mobdeve.s11.g32.tindergree.R;

public class SettingsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    private Button btnLogout, btnDeleteAccount;
    private ConstraintLayout clDeveloperNotes,clProfileActivity,clDarkMode;
    private Switch switchDark;
    private Toolbar toolbar;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.Theme_TindergreeDark);
        }
        else{
            setTheme(R.style.Theme_Tindergree);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changeStatusBarColor();

        this.toolbar = findViewById(R.id.settings_toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        btnLogout = findViewById(R.id.btn_settings_log_out);
        btnDeleteAccount = findViewById(R.id.btn_settings_delete_account);
        clDeveloperNotes = findViewById(R.id.cl_settings_developer_notes);
        clProfileActivity = findViewById(R.id.cl_settings_profile_activity);
        clDarkMode = findViewById(R.id.cl_settings_child_dark);
        switchDark = findViewById(R.id.switch_dark);
        Intent i = getIntent();

        this.sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.spEditor = this.sp.edit();
        this.loadData();

        // Sign out the user on click and destroy activities
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Log.d(SwipeActivity.firebaseLogKey, "User signed out.");
                Intent authenticateIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(authenticateIntent);

                // End Register Activity.
                Intent intent = new Intent("finish_main_activity");
                sendBroadcast(intent);

                finish(); // Destroy activity
            }
        });

        // Delete account
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = mAuth.getUid();

                deleteAuthentication();
                deleteStorage(uid);
                deleteFirestore(uid);

                // Sign out
                mAuth.signOut();
                Log.d(SwipeActivity.firebaseLogKey, "User signed out.");
                Intent authenticateIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(authenticateIntent);

                // End Register Activity.
                Intent intent = new Intent("finish_main_activity");
                sendBroadcast(intent);

                finish(); // Destroy activity
            }
        });

        clDeveloperNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,DeveloperNotesActivity.class);
                startActivity(intent);
            }
        });

        clProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,UserProfilePageActivity.class);
                startActivity(intent);
            }
        });

    }

    private void deleteAuthentication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(SwipeActivity.firebaseLogKey, "User account deleted.");
                        }
                    }
                });
    }

    private void deleteStorage(String uid) {
        // Get the filenames of media to be deleted
        firestore.collection("filenames")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(SwipeActivity.firebaseLogKey, document.getId() + " => " + document.getData());
                                String filenameDelete = document.getData().get("filename").toString();

                                // Create a storage reference from our app
                                StorageReference storageRef = storage.getReference();

                                // Create a reference to the file to delete
                                StorageReference deleteImageRef = storageRef.child("Users/" + uid + "/" + filenameDelete);

                                // Delete the file
                                deleteImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(SwipeActivity.firebaseLogKey, "Deleted " + filenameDelete + " successfully.");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });
                            }
                        } else {
                            Log.d(SwipeActivity.firebaseLogKey, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void deleteFirestore(String uid) {
        // Delete entries from "Matches" collection
        firestore.collection("Matches")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get document ID
                                String matchDocumentKey = document.getId();

                                // Delete document using that ID
                                firestore.collection("Matches").document(matchDocumentKey)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(SwipeActivity.firebaseLogKey, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(SwipeActivity.firebaseLogKey, "Error deleting document", e);
                                            }
                                        });
                            }
                        } else {
                        }
                    }
                });

        // Delete entries from "Pets" collection
        firestore.collection("Pets")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get document ID
                                String matchDocumentKey = document.getId();

                                // Delete document using that ID
                                firestore.collection("Pets").document(matchDocumentKey)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(SwipeActivity.firebaseLogKey, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(SwipeActivity.firebaseLogKey, "Error deleting document", e);
                                            }
                                        });
                            }
                        } else {
                        }
                    }
                });

        // Delete entries from "filenames" collection
        firestore.collection("filenames")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get document ID
                                String matchDocumentKey = document.getId();

                                // Delete document using that ID
                                firestore.collection("filenames").document(matchDocumentKey)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(SwipeActivity.firebaseLogKey, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(SwipeActivity.firebaseLogKey, "Error deleting document", e);
                                            }
                                        });
                            }
                        } else {
                        }
                    }
                });
    }

    private void loadData(){
        boolean switchDarkChecked = this.sp.getBoolean(Keys.KEY_DARK_BOOL.name(), false);
        this.switchDark.setChecked(switchDarkChecked);
        if(switchDarkChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }

    private void saveData(){

        this.spEditor.putBoolean(Keys.KEY_DARK_BOOL.name(), this.switchDark.isChecked());
        this.spEditor.apply();
    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    protected void onPause(){
         super.onPause();
         this.saveData();
         overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}