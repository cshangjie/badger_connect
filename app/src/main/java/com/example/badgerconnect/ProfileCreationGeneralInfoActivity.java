package com.example.badgerconnect;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
                if(mName.isBlank()){
                    nameField.setError("Name is required!");
                    nameField.requestFocus();
                    return;
                }
                if(mMajor.isBlank()){
                    majorField.setError("Major is required!");
                    majorField.requestFocus();
                    return;
                }
                if(dateString.isBlank()){
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
                } else if (now.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                        && now.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
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
                startActivity(intent);
            }
        });

    }

    private void initializeUI() {
        nameField = findViewById(R.id.name);
        majorField = findViewById(R.id.major);
        birthdayField = findViewById(R.id.datePickerEditText);
        continueBtn = findViewById(R.id.continueButton);
    }
}