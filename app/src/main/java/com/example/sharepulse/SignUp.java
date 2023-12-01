package com.example.sharepulse;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

public class SignUp extends AppCompatActivity {
    //Navigation
    private Button toNext, toLogin;

    //bod selection
    private Button BODbtn, chooseImageButton;

    //image selecting
    private Uri imageUri;
    private ImageView imagePreview;
    private ImagePickerHelper imagePickerHelper;

    //signup
    private TextView fName, lName, birthDate, signUpEmail, MobileNo, Gender, Nic;

    //gender selection
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    // Firebase Storage
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up);

        signUpEmail = findViewById(R.id.userEmail);
        fName = findViewById(R.id.Fname);
        lName = findViewById(R.id.Lname);
        MobileNo = findViewById(R.id.mobileNo);
        Gender = findViewById(R.id.selectGender);
        Nic = findViewById(R.id.NIC);
        birthDate = findViewById(R.id.inputBirthDate);


        toLogin = findViewById(R.id.movetoLogin);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this,Login.class);
                startActivity(intent);
            }
        });


        //BOD selection
        BODbtn = findViewById(R.id.dateSelect);

        BODbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalender();
            }
        });

        //gender selection
        String[] genders = {"Male","Female","Non Binary"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.gender_dropdown,genders);
        AutoCompleteTextView autoCompleteTextView=findViewById(R.id.selectGender);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SignUp.this, autoCompleteTextView.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        chooseImageButton = findViewById(R.id.choose_image_button);
        imagePreview = findViewById(R.id.image_preview);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        imagePickerHelper = new ImagePickerHelper(this, new ImagePickerHelper.ImagePickerCallback() {
            @Override
            public void onImageSelected(Bitmap bitmap, Uri imageUri) {
                // Handle the selected image
                imagePreview.setImageBitmap(bitmap);
                // Store the imageUri for later use
                SignUp.this.imageUri = imageUri;
            }
        });

        //signup validation
        toNext = findViewById(R.id.nextBtn);
        toNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Retrieve the entered data
                String user = signUpEmail.getText().toString().trim();
                String fname = fName.getText().toString().trim();
                String lname = lName.getText().toString().trim();
                String mobileNo = MobileNo.getText().toString().trim();
                String gender = Gender.getText().toString().trim();
                String nic = Nic.getText().toString().trim();
                String birthdate = birthDate.getText().toString().trim();

                // Validate the entered data
                if (fname.isEmpty()) {
                    fName.setError("First Name Required!");
                    fName.requestFocus();
                    return;
                }
                if (lname.isEmpty()) {
                    lName.setError("Last Name Required!");
                    lName.requestFocus();
                    return;
                }
                if (user.isEmpty()) {
                    signUpEmail.setError("Email Required!");
                    signUpEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
                    signUpEmail.setError("Valid Email Required!");
                    signUpEmail.requestFocus();
                    return;
                }
                if (mobileNo.isEmpty()) {
                    MobileNo.setError("Mobile Number Required!");
                    MobileNo.requestFocus();
                    return;
                }
                if (gender.isEmpty()) {
                    Gender.setError("Gender Required!");
                    Gender.requestFocus();
                    return;
                }
                if (nic.isEmpty()) {
                    Nic.setError("NIC Required!");
                    Nic.requestFocus();
                    return;
                }
                if (birthdate.isEmpty()) {
                    birthDate.setError("Birthdate Required!");
                    birthDate.requestFocus();
                    return;
                }

                // Create an intent to start the next activity
                Intent intent = new Intent(SignUp.this, SignUp2.class);
                intent.putExtra("EMAIL", user);
                intent.putExtra("FNAME", fname);
                intent.putExtra("LNAME", lname);
                intent.putExtra("MOBILE", mobileNo);
                intent.putExtra("GENDER", gender);
                intent.putExtra("NIC", nic);
                intent.putExtra("BDAY", birthdate);

                // Pass the image URI to the next activity if it is not null
                if (imageUri != null) {
                    intent.putExtra("IMAGE_URI", imageUri.toString());
                }

                startActivity(intent);
            }
        });

    }

    private void openCalender(){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                birthDate.setText(String.valueOf(year)+"/"+String.valueOf(month+1)+"/"+String.valueOf(day));
            }
        }, 2023, 0, 15);
        dialog.show();
    }

    private void openImagePicker() {
        imagePickerHelper.openImagePicker();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePickerHelper.handleActivityResult(requestCode, resultCode, data);
    }

}
