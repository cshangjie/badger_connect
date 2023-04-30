package com.example.badgerconnect.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.badgerconnect.Model.Chat;
import com.example.badgerconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/*
Where the list of users currently connected to the main users is generated
this code collects the list of users from firebase and displays them on the main chat activity
Next step will be to only display users who share a chat history with main user and when clicked
will take main user to a chat page
*/

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private Context mContext;
    private List<Chat> mChat;
    private String imageUrl;
    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChats, String imageUrl){
        this.mContext=mContext;
        this.mChat=mChats;
        this.imageUrl=imageUrl;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView show_message;
        private ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            profile_image= itemView.findViewById(R.id.profile_image);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    //called when about to display new item (message) on the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat= mChat.get(position);

        holder.show_message.setText(chat.getMessage());
        //holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        if(imageUrl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(imageUrl).into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals((fuser.getUid()))){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
