package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterPlus extends RecyclerView.Adapter<RecyclerViewAdapterPlus.ViewHolder> {

    private List<FriendData> mFriendList = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerViewAdapterPlus.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendplusitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterPlus.ViewHolder holder, int position) {
        holder.onBind(mFriendList.get(position));
    }

    public void setFriendList(ArrayList<FriendData> list){
        this.mFriendList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mFriendList != null) {
            return mFriendList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        FirebaseUser user;
        TextView uid;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            uid = view.findViewById(R.id.uid);

        }

        void onBind(FriendData user){
            name.setText(user.getName());
            uid.setText(user.getUid());
        }
    }
}