package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.mobdeve.s11.g32.tindergree.Models.Matches;
import com.mobdeve.s11.g32.tindergree.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private EditText emailEt;
    private EditText passwordEt;
    private EditText confirmPasswordEt;
    private EditText firstName;
    private EditText lastName;
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


        changeStatusBarColor();
        initData();
    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    private void initData(){
        emailEt = findViewById(R.id.et_register_emailaddress);
        passwordEt = findViewById(R.id.et_register_password);
        confirmPasswordEt = findViewById(R.id.et_register_confirm);
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
                registerAccount(emailEt.getText().toString(), passwordEt.getText().toString(), displayName); // Asynchronous
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
        firestore = FirebaseFirestore.getInstance();

        // Comment these lines if production Firebase should be used instead of emulator
        try {
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
            firestore.useEmulator("10.0.2.2", 8080);
        }
        catch (IllegalStateException e) {
            Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
        }
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

        if(checkEmptyFields(email,password))
            return;
        registerPb.setVisibility(View.VISIBLE);

        // Create the account in Firebase Authentication
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
                            // Initialize Match record in the database
                            Matches initMatch = new Matches(currentUser.getUid());
                            initMatch.initializeMatches();

                            firestore.collection("Matches").document(currentUser.getUid()).set(initMatch);

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

    private boolean checkEmptyFields(String email,String password){
        boolean hasEmpty = false;

        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String confirmPassword = confirmPasswordEt.getText().toString();

            if(email.isEmpty()) {
                emailEt.setError("Please enter your email!");
                emailEt.requestFocus();
                hasEmpty = true;
            }else if(firstNameText.isEmpty()){
                firstName.setError("Please enter your first name!");
                firstName.requestFocus();
                hasEmpty = true;
            }else if(lastNameText.isEmpty()){
                lastName.setError("Please enter your last name!");
                lastName.requestFocus();
                hasEmpty = true;
            }else if(password.isEmpty()){
                passwordEt.setError("Please enter your password!");
                passwordEt.requestFocus();
                hasEmpty = true;
            }else if(confirmPassword.isEmpty()){
                confirmPasswordEt.setError("Please confirm your password!");
                confirmPasswordEt.requestFocus();
                hasEmpty = true;
            }


            return hasEmpty;

    }

}