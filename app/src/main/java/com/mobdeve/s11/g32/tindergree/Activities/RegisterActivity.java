package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mobdeve.s11.g32.tindergree.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView emailTv;
    private TextView passwordTv;
    private TextView firstName;
    private TextView lastName;
    private TextView loginRedirectTv;
    private Button registerBtn;
    private ProgressBar registerPb;

    // BroadcastReceiver to end the activity from another activity.
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals("finish_activity")) {
                finish();
                Log.d(SwipeActivity.firebaseLogKey, "Register activity ended.");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));

        emailTv = findViewById(R.id.et_register_emailaddress);
        passwordTv = findViewById(R.id.et_register_password);
        firstName = findViewById(R.id.et_register_first_name);
        lastName = findViewById(R.id.et_register_last_name);
        loginRedirectTv = findViewById(R.id.tv_register_login);
        registerBtn = findViewById(R.id.btn_register);
        registerPb = findViewById(R.id.pb_register);
        // Register button click event
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = firstName.getText().toString() + " " + lastName.getText().toString();
                // Register the account, and get the current instance of the user
                registerAccount(emailTv.getText().toString(), passwordTv.getText().toString(), displayName); // Asynchronous
                // TODO: Write code here that informs the user the app is currently creating the account. Probably a progress bar or something

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

    // When the user wants to login instead.
    public void loginRedirectOnClick(View v) {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);

        v.getContext().startActivity(loginIntent);
    }

    public void successfulRegisterRedirect() {
        registerPb.setVisibility(View.GONE);

        Log.d(SwipeActivity.firebaseLogKey, "User has created account.");
        Intent successfulRegister = new Intent(RegisterActivity.this, PostRegisterUploadPhotos.class);

        startActivity(successfulRegister);
        finish();
    }

    public void registerAccount(String email, String password, String displayName) {
        // This is a shit input validation. TODO: Implement proper validation. Just make it simple, I don't think miss cares too much about it.
        if (email.length() == 0)
            return;
        registerPb.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // Successful register
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            // Update user profile the save name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            currentUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(SwipeActivity.firebaseLogKey, "User profile updated.");
                                            }
                                        }
                                    });
                            Log.d(SwipeActivity.firebaseLogKey, "createUserWithEmail:success");
                            successfulRegisterRedirect();
                        } else {
                            // If sign in fails, display a message to the user.
                            registerPb.setVisibility(View.GONE);
                            Log.w(SwipeActivity.firebaseLogKey, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}