package com.example.sharepulse;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp2 extends AppCompatActivity {
    private Button toDashboard;
    private Button toLogin2;
    private ProgressBar progressBar;

    //firebase part
    private FirebaseAuth auth;
    private TextView password, re_password;
    private CheckBox checkBoxDrug, checkBoxHomo, checkBoxSex;

    // Firebase Storage
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up2);

        // Firebase
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        password = findViewById(R.id.newPassword);
        re_password = findViewById(R.id.newPassword2);
        progressBar = findViewById(R.id.progressBar4);
        progressBar.setVisibility(View.GONE);

        checkBoxDrug = findViewById(R.id.checkBox_drug);
        checkBoxHomo = findViewById(R.id.checkBox_homo);
        checkBoxSex = findViewById(R.id.checkBox_sex);

        toDashboard = findViewById(R.id.moveToHome);
        toDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = password.getText().toString().trim();
                String pass2 = re_password.getText().toString().trim();

                // Getting components from other screen
                String fName = getIntent().getStringExtra("FNAME");
                String lName = getIntent().getStringExtra("LNAME");
                String user = getIntent().getStringExtra("EMAIL");
                String mobileNo = getIntent().getStringExtra("MOBILE");
                String gender = getIntent().getStringExtra("GENDER");
                String nic = getIntent().getStringExtra("NIC");
                String bDate = getIntent().getStringExtra("BDAY");
                String profileImage = getIntent().getStringExtra("IMAGE_URI");

                // Check if the checkboxes are checked
                if (!checkBoxDrug.isChecked()) {
                    checkBoxDrug.setError("required!");
                    return;
                }
                if (!checkBoxHomo.isChecked()) {
                    checkBoxHomo.setError("required!");
                    return;
                }
                if (!checkBoxSex.isChecked()) {
                    checkBoxSex.setError("required!");
                    return;
                }

                if (pass.isEmpty()) {
                    password.setError("Password Required!");
                    password.requestFocus();
                    return;
                }
                String errorMessage = validatePassword(pass);
                if (errorMessage != null) {
                    // Show the error message to the user
                    password.setError(errorMessage);
                    password.requestFocus();
                    return;
                }
                if (pass2.isEmpty()) {
                    re_password.setError("Repeat Password Required!");
                    re_password.requestFocus();
                    return;
                }
                if (!pass.equals(pass2)) {
                    re_password.setError("Repeat Password is not Same!");
                    re_password.requestFocus();
                    return;
                } else {

                    // Check if the email already exists in Firebase Authentication
                    auth.fetchSignInMethodsForEmail(user).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                SignInMethodQueryResult result = task.getResult();
                                if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                                    // Email already exists, show error toast
                                    Toast.makeText(SignUp2.this, "Email already exists", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    // Email doesn't exist, proceed with sign up

                                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                                // Uploading the profile image to Firebase Storage
                                                if (profileImage != null) {
                                                    Uri imageUri = Uri.parse(profileImage);
                                                    StorageReference profileImageRef = storageReference.child("profileImages").child(firebaseUser.getUid() + ".jpg");

                                                    profileImageRef.putFile(imageUri)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    // Get the download URL of the uploaded image
                                                                    profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri downloadUri) {
                                                                            String imageUrl = downloadUri.toString();
                                                                            // Inserting the user details to the database
                                                                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fName, lName, mobileNo, gender, nic, bDate, imageUrl);

                                                                            // Getting the reference from the database
                                                                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                                                                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(SignUp2.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                                                                        Intent intent2 = new Intent(SignUp2.this, Login.class);
                                                                                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                        startActivity(intent2);
                                                                                        finish();
                                                                                    } else {
                                                                                        Toast.makeText(SignUp2.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(SignUp2.this, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                                                                    // Inserting the user details to the database without the image
                                                                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fName, lName, mobileNo, gender, nic, bDate);

                                                                    // Getting the reference from the database
                                                                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                                                                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Toast.makeText(SignUp2.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                                                                Intent intent2 = new Intent(SignUp2.this, Login.class);
                                                                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                startActivity(intent2);
                                                                                finish();
                                                                            } else {
                                                                                Toast.makeText(SignUp2.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                } else {
                                                    // Inserting the user details to the database without the image
                                                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(fName, lName, mobileNo, gender, nic, bDate);

                                                    // Getting the reference from the database
                                                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                                                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SignUp2.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                                                Intent intent2 = new Intent(SignUp2.this, Login.class);
                                                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(intent2);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(SignUp2.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            } else {
                                                Toast.makeText(SignUp2.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(SignUp2.this, "Failed to check email existence", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            }
        });

        toLogin2 = findViewById(R.id.movetoLogin2);
        toLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp2.this, Login.class);
                startActivity(intent);
            }
        });
    }

    public static String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password should be at least 8 characters long.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password should contain at least one digit.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password should contain at least one lowercase letter.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password should contain at least one uppercase letter.";
        }
        if (!password.matches(".*[@#$%^&+=!].*")) {
            return "Password should contain at least one special character (@#$%^&+=!).";
        }
        return null;
    }
}
