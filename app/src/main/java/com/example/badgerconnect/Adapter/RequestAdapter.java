package com.example.badgerconnect.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.badgerconnect.Fragments.PendingRequestsFragment;
import com.example.badgerconnect.MessageActivity;
import com.example.badgerconnect.Model.Conversation;
import com.example.badgerconnect.Model.Request;
import com.example.badgerconnect.Model.User;
import com.example.badgerconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//should extend userAdapter and baseically do all that useradaper does only difference
//should be onclick which should show an option to connect with the sender
//TODO Make list of request automatically reload. Might have something to do with not clearing musers correctly
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

        private Context mContext;
        private ArrayList<String> participant_ids= new ArrayList<>();
        private List<User> mUsers;

        public RequestAdapter(Context mContext, List<User> mUsers){
            this.mContext=mContext;
            this.mUsers=mUsers;
        }


    public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView username;

            private ImageView profile_image;
            private Button accept_btn;
            private Button decline_btn;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                username=itemView.findViewById(R.id.username);
                profile_image= itemView.findViewById(R.id.profile_image);
                accept_btn=itemView.findViewById(R.id.accept_btn);
                decline_btn=itemView.findViewById(R.id.decline_btn);
            }
        }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.pending_user_item, parent, false);

            return new RequestAdapter.ViewHolder(view);
        }


    @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getName());
        if (user.getProfile_pic().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getProfile_pic()).into(holder.profile_image);
        }
        String sender_id = user.getUid();
        FirebaseAuth auth = FirebaseAuth.getInstance();;
        String currUserId = auth.getCurrentUser().getUid();


        holder.accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //remove sender_id from recipient's list
                DatabaseReference RequestDataRef = FirebaseDatabase.getInstance().getReference("Data").child("Pending_Connection_Requests");

                RequestDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        for (DataSnapshot recipientJson : datasnapshot.getChildren()) {
                            //System.out.println("ddddd " + datasnapshot.getValue());
                            if (recipientJson.getKey().equals(currUserId)){

                                DatabaseReference recipientRef=RequestDataRef.child(""+currUserId);
                                recipientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        recipientRef.child(""+sender_id).removeValue();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                              // RequestDataaRef.child(""+currUserId).child(""+sender_id).removeValue(); //delete sender ID
                                //start conversation between both
                                HashMap<String, String> map= new HashMap<>();
                                map.put(sender_id, sender_id);
                                map.put(currUserId, currUserId);
                                Conversation conversation= new Conversation(map);
                                conversation.CreateNewConversation();

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.decline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //remove sender_id from recipient's list
                DatabaseReference RequestDataRef = FirebaseDatabase.getInstance().getReference("Data").child("Pending_Connection_Requests");

                RequestDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        for (DataSnapshot recipientJson : datasnapshot.getChildren()) {
                            if (recipientJson.getKey().equals(currUserId)){
                                DatabaseReference recipientRef=RequestDataRef.child(""+currUserId);
                                recipientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        recipientRef.child(""+sender_id).removeValue();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        /////// display profile if it's clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                    Intent intent = new Intent(mContext, MessageActivity.class);
//                    intent.putExtra("userid", user.getUid());
//                    intent.putExtra("image_URL", user.getProfile_pic());
//                    mContext.startActivity(intent);

            }
        });


    };

        @Override
        public int getItemCount() {
            return mUsers.size();
        }
}


