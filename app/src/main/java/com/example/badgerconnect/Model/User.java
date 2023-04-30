package com.example.badgerconnect.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

}
