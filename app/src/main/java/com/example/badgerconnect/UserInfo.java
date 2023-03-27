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
    COMPUTER_ENGINEERING, COMPUTER_SCIENCE, ELECTRICAL_ENGINERING
}

enum MeetingType {
    IN_PERSON, VIRTUAL
}

enum Courses {
    ECE_454, ECE_755, ECE_552
}
@IgnoreExtraProperties
public class UserInfo {

    public String email;
    public String username;
    public String major;
    public String courses = "";
    public String meetingType;

    public UserInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserInfo(String username, String email, Major major, List<Courses> courses, MeetingType meetingType) {
        this.email = email;
        this.username = username;
        this.major = major.toString();
        for (int i = 0; i < courses.size(); i++) {
            if(i == (courses.size() - 1)) {
                this.courses += courses.get(i).toString();
            }
            else {
                this.courses += courses.get(i).toString();
                this.courses += ",";
            }
        }
        this.meetingType = meetingType.toString();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getMajor() {
        return major;
    }

    public String getCourses() {
        return courses;
    }

    public String getMeetingType() {
        return meetingType;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("username", username);
        result.put("major", major);
        result.put("courses", courses);
        result.put("meeting type", meetingType);
        return result;
    }

}

