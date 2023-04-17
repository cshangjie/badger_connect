package com.example.badgerconnect.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.badgerconnect.MessageActivity;
import com.example.badgerconnect.Model.User;
import com.example.badgerconnect.R;

import java.util.List;

//Where the list of users currently connected to the main users is generated
//this code collects the list of users from firebase and displays them on the main chat activity
//Next step will be to only display users who share a chat history with main user and when clicked
//will take main user to a chat page
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context mContext, List<User> mUsers){
        this.mContext=mContext;
        this.mUsers=mUsers;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView username;
        private ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            profile_image= itemView.findViewById(R.id.profile_image);

        }
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user= mUsers.get(position);
        holder.username.setText(user.getName());
        if(user.getProfile_pic().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.getProfile_pic()).into(holder.profile_image);
        }

        ///////
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getUid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
