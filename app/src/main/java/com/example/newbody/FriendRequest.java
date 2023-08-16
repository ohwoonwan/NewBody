package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.newbody.FriendData;
import com.example.newbody.R;
import com.example.newbody.RecyclerViewAdapterRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FriendRequest extends AppCompatActivity {

    private RecyclerView rRecyclerView;
    private RecyclerViewAdapterRequest rRecyclerAdapterRequest;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        rRecyclerView = findViewById(R.id.recyclerView);
        rRecyclerAdapterRequest = new RecyclerViewAdapterRequest();

        rRecyclerView.setAdapter(rRecyclerAdapterRequest);

        // 레이아웃 매니저 설정 (LinearLayoutManager 사용)
        rRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String currentUid = currentUser.getUid();

            // 사용자의 친구 요청 목록 불러오기
            db.collection("users").document(currentUid)
                    .collection("friend_requests")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        ArrayList<FriendData> friendRequestList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            FriendData friendRequest = document.toObject(FriendData.class);
                            friendRequestList.add(friendRequest);
                        }
                        rRecyclerAdapterRequest.setFriendList(friendRequestList);

                    })
                    .addOnFailureListener(e -> {
                        // 실패 처리
                    });
        }
    }
}
