package com.example.badgerconnect.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class User {

    private String Uid, Name, profile_pic;
    private ArrayList<String> Chat_ids;

    public User(){
        this.Uid="default";
        this.Name="default";
        this.profile_pic="default";
        this.Chat_ids=null;
    }

    public User(String id, String username, String imageURL){
        this.Uid=id;
        this.Name=username;
        this.profile_pic=imageURL;
        this.Chat_ids=null;
    }

    public User(String id, String username, String imageURL, ArrayList<String> Chat_ids){
        this.Uid=id;
        this.Name=username;
        this.profile_pic=imageURL;
        this.Chat_ids=Chat_ids;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public ArrayList<String> getChat_ids() {
        return Chat_ids;
    }

    public void setChat_ids(ArrayList<String> chat_ids) {
        Chat_ids = chat_ids;
    }

    //add new user ot remote database
    public void addUser(){
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference("Data").child("Users");
        HashMap<String, Object> map= new HashMap<>();
        map.put("Chat_ids", getChat_ids());
        map.put("Name", getName());
        map.put("Uid", getUid());
        map.put("profile_pic", getProfile_pic());

        UserRef.push().setValue(map);
    }

}
