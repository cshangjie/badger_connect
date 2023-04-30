package com.example.badgerconnect.Model;

import java.util.ArrayList;

public class Request {

    public Request(){

        this.request_ids=new ArrayList<>();
        this.senders_id=null;
        this.recipients_id=null;
    }
    public Request(ArrayList<String> request_ids) {
        this.request_ids = request_ids;
    }

    public void SendRequest(String senders_id, String recipients_id){
        this.senders_id=senders_id;
        this.recipients_id=recipients_id;

        //create a new conversation on firebase
        //add sender id and recipients id to pending requests table

    }

    private ArrayList<String> request_ids;
    private String senders_id, recipients_id;


}
