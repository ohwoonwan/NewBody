package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterRequest extends RecyclerView.Adapter<RecyclerViewAdapterRequest.ViewHolder> {

    private List<FriendData> mFriendList = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerViewAdapterRequest.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_requestitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
        holder.onBind(mFriendList.get(position));
    }

    public void setFriendList(ArrayList<FriendData> list) {
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
        Button acceptButton;
        Button rejectButton;
        private FirebaseFirestore db;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            uid = view.findViewById(R.id.uid);
            acceptButton = view.findViewById(R.id.acceptButton);
            rejectButton = view.findViewById(R.id.rejectButton);
        }

        void onBind(FriendData user) {
            name.setText(user.getName());
            uid.setText(user.getUid());
            acceptButton.setOnClickListener(v -> {
                acceptFriendRequest(user);
            });

            rejectButton.setOnClickListener(v -> {
                rejectFriendRequest(user.getUid());
            });
        }

        private void acceptFriendRequest(FriendData friend) {
            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String friendRequestDocId = friend.getUid() + "_" + currentUserUid;

            // 친구 요청 데이터 업데이트
            DocumentReference friendRequestRef = db.collection("friend_requests")
                    .document(friendRequestDocId);
            friendRequestRef.delete() // 친구 요청 삭제
                    .addOnSuccessListener(aVoid -> {
                        // 요청 삭제 성공 처리

                        // 상대방 친구 목록에 추가
                        DocumentReference friendRef = db.collection("users")
                                .document(currentUserUid)
                                .collection("friends")
                                .document(friend.getUid());

                        friendRef.set(friend) // 상대방 정보 추가
                                .addOnSuccessListener(documentReference -> {
                                    // 친구 추가 성공 처리
                                    Log.d("FriendRequest", "Friend request accepted for: " + friend.getName());
                                })
                                .addOnFailureListener(e -> {
                                    // 친구 추가 실패 처리
                                    Log.e("FriendRequest", "Failed to add friend", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        // 요청 삭제 실패 처리
                        Log.e("FriendRequest", "Failed to accept friend request", e);
                    });
        }


        private void rejectFriendRequest(String senderUid) {
            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 친구 요청 데이터 삭제
            DocumentReference friendRequestRef = db.collection("friend_requests")
                    .document(senderUid + "_" + currentUserUid);
            friendRequestRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // 성공 처리
                    })
                    .addOnFailureListener(e -> {
                        // 실패 처리
                    });

        }

    }
};