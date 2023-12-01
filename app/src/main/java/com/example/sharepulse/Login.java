package com.example.sharepulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private Button toReset, toSignup, toDashboard;
    private FirebaseAuth auth;
    private ProgressBar progressBar2;
    private TextView loginEmail, loginPs, navigateToAdminBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //linking all the variables with ids
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.loginEmail);
        loginPs = findViewById(R.id.loginPs);
        toDashboard = findViewById(R.id.Login);

        // Retrieve message from intent
        String message = getIntent().getStringExtra("message");
        if (message != null) {
            // Display message as toast
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        toReset = findViewById(R.id.moveReset);
        toReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, reset_screen01.class);
                startActivity(intent);
            }
        });

        toSignup = findViewById(R.id.moveSignup);
        toSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Login.this, SignUp.class);
                startActivity(intent1);
            }
        });

        navigateToAdminBtn = findViewById(R.id.moveAdmin);
        navigateToAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Login.this, AdminLogin.class);
                startActivity(intent2);

            }
        });


        toDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lgEmail = loginEmail.getText().toString();
                String lgPassword = loginPs.getText().toString();

                if(!lgEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(lgEmail).matches()){
                    if(!lgPassword.isEmpty()){
                        progressBar2.setVisibility(View.VISIBLE);
                        auth.signInWithEmailAndPassword(lgEmail, lgPassword)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(Login.this,"Login SuccessFul",
                                                Toast.LENGTH_SHORT).show();

                                        Intent intent2 = new Intent(Login.this, Dashboard.class);

                                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent2);
                                        progressBar2.setVisibility(View.GONE);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar2.setVisibility(View.GONE);
                                        Toast.makeText(Login.this, "Your email or password is wrong!",
                                                Toast.LENGTH_SHORT).show();
                                        loginEmail.requestFocus();
                                        loginPs.requestFocus();
                                    }
                                });
                    }else{
                        loginPs.setError("Password Cannot be Empty!");
                        loginPs.requestFocus();
                    }
                } else if (lgEmail.isEmpty()) {
                    loginEmail.setError("Email Cannot be Empty!");
                    loginEmail.requestFocus();
                }else{
                    loginEmail.setError("Please Enter Valid email!");
                    loginEmail.requestFocus();
                }
            }
        });


    }

}