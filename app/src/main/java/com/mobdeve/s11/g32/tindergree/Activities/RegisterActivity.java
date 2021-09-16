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

    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etFirstName;
    private EditText etLastName;
    private TextView tvLoginRedirect;
    private Button btnRegister;
    private ProgressBar pbRegister;

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
        etEmail = findViewById(R.id.et_register_emailaddress);
        etPassword = findViewById(R.id.et_register_password);
        etConfirmPassword = findViewById(R.id.et_register_confirm);
        etFirstName = findViewById(R.id.et_register_first_name);
        etLastName = findViewById(R.id.et_register_last_name);
        tvLoginRedirect = findViewById(R.id.tv_register_login);
        btnRegister = findViewById(R.id.btn_register);
        pbRegister = findViewById(R.id.pb_register);
        // Register button click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = etFirstName.getText().toString() + " " + etLastName.getText().toString();
                // Register the account, and get the current instance of the user
                registerAccount(etEmail.getText().toString(), etPassword.getText().toString(), displayName); // Asynchronous
                // TODO: Write code here that informs the user the app is currently creating the account. Probably a progress bar or something

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Comment these lines if production Firebase should be used instead of emulator
        if (SwipeActivity.useEmulator) {
            try {
                FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
                firestore.useEmulator("10.0.2.2", 8080);
            } catch (IllegalStateException e) {
                Log.d(SwipeActivity.firebaseLogKey, "Firestore emulator already instantiated!");
            }
        }
    }

    // When the user wants to login instead.
    public void loginRedirectOnClick(View v) {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);

        v.getContext().startActivity(loginIntent);
    }

    public void successfulRegisterRedirect() {
        pbRegister.setVisibility(View.GONE);

        Log.d(SwipeActivity.firebaseLogKey, "User has created account.");
        Intent successfulRegister = new Intent(RegisterActivity.this, PostRegisterUploadPhotos.class);

        startActivity(successfulRegister);
        finish();
    }

    public void registerAccount(String email, String password, String displayName) {

        if(checkEmptyFields(email,password))
            return;
        pbRegister.setVisibility(View.VISIBLE);

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
                            pbRegister.setVisibility(View.GONE);
                            Log.w(SwipeActivity.firebaseLogKey, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private boolean checkEmptyFields(String email,String password){
        boolean hasEmpty = false;

        String etFirstNameText = etFirstName.getText().toString();
        String etLastNameText = etLastName.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

            if(email.isEmpty()) {
                etEmail.setError("Please enter your email!");
                etEmail.requestFocus();
                hasEmpty = true;
            }else if(etFirstNameText.isEmpty()){
                etFirstName.setError("Please enter your first name!");
                etFirstName.requestFocus();
                hasEmpty = true;
            }else if(etLastNameText.isEmpty()){
                etLastName.setError("Please enter your last name!");
                etLastName.requestFocus();
                hasEmpty = true;
            }else if(password.isEmpty()){
                etPassword.setError("Please enter your password!");
                etPassword.requestFocus();
                hasEmpty = true;
            }else if(confirmPassword.isEmpty()){
                etConfirmPassword.setError("Please confirm your password!");
                etConfirmPassword.requestFocus();
                hasEmpty = true;
            }


            return hasEmpty;

    }

}