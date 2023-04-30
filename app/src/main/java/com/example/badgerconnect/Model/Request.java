package com.example.badgerconnect.Model;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private ArrayList<String> request_ids;
    private String senders_id, recipients_id;

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

        //add sender id and recipients id to pending requests table
        DatabaseReference RequestDataRef = FirebaseDatabase.getInstance().getReference("Data").child("Pending_Connection_Requests");

        RequestDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check to see if recipient id json structure alreadly exists. create a new one if it does not
               if( snapshot.getValue().toString().contains(recipients_id) ){ //HACKY but will do for now
                    RequestDataRef.child("" + recipients_id).updateChildren(Map.of(""+senders_id, ""));
                } else{
                   RequestDataRef.updateChildren(Map.of("" + recipients_id, Map.of(""+senders_id, "" )));
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




}
