package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.newbody.FriendData;
import com.example.newbody.R;
import com.example.newbody.RecyclerViewAdapterPlus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FriendListPlus extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapterPlus mRecyclerAdapter;
    private FirebaseFirestore db;

    private Button FriendRequestBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list11);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerAdapter = new RecyclerViewAdapterPlus();

        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        db = FirebaseFirestore.getInstance();
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(currentUid)
                .collection("friends")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<FriendData> friendList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        FriendData friend = document.toObject(FriendData.class);
                        friendList.add(friend);
                    }
                    mRecyclerAdapter.setFriendList(friendList);
                })
                .addOnFailureListener(e -> {
                    // 실패 처리
                });

    }
}