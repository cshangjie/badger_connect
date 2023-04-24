package com.example.badgerconnect;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

enum Major {
    COMPUTER_ENGINEERING, COMPUTER_SCIENCE, ELECTRICAL_ENGINERING;
}
enum Year {
    Freshman(1), Sophomore(2), Junior(3), Senior(4);

    private int numVal;

    Year(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
enum MeetingType {
    IN_PERSON, VIRTUAL, BOTH;
}

@IgnoreExtraProperties
public class UserInfo {
    public String username;
    public String email;
    public String major;
    public HashMap<String, Boolean> connectionTypes = new HashMap<String, Boolean>() {{
        put("Mentor", false);
        put("Mentee", false);
        put("StudyBuddy", false);
    }};;
    public String bio;
    public Year year;
    public int numCourses;
    public HashMap<String, String> studyBuddyCourses = new HashMap<String, String>();

    public UserInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserInfo(String username, String email, String major,
                    List<String> connectionTypes, String bio, Year year) {
        this.username = username;
        this.email = email;
        this.major = major;
        for (String connectionType : connectionTypes) {
            this.connectionTypes.put(connectionType, true);
        }
        this.bio = bio;
        this.year = year;
    }

    public UserInfo(String username, String email, String major, int numCourses,
                    List<String> studyBuddyCourses, List<String> connectionTypes,
                    String bio, Year year) {
        this.email = email;
        this.username = username;
        this.major = major;
        this.numCourses = numCourses;
        for (int i = 1; i <= numCourses; i++) {
            this.studyBuddyCourses.put("Course"+i, studyBuddyCourses.get(i-1));
        }
        for (String connectionType : connectionTypes) {
            this.connectionTypes.put(connectionType, true);
        }
        this.bio = bio;
        this.year = year;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getMajor() {
        return major;
    }

    public HashMap<String, Boolean> getConnectionType() {
        return connectionTypes;
    }

    public String getBio() {
        return bio;
    }

    public Year getYear() {
        return year;
    }

    public int getNumCourses() {
        return numCourses;
    }

    public HashMap<String, String> getStudyBuddyCourses() {
        return studyBuddyCourses;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setConnectionType(HashMap<String, Boolean> connectionTypes) {
        this.connectionTypes = connectionTypes;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public void setNumCourses(int numCourses) {
        this.numCourses = numCourses;
    }

    public void setStudyBuddyCourses(HashMap<String, String> studyBuddyCourses) {
        this.studyBuddyCourses = studyBuddyCourses;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Email", email);
        result.put("Username", username);
        result.put("Major", major);
        result.put("Year", year.getNumVal());
        result.put("Bio", bio);
        result.put("StudyBuddyCourses", studyBuddyCourses);
        result.put("ConnectionTypes", connectionTypes);
        return result;
    }

}

