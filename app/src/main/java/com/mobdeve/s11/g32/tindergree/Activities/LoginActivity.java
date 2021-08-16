package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.s11.g32.tindergree.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView emailTv;
    private TextView passwordTv;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTv = findViewById(R.id.et_login_emailaddress);
        passwordTv = findViewById(R.id.et_login_password);
        loginBtn = findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();

        // Login button click event
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(emailTv.getText().toString(), passwordTv.getText().toString());

                FirebaseUser currentUser = mAuth.getCurrentUser();

                // Check if user is signed in (non-null).
                if(currentUser != null){
                    Log.d(SwipeActivity.firebaseLogKey, "User has signed in.");
                    // End Register Activity.
                    Intent intent = new Intent("finish_activity");
                    sendBroadcast(intent);

                    // Move on to homescreen.
                    Intent homescreenIntent = new Intent(v.getContext(), SwipeActivity.class);
                    v.getContext().startActivity(homescreenIntent);

                    finish();
                }
                else {
                    Log.d(SwipeActivity.firebaseLogKey, "Unauthorized access to Swipe Activity thwarted.");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Use Firebase emulator instead
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    protected void onLoginClick(View v) {
        Log.d(SwipeActivity.firebaseLogKey, "Attempt login CLICKED");
    }

    /**
     * Signs the user using Firebase Authentication. Display Toast if user does not exist.
     * @param email - email of the user
     * @param password - password of the user
     */
    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(SwipeActivity.firebaseLogKey, "signInWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(SwipeActivity.firebaseLogKey, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed. Check credentials.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        Log.d("AUTH_TEST", "Successfully logged out.");
    }
}