package com.example.badgerconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.badgerconnect.Adapter.MessageAdapter;
import com.example.badgerconnect.Model.Chat;
import com.example.badgerconnect.Model.Conversation;
import com.example.badgerconnect.Model.Message;
import com.example.badgerconnect.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private String desiredConvId;
    CircleImageView profile_image;
    //TextView username;s
    ImageButton btn_send;
    EditText text_send;
    //MaterialEditText username;
    MessageAdapter messageAdapter;
    List<Chat> mChat;

    CircleImageView profileImageForMenu;
    TextView profile_username;
    RecyclerView recyclerView;

    FirebaseUser fuser;
    String curr_user;
    DatabaseReference reference;
    Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ///////////////////////
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ///////////////////////
        btn_send= findViewById(R.id.btn_send);
        text_send= findViewById(R.id.text_send);

        intent= getIntent();
        final String receiverId= intent.getStringExtra("userid"); //user who got passed from UserAdapter
        final String image_URL= intent.getStringExtra("image_URL");


        fuser= FirebaseAuth.getInstance().getCurrentUser(); //current user!
        curr_user=fuser.getUid();
        reference= FirebaseDatabase.getInstance().getReference("Data").child("Users");
        readMessages(fuser.getUid(), receiverId, image_URL);

        //send message when button is pressed
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg= text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(msg, fuser.getUid(), receiverId);
                }else {
                    Toast.makeText(MessageActivity.this, "Empty Message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

    }


//////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Data").child("Users");
        Query query= reference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {


                User user = new User();
                for (DataSnapshot userinfo : datasnapshot.getChildren()) {

                    user = userinfo.getValue(User.class);
                    if (user.getUid().equals(fuser.getUid())) {
                        if (!user.getProfile_pic().equals("default")) {
                            profileImageForMenu = findViewById(R.id.profile_image_icon);
                            profile_username = findViewById(R.id.profile_username);
                            MenuItem profileImageMenuItem=menu.findItem(R.id.profile_image_menu);
                            MenuItem username_menu=menu.findItem(R.id.username_menu);
//                            username_menu.setTitle(user.getName());
                            profile_username.setText(user.getName());

                            // Inflate the layout and set it as the action view for the menu item
                            View profileImageView = profileImageMenuItem.getActionView();
                            if (profileImageView != null) {
                                profileImageMenuItem.setActionView(profileImageView);
                            }

                            Glide.with(MessageActivity.this).load(user.getProfile_pic()).into(profileImageForMenu);

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
            case android.R.id.home: {
                this.finish();
            }

            return true;

        }

        return false;
    }

    // this event will enable the back
    // function to the button on press


    /*handles sending message to the firebase cloud
    //message should have conversation id, message, senderId,
    //date sent, generated messageId
    //conditions, handles one-on-one chats no group chats
    //both participants must be in the participant list
    //upi do not need to assign cid
    //@todo create new conversation instances
    //clean code
    */
    private void sendMessage(String messageToSend, String senderId, String receiverId){
        DatabaseReference convRef= FirebaseDatabase.getInstance().getReference("Data").child("Conversations");//give me desired conversation
        //find desired conversation using participant list
        String curr_user=fuser.getUid();
        Query ConvRefQuery = convRef;

        ConvRefQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("participants" + dataSnapshot.getValue());
                for (DataSnapshot convSnapshot : dataSnapshot.getChildren()) {
                    // check if both users are in the conversation
                   // System.out.println("participants" + convSnapshot.child("Participants").getValue());

                    desiredConvId=convSnapshot.getKey();
                    GenericTypeIndicator<HashMap<String, Object>> p2 = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    HashMap<String, Object> pMap=  convSnapshot.child("Participants").getValue(p2);

                    ArrayList<Object> participants= new ArrayList<>(pMap.values());
                    if(participants.contains(curr_user) && participants.contains(receiverId)) {
                      //  System.out.println("Found users in conv d:" + desiredConvId + " convindx: " + convSnapshot.getKey());
                        break;
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }

        });

        ///now we have our desired cnversation, time to add the message
        Date date= new Date();
        Message msg = new Message();
        msg.setDate(String.valueOf(date));
        msg.setText(messageToSend);
        msg.setSender(senderId);

//        TESTING CREATE conversation
//        ArrayList<String> participant_ids=new ArrayList<String>();
//        participant_ids.add("AAAA");
//        participant_ids.add("BBBBB");
//        Conversation conversation=new Conversation(msg, participant_ids);
//        //conversation.CreateNewConversation();
//        conversation.DeleteConversation("1");

        //push a message to that conversation
        ConvRefQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                //System.out.println("DesConv ind is: " + desiredConvId);
                convRef.child(""+ desiredConvId).child("Messages").push().setValue(msg);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void readMessages(String senderId, String receiverId, String imageurl){
        mChat = new ArrayList<>();

        DatabaseReference convRefer= FirebaseDatabase.getInstance().getReference("Data").child("Conversations");
//        Query query=convRefer.orderByKey().equalTo()
       // System.out.println(" in ccc " + convRefer);
        convRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mChat.clear();
               // System.out.println(" in datachange " + datasnapshot.getValue());
                for (DataSnapshot convSnapshot : datasnapshot.getChildren()) {
                    //System.out.println(" in datachange " + convSnapshot.getValue());

                    GenericTypeIndicator<HashMap<String, Object>> p3 = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    HashMap<String, Object> pMap= convSnapshot.child("Participants").getValue(p3);

                    ArrayList<Object> participants= new ArrayList<>(pMap.values());

                    // check if both users are in the conversation
                     System.out.println("p is:" + curr_user + " and " + receiverId);
                     System.out.println("px is:" + participants);
                    if(participants.contains(curr_user) && participants.contains(receiverId)) {
//                        System.out.println("p is:" + curr_user + " and " + receiverId);
//                        System.out.println("px is:" + participants);
                      //deserialize the chat and add it to mChat
                        for(DataSnapshot msgSnapshot: convSnapshot.child("Messages").getChildren()){
                            Chat chat= new Chat();
                            chat.setMessage(msgSnapshot.child("text").getValue(String.class));
                            chat.setDate(msgSnapshot.child("date").getValue(String.class));
                            chat.setSender(msgSnapshot.child("sender").getValue(String.class));
                            mChat.add(chat);
                        }
                    }
                }

                messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                recyclerView.setAdapter(messageAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}