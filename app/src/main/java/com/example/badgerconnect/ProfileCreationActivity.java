package com.example.badgerconnect;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Dialog;
import android.app.DialogFragment;


public class ProfileCreationActivity extends AppCompatActivity {
    private int mentor = -1;
    private int mentee = -1;
    private int studybuddy = -1;

    private boolean physical, virtual;

    private CheckBox isLookingForMentorCB, notLookingForMentorCB, isMentorCB, notMentorCB, isStudyBuddyCB, notStudyBuddyCB;
    private CheckBox physicalCB, virtualCB;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String major = intent.getStringExtra("major");
        String year = intent.getStringExtra("year");
        String dob = intent.getStringExtra("dob");
        String bio = intent.getStringExtra("bio");
        Bitmap imagePfp = (Bitmap) intent.getParcelableExtra("image_pfp");

        setContentView(R.layout.activity_profile_creation);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);



        initializeUI();

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

        physicalCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the boolean variable based on whether the checkbox is checked or unchecked
                physical = isChecked;
            }
        });
        virtualCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the boolean variable based on whether the checkbox is checked or unchecked
                virtual = isChecked;
            }
        });



        // On-click, need to verify that the user has at least entered in one course, and then continue
        continueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(physical == false && virtual == false){
                    Toast.makeText(getApplicationContext(), "Please select at least one meeting preference", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((mentor == -1) && (mentee == -1) && (studybuddy == -1)){
                    Toast.makeText(getApplicationContext(), "Please select connection preferences", Toast.LENGTH_SHORT).show();
                    return;
                }else if(((mentor == 0) && (mentee == 0) && (studybuddy == 0))){
                    Toast.makeText(getApplicationContext(), "Please select yes to at least one category", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if the studybudy == 0 create user and then just send them to the dashboard TODO
                else if(studybuddy == 0){
                    List<String> connectionTypes = new ArrayList<>();
                    if(mentor == 1){
                        connectionTypes.add("Mentor");
                    }
                    if(mentee == 1){
                        connectionTypes.add("Mentee");
                    }
                    Intent myIntent = new Intent(ProfileCreationActivity.this, ApplicationWrapperActivity.class);
                    MeetingType userMeetingPref;
                    if(physical && virtual){
                        userMeetingPref = MeetingType.BOTH;
                    }else if(physical){
                        userMeetingPref = MeetingType.IN_PERSON;
                    }
                    else{
                        userMeetingPref = MeetingType.VIRTUAL;
                    }
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String uid = currentUser.getUid();
                    String email = currentUser.getEmail();
                    // Write new user
                    DatabaseFunctions.writeNewUser(uid, name, email, major, connectionTypes, bio, Year.valueOf(year), userMeetingPref, dob);
                    // Upload PFP
                    DatabaseFunctions.uploadPFP(uid, imagePfp);
                    // TODO create user for CJ
                    startActivity(myIntent);
                }
                // otherwise send them to a course selection page
                else{
                    Intent myIntent = new Intent(ProfileCreationActivity.this, ProfileCreationCourseInfoActivity.class);
                    // pack all needed information
                    myIntent.putExtra("name", name);
                    myIntent.putExtra("major", major);
                    myIntent.putExtra("year", year);
                    myIntent.putExtra("dob", dob);
                    myIntent.putExtra("bio", bio);

                    // build connection types
                    List<String> connectionTypes = new ArrayList<>();
                    if(mentor == 1){
                        connectionTypes.add("Mentor");
                    }
                    if(mentee == 1){
                        connectionTypes.add("Mentee");
                    }
                    if(studybuddy == 1){
                        connectionTypes.add("StudyBuddy");
                    }
                    myIntent.putStringArrayListExtra("connectionType", (ArrayList<String>) connectionTypes);
                    myIntent.putExtra("physical", physical);
                    myIntent.putExtra("virtual", virtual);
                    myIntent.putExtra("image_pfp", imagePfp);
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
        physicalCB = findViewById(R.id.physicalCheckbox);
        virtualCB = findViewById(R.id.virtualCheckbox);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            Intent myIntent = new Intent(ProfileCreationActivity.this, ProfileCreationGeneralInfoActivity.class);
            startActivity(myIntent);
        }
        return true;
    }

    public void onTextEntered(String inputText) {
        // Do something with the input text
        Toast.makeText(this, "You entered: " + inputText, Toast.LENGTH_SHORT).show();
    }

}
