package com.example.newbody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapterPlus extends RecyclerView.Adapter<com.example.newbody.RecyclerViewAdapterPlus.ViewHolder> {

    private List<FriendData> mFriendList = new ArrayList<>();
    private Map<String, Boolean> selectedItems = new HashMap<>();

    @NonNull
    @Override
    public com.example.newbody.RecyclerViewAdapterPlus.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendplusitem, parent, false);
        return new com.example.newbody.RecyclerViewAdapterPlus.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.newbody.RecyclerViewAdapterPlus.ViewHolder holder, int position) {
        holder.onBind(mFriendList.get(position));
    }

    public void setFriendList(ArrayList<FriendData> list) {
        Collections.sort(list, (user1, user2) -> user1.getName().compareTo(user2.getName()));
        this.mFriendList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        FirebaseUser user;
        TextView uid;
        ImageView imageUrl;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            uid = view.findViewById(R.id.uid);
            imageUrl = view.findViewById(R.id.profile);
            checkBox = view.findViewById(R.id.checkbox);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String uid = mFriendList.get(position).getUid();
                    if (isChecked) {
                        selectedItems.put(uid, true);
                    } else {
                        selectedItems.remove(uid);
                    }
                }
            });
        }

        void onBind(FriendData user) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());

            // 기본 이미지를 미리 설정
            imageUrl.setImageResource(R.drawable.baseline_person_24);
            checkBox.setChecked(selectedItems.containsKey(user.getUid()));

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String imageUrl = documentSnapshot.getString("imageUrl");
                    user.setImageUrl(imageUrl);
                    // 이미지 로딩
                    if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                        Picasso.get()
                                .load(user.getImageUrl())
                                .placeholder(R.drawable.baseline_person_24)
                                .error(R.drawable.baseline_person_24)
                                .into(this.imageUrl);
                    }
                }
            });

            name.setText(user.getName());
            String maskedUID = maskUID(user.getUid());
            uid.setText(maskedUID);
        }

    }
    private String maskUID(String uid) {
        if (uid == null || uid.length() <= 6) {
            return uid; // UID가 null이거나 6글자 이하일 경우 가려지지 않은 UID를 반환
        }

        String masked = uid.substring(0, 6); // 앞 6글자를 유지
        for (int i = 6; i < uid.length(); i++) {
            masked += "*"; // 나머지 자리는 '*'로 대체
        }

        return masked;
    }

    public void deleteSelectedItems(String currentUserUid) {
        for (String uid : selectedItems.keySet()) {
            // Firestore에서 데이터 삭제 처리
            deleteUserDataAndFriend(currentUserUid, uid);
        }

        // 선택된 아이템 목록에서 삭제된 아이템 제거
        List<FriendData> newFriendList = new ArrayList<>();
        for (FriendData friend : mFriendList) {
            if (!selectedItems.containsKey(friend.getUid())) {
                newFriendList.add(friend);
            }
        }

        Log.d("NewFriendList", "New friend list after deletion: " + newFriendList.toString());

        mFriendList = newFriendList;
        selectedItems.clear();

        notifyDataSetChanged(); // RecyclerView 갱신 추가

        // notifyDataSetChanged() 호출 이후에도 Firebase에서 데이터를 삭제하는 로직은 그대로 두세요.
    }

    private void deleteUserDataAndFriend(String currentUserUid, String friendUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 상대방의 친구 목록에서 현재 사용자 삭제
        deleteFromFriendFriendList(friendUid, currentUserUid);

        // 선택된 아이템 목록에서 삭제된 아이템 제거
        List<FriendData> newFriendList = new ArrayList<>();
        for (FriendData friend : mFriendList) {
            if (!selectedItems.containsKey(friend.getUid())) {
                newFriendList.add(friend);
            }
        }

        Log.d("NewFriendList", "New friend list after deletion: " + newFriendList.toString());

        mFriendList = newFriendList;
        selectedItems.clear();

        notifyDataSetChanged();
    }


    private void deleteFromFriendFriendList(String friendUid, String currentUserUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 상대방의 친구 목록에서 현재 사용자 삭제
        db.collection("users").document(friendUid)
                .collection("friends")
                .document(currentUserUid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteFriendSuccess", "Friend relationship deleted successfully from friend's list.");

                    // 선택된 아이템 목록에서 삭제된 아이템 제거
                    List<FriendData> newFriendList = new ArrayList<>();
                    for (FriendData friend : mFriendList) {
                        if (!selectedItems.containsKey(friend.getUid())) {
                            newFriendList.add(friend);
                        }
                    }

                    Log.d("NewFriendList", "New friend list after deletion: " + newFriendList.toString());

                    mFriendList = newFriendList;
                    selectedItems.clear();

                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteFriendFailure", "Friend relationship deletion failed from friend's list: " + e.getMessage());
                });

        // 여기서 friendUid에 해당하는 문서를 삭제하는 로직을 추가하세요.
        db.collection("users").document(currentUserUid)
                .collection("friends")
                .document(friendUid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DeleteFriendSuccess", "Friend relationship deleted successfully from user's list.");
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteFriendFailure", "Friend relationship deletion failed from user's list: " + e.getMessage());
                });
    }




}
