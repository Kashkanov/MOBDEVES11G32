package com.mobdeve.s11.g32.tindergree.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobdeve.s11.g32.tindergree.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView tvRegister;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar pbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        changeStatusBarColor();

        tvRegister = findViewById(R.id.tv_login_register);
        etEmail = findViewById(R.id.et_login_emailaddress);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        pbLogin = findViewById(R.id.pb_login);

        mAuth = FirebaseAuth.getInstance();

        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void changeStatusBarColor(){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF914D"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Use Firebase emulator instead
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    private void successLoginRedirect() {
        // End Register Activity.
        pbLogin.setVisibility(View.GONE);
        Intent intent = new Intent("finish_activity");
        sendBroadcast(intent);

        // Move on to homescreen.
        Intent homescreenIntent = new Intent(LoginActivity.this, SwipeActivity.class);
        startActivity(homescreenIntent);

        finish();
    }

    /**
     * Signs the user using Firebase Authentication. Display Toast if user does not exist.
     * @param email - email of the user
     * @param password - password of the user
     */
    public void signIn(String email, String password){

        if(checkEmptyFields(email,password))
            return;

        pbLogin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            successLoginRedirect();
                            Log.d(SwipeActivity.firebaseLogKey, "signInWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            pbLogin.setVisibility(View.GONE);
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

    private boolean checkEmptyFields(String email,String password){
        boolean hasEmpty = false;

        if(email.isEmpty()){
            etEmail.setError("Please enter your email!");
            etEmail.requestFocus();
            hasEmpty = true;
        }else if(password.isEmpty()){
            etPassword.setError("Please enter your password!");
            etPassword.requestFocus();
            hasEmpty = true;
        }

        return hasEmpty;
    }
}