package com.example.badgerconnect.Model;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Conversation {

    private Message msg;
    private Map<String, String> participant_ids;


    public Conversation(Message msg, Map<String, String> participant_ids) {
      this.msg=msg;
      this.participant_ids=participant_ids;
    }
    public Conversation( Map<String, String> participant_ids) {
        Date date= new Date();
        this.msg=new Message();
        this.msg.setText("New Connection Established!");
        this.msg.setSender(participant_ids.get(0));
        this.msg.setDate(String.valueOf(date));
        this.participant_ids=participant_ids;
    }

    //instantiate db
    //access data-> conversation
    //push new conversation
  public void CreateNewConversation(){
      DatabaseReference convRef = FirebaseDatabase.getInstance().getReference("Data").child("Conversations");
      HashMap<String, Object> convMap= new HashMap<>();
      convMap.put("Messages", msg);
      convMap.put("Participants", participant_ids);
      convRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot datasnapshot) {
              convRef.push().setValue(convMap);
          }
          @Override
          public void onCancelled(@NonNull DatabaseError error) {
          }
      });
  }
    public void DeleteConversation(String convId){
        DatabaseReference convRef = FirebaseDatabase.getInstance().getReference("Data").child("Conversations");
        convRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                convRef.child(""+ convId).removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
