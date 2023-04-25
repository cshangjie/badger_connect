package com.example.badgerconnect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ProfileCreationGeneralInfoActivity extends AppCompatActivity {
    // declarations
    private EditText nameField;
    private AutoCompleteTextView majorField;
    private EditText birthdayField;
    private Button continueBtn;
    private ImageView profileImg;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Bitmap selectedImageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation_general_info);
        initializeUI();


        continueBtn.setOnClickListener(new View.OnClickListener() {
            String mName, mMajor;
            Date mBirthdate = null;

            @Override
            public void onClick(View view) {
                // check the birthdate, name and major for strings.
                mName = nameField.getText().toString().trim();
                mMajor = majorField.getText().toString().trim();
                String dateString = birthdayField.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

                try {
                    mBirthdate = format.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // if any are null/empty -- show the corresponding error msg
                // check the profile picture
                if(selectedImageBitmap == null){
                    Toast.makeText(getApplicationContext(), "Please add a profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mName.isBlank()) {
                    nameField.setError("Name is required!");
                    nameField.requestFocus();
                    return;
                }
                if (mMajor.isBlank()) {
                    majorField.setError("Major is required!");
                    majorField.requestFocus();
                    return;
                }
                if (dateString.isBlank()) {
                    birthdayField.setError("Birthdate is required!");
                    majorField.requestFocus();
                    return;
                }
                if (mBirthdate == null) {
                    Toast.makeText(getApplicationContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check that the user is above 13 according to Children's Online Privacy Protection Act (COPPA)
                Calendar now = Calendar.getInstance();
                Calendar dob = Calendar.getInstance();
                dob.setTime(mBirthdate);

                int age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                if (now.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
                    age--;
                } else if (now.get(Calendar.MONTH) == dob.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
                    age--;
                }

                if (age < 13) {
                    Toast.makeText(getApplicationContext(), "You must be at least 13 years old", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Passed all information Validation, send the information to the next activity.
                Intent intent = new Intent(ProfileCreationGeneralInfoActivity.this, ProfileCreationActivity.class);
                // bundle all information in this activity to the next
                intent.putExtra("name", mName);
                intent.putExtra("major", mMajor);
                intent.putExtra("dob", mBirthdate);
                intent.putExtra("image_pfp", selectedImageBitmap);
                startActivity(intent);
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestExternalStoragePermission();
            }

        });

    }


    private void requestExternalStoragePermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Permission has already been granted, access external storage here
            imageChooser();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            // If the request is cancelled, the grantResults array is empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, access external storage here
                imageChooser();
            } else {
                // Permission has been denied, show a message to the user
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            // do your operation from here....
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    profileImg.setImageBitmap(selectedImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private void initializeUI() {
        nameField = findViewById(R.id.name);
        majorField = findViewById(R.id.major);
        birthdayField = findViewById(R.id.datePickerEditText);
        continueBtn = findViewById(R.id.continueButton);
        profileImg = findViewById(R.id.profileImage);
    }
}