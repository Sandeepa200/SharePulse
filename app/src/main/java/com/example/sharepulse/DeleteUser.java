package com.example.sharepulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteUser extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private Button psVerifyBtn, deleteBtn;
    private EditText password;
    private TextView verifyMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        getSupportActionBar().setTitle("Delete Profile");

        //variable linking
        psVerifyBtn = findViewById(R.id.deleteVerifyBtn);
        deleteBtn = findViewById(R.id.deleteAccBtn);
        password = findViewById(R.id.passForDelete);
        verifyMsg = findViewById(R.id.verifyMsg);
        progressBar = findViewById(R.id.progressBar6);

        //initialization
        progressBar.setVisibility(View.GONE);
        verifyMsg.setText("Your password is not verified yet!");


        //disable delete btn untill verifying
        deleteBtn.setEnabled(false);
        deleteBtn.setBackgroundTintList(ContextCompat.getColorStateList(
                DeleteUser.this,R.color.ash
        ));

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        //cheking if the user is available or not
        if(firebaseUser.equals("")){
            Toast.makeText(DeleteUser.this,"User details not Available!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteUser.this, Dashboard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            //calling re authenticate method
            reAuthenticate(firebaseUser);
        }

    }

    private void reAuthenticate (FirebaseUser firebaseUser){
        psVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Userpass = password.getText().toString().trim();
                if(Userpass.isEmpty()){
                    Toast.makeText(DeleteUser.this,"Enter your password!", Toast.LENGTH_SHORT).show();
                    password.setError("Password Cannot be Empty!");
                    password.requestFocus();
                    return;
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    //reAuthenticate the user
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), Userpass);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                //disabling password input
                                password.setEnabled(false);
                                psVerifyBtn.setEnabled(false);
                                psVerifyBtn.setBackgroundTintList(ContextCompat.getColorStateList(
                                        DeleteUser.this,R.color.ash
                                ));
                                //enabling delete btn
                                deleteBtn.setEnabled(true);
                                deleteBtn.setBackgroundTintList(ContextCompat.getColorStateList(
                                        DeleteUser.this,R.color.red
                                ));

                                //setting the verify message
                                verifyMsg.setText("Your password is successfully verified\n you can delete your account now!");

                                progressBar.setVisibility(View.GONE);
                                deleteBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showAlertDialog();
                                    }
                                });
                            }else{
                                Toast.makeText(DeleteUser.this,"Wrong Password!", Toast.LENGTH_SHORT).show();
                                verifyMsg.setText("Your password is not verified\n Try again!");
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }

            }
        });
    }

    private void showAlertDialog(){
        //alert box initialization
        AlertDialog.Builder builder = new AlertDialog.Builder( DeleteUser.this);
        builder.setTitle("Warning!");
        builder.setMessage("Do you really need to delete this Account and It's Related data?\n This action is irreversible!");

        //calling delete user method if user pressed yes
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressBar.setVisibility(View.VISIBLE);
                deleteUser(firebaseUser);
            }
        });

        //navigate back if user clicks no
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(DeleteUser.this,UserProfile.class);
                startActivity(intent);
                finish();
            }
        });



        AlertDialog alertDialog = builder.create();
        //change the color of the yes button
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });
        alertDialog.show();
    }

    private void deleteUser(FirebaseUser firebaseUser) {
        // Delete the user's profile picture
        deleteUserProfilePicture(firebaseUser);
        //delete the user
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            //delete user data if user deletion succeeds
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    deleteUserData();
                    authProfile.signOut();
                    Toast.makeText(DeleteUser.this,"Account Deleted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteUser.this,Login.class);
                    startActivity(intent);
                    finish();
                }else{
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(DeleteUser.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void deleteUserData() {
        //delete user data in realtime database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DeleteUserActivity ", "user data deleted Successfully!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DeleteUserActivity ", e.getMessage());
                Toast.makeText(DeleteUser.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    private void deleteUserProfilePicture(FirebaseUser firebaseUser) {
        // Get a reference to the Firebase Storage instance
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a reference to the user's profile picture file
        StorageReference profilePicRef = storage.getReference().child("profileImages").child(firebaseUser.getUid() + ".jpg");

        // Delete the profile picture file
        profilePicRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DeleteUserActivity", "Profile picture deleted successfully!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DeleteUserActivity", "Failed to delete profile picture: " + e.getMessage());
                Toast.makeText(DeleteUser.this, "Failed to delete profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}