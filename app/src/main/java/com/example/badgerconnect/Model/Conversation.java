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
import java.util.HashMap;

public class Conversation {

    private Message msg;
    private ArrayList<String> participant_ids;


    public Conversation(Message msg, ArrayList<String> participant_ids) {
      this.msg=msg;
      this.participant_ids=participant_ids;
    }

  public void CreateNewConversation(){
      //instantiate db
      //access data-> conversation
      //push new conversation
      DatabaseReference convRef = FirebaseDatabase.getInstance().getReference("Data").child("Conversations");
      //final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

      HashMap<String, Object> convMap= new HashMap<>();
      convMap.put("Messages", msg);
      convMap.put("Participants", participant_ids);

      convRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot datasnapshot) {
              //System.out.println("DesConv ind is: " + desiredConvId);
              convRef.push().setValue(convMap);
          }
          @Override
          public void onCancelled(@NonNull DatabaseError error) {
          }
      });
  }
    public void DeleteConversation(String convId){
        DatabaseReference convRef = FirebaseDatabase.getInstance().getReference("Data").child("Conversations");
        //final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



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
