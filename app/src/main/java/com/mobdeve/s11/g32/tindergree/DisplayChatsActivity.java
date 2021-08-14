package com.mobdeve.s11.g32.tindergree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DisplayChatsActivity extends AppCompatActivity {

    private RecyclerView rv_matches;
    private MatchAdapter matchAdapter;
    private ArrayList<Profile> profiles;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Comment this line if Firebase emulator is not being used.
        //FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("AUTH_TEST", "User has signed in.");
        }
        else {
            Log.d("AUTH_TEST", "User has NOT signed in.");
        }
        Intent i = getIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chats);
        this.initRecyclerView();

    }

    /**
     * Signs the user using Firebase Authentication. Display Toast if user does not exist.
     * @param email - email of the user
     * @param password - password of the user TODO: Password hashing
     */
    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AUTH_TEST", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AUTH_TEST", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        Log.d("AUTH_TEST", "Successfully logged out.");
    }

    public void initRecyclerView(){
        CardDataHelper carddataHelper = new CardDataHelper();

        this.profiles = carddataHelper.loadProfileData();
        this.rv_matches = findViewById(R.id.rv_matches);



        GridLayoutManager manager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);

        this.rv_matches.setLayoutManager(manager);
        this.matchAdapter = new MatchAdapter(this.profiles);
        this.rv_matches.setAdapter(this.matchAdapter);

    }

}
