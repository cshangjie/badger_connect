package com.example.badgerconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class ProfileCreationActivity extends AppCompatActivity {
    private int mentor = -1;
    private int mentee = -1;
    private int studybuddy = -1;

    private CheckBox isLookingForMentorCB, notLookingForMentorCB, isMentorCB, notMentorCB, isStudyBuddyCB, notStudyBuddyCB;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);



        initializeUI();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        isLookingForMentorCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // user has checked yes on looking for mentor
            if (isChecked) {
                notLookingForMentorCB.setChecked(false);
                mentee = 1;
            }
        });
        notLookingForMentorCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // user has checked no on looking for mentor
            if (isChecked) {
                isLookingForMentorCB.setChecked(false);
                mentee = 0;
            }
        });

        isMentorCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // user has checked yes wanting to be Mentor
            if (isChecked) {
                notMentorCB.setChecked(false);
                mentor = 1;
            }
        });
        notMentorCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // user has checked no on wanting to be Mentor
            if (isChecked) {
                isMentorCB.setChecked(false);
                mentor = 0;
            }
        });

        isStudyBuddyCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // user has checked yes on looking for study buddy
            if (isChecked) {
                notStudyBuddyCB.setChecked(false);
                studybuddy = 1;
            }
        });
        notStudyBuddyCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // user has checked no on looking for study buddy
            if (isChecked) {
                isStudyBuddyCB.setChecked(false);
                studybuddy = 0;
            }
        });

//        public boolean onOptionsItemSelected(MenuItem item){
//            Intent myIntent = new Intent(getApplicationContext(), MyActivity.class);
//            startActivityForResult(myIntent, 0);
//            return true;
//        }



        // On-click, need to verify that the user has at least entered in one course, and then continue
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((mentor == -1) && (mentee == -1) && (studybuddy == -1)){
                    Toast.makeText(getApplicationContext(), "Please select preferences", Toast.LENGTH_SHORT).show();
                }else if(((mentor == 0) && (mentee == 0) && (studybuddy == 0))){
                    Toast.makeText(getApplicationContext(), "Please select yes to at least one category", Toast.LENGTH_SHORT).show();
                }
                // if the studybudy == 0 then just send them to the dashboard
                else if(studybuddy == 0){
                    Intent myIntent = new Intent(ProfileCreationActivity.this, DashboardActivity.class);
                    startActivity(myIntent);
                }
                // otherwise send them to a course selection page
                else{
                    Intent myIntent = new Intent(ProfileCreationActivity.this, ProfileCreationCourseInfoActivity.class);
                    startActivity(myIntent);
                }
            }
        });
    }

    private void initializeUI() {
        continueButton = findViewById(R.id.continue_button);
        isLookingForMentorCB = findViewById(R.id.yes_checkbox_1);
        notLookingForMentorCB = findViewById(R.id.no_checkbox_1);
        isMentorCB = findViewById(R.id.yes_checkbox_2);
        notMentorCB = findViewById(R.id.no_checkbox_2);
        isStudyBuddyCB = findViewById(R.id.yes_checkbox_3);
        notStudyBuddyCB = findViewById(R.id.no_checkbox_3);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            Intent myIntent = new Intent(ProfileCreationActivity.this, ProfileCreationGeneralInfoActivity.class);
            startActivity(myIntent);
        }
        return true;
    }
}
