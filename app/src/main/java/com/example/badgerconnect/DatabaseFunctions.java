package com.example.badgerconnect;

import androidx.annotation.NonNull;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseFunctions{

    private DatabaseReference mDatabase;

//    //Class for testing other methods
//    public void sendMessage(View view) {
//        String name = "Sahas Gelli";
//        String email = "sahasgelli@gmail.com";
//        String userId = "000001";
//        MeetingType meetingType1 = MeetingType.IN_PERSON;
//        Major major1 = Major.COMPUTER_ENGINEERING;
//        MeetingType meetingType2 = MeetingType.VIRTUAL;
//        Major major2 = Major.ELECTRICAL_ENGINERING;
//        List<Courses> courses = new ArrayList<>();
//        courses.add(Courses.ECE_454);
//        courses.add(Courses.ECE_755);
//        //writeNewUser(userId, name, email, major1, courses, meetingType1);
//        //updateUser(userId, "", "", major2, courses, meetingType2);
//        readUserData(userId);
//        //deleteUser(userId);
//    }
//
//    public void deleteMessage(View view) {
//        String userId = "000001";
//        deleteUser(userId);
//    }

    public void writeNewUser(String userId, String name, String email, Major major, List<Courses> courses, MeetingType meetingType) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        String key = userId;
        UserInfo user = new UserInfo(name, email, major, courses, meetingType);
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user_info/" + key, userValues);

        mDatabase.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase", "Data updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("firebase", "Error getting data");
                    }
                });

    }

    public void updateUser(String userId, String name, String email, Major major, List<Courses> courses, MeetingType meetingType) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        String key = userId;
        Map<String, Object> childUpdates = new HashMap<>();

        UserInfo user = new UserInfo(name, email, major, courses, meetingType);

        if(!user.getUsername().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/username/", user.getUsername());
        }
        if(!user.getEmail().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/email/", user.getEmail());
        }
        if(!user.getMajor().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/major/", user.getMajor());
        }
        if(!user.getCourses().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/courses/", user.getCourses());
        }
        if(!user.getMeetingType().isEmpty()) {
            childUpdates.put("/user_info/" + key + "/meeting type/", user.getMeetingType());
        }

        mDatabase.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)    {
                        Log.d("firebase", "Data updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("firebase", "Error getting data");
                    }
                });

    }

    public void readUserData(String userId) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("user_info").child(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            afterRead(task);
                        }
                    }
                });
    }

    public void afterRead(Task<DataSnapshot> task) {
        String name = String.valueOf(task.getResult().child("username").getValue());
        String email = String.valueOf(task.getResult().child("email").getValue());
        Major major = Major.valueOf(String.valueOf(task.getResult().child("major").getValue()));
        MeetingType meetingType = MeetingType.valueOf(String.valueOf(task.getResult().child("meeting type").getValue()));
        String[] list_courses = String.valueOf(task.getResult().child("courses").getValue()).split(",");
        List<Courses> courses = new ArrayList<>();
        for(String course : list_courses) {
            courses.add(Courses.valueOf(course));
        }
        UserInfo user = new UserInfo(name, email, major, courses, meetingType);
    }

    public void deleteUser(String userId) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Data");
        mDatabase.child("user_info").child(userId).removeValue();
    }
}

