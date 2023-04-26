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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


public class ProfileCreationGeneralInfoActivity extends AppCompatActivity {
    // declarations
    private EditText nameField;
    private AutoCompleteTextView majorField;
    private EditText birthdayField;
    private Button continueBtn;
    private ImageView profileImg;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private Bitmap selectedImageBitmap = null;
    private Spinner yearSpinner;
    private String yearSelected;
    private String userBio = "";

    private String mName, mMajor;
    private Date mBirthdate = null;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation_general_info);
        initializeUI();


        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check the birthdate, name and major for strings.
                mName = nameField.getText().toString().trim();
                mMajor = majorField.getText().toString().trim();
                String dateString = birthdayField.getText().toString();
//                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

                try {
                    mBirthdate = format.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // if any are null/empty -- show the corresponding error msg
                // check the profile picture
                if (selectedImageBitmap == null) {
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

                // major validity checks
                String[] majorList = getResources().getStringArray(R.array.bs_majors);
                Set<String> majorSet = new HashSet<>(Arrays.asList(majorList));
                if (!majorSet.contains(mMajor)) {
                    majorField.requestFocus();
                    majorField.setError("Invalid major");
                    return;

                }
                if (yearSelected == null || yearSelected.isBlank() || yearSelected.isEmpty()) {
                    yearSpinner.requestFocus();
                    Toast.makeText(getApplicationContext(), "Please select your current year", Toast.LENGTH_SHORT).show();
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
                // prompt for bio
                BioDialogFragment dialog = new BioDialogFragment();
                dialog.show(getSupportFragmentManager(), "TextEntryDialog");

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

    public void onTextEntered(String inputText) {
        if (inputText == null) {
            userBio = null;
            // do nothing
        } else {
            if (inputText.isBlank() || inputText.isEmpty()) {
                userBio = "";
            } else {
                userBio = inputText;
            }

            // Passed all information Validation, send the information to the next activity.
            Intent intent = new Intent(ProfileCreationGeneralInfoActivity.this, ProfileCreationActivity.class);
            // bundle all information in this activity to the next
            String bday = format.format(mBirthdate);
            intent.putExtra("name", mName);
            intent.putExtra("major", mMajor);
            intent.putExtra("dob", bday);
            intent.putExtra("year", yearSelected);
            intent.putExtra("bio", userBio);
            intent.putExtra("image_pfp", selectedImageBitmap);
            Log.i("Intent Extras", mName + "\n" + mMajor + "\n" + bday + "\n" + yearSelected + "\n" + userBio);
            startActivity(intent);


        }
//        Log.i("123123123123123123", inputText);
    }

    private void initializeUI() {
        nameField = findViewById(R.id.name);
        majorField = findViewById(R.id.major);
        ArrayAdapter<CharSequence> majorAdapter = ArrayAdapter.createFromResource(this, R.array.bs_majors, android.R.layout.simple_dropdown_item_1line);
        majorField.setAdapter(majorAdapter);


        birthdayField = findViewById(R.id.datePickerEditText);
        continueBtn = findViewById(R.id.continueButton);
        profileImg = findViewById(R.id.profileImage);

        yearSpinner = findViewById(R.id.year_spinner);
        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this, R.array.years_array, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(spinAdapter);
    }
}