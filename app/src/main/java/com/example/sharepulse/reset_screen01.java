package com.example.sharepulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class reset_screen01 extends AppCompatActivity {
    private Button cancelBtn, sendOTP;
    private EditText emailInput;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset_screen01);

        //initialize
        auth = FirebaseAuth.getInstance();

        sendOTP = findViewById(R.id.send_otp_btn);
        emailInput = findViewById(R.id.email_input);


        //password reset operation
        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailInput.setError("valid email Required!");
                    emailInput.requestFocus();
                    return;
                }else{
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(reset_screen01.this, "Check your email", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(reset_screen01.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        cancelBtn = findViewById(R.id.Login);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancel and go back
                Intent intent = new Intent(reset_screen01.this,Login.class);
                startActivity(intent);
            }
        });
    }
}