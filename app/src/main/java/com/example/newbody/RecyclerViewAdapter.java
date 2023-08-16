package com.example.newbody;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<FriendData> originalUsers = new ArrayList<>();
    private List<FriendData> filteredUsers = new ArrayList<>();
    private FirebaseFirestore firestore;

    private FirebaseUser user;
    private FirebaseAuth auth;
    private String uid;
    private ListenerRegistration listenerRegistration;

    public RecyclerViewAdapter() {
        firestore = FirebaseFirestore.getInstance();
        // Firebase Firestore에서 데이터 변경을 감지하고 데이터를 불러오는 코드입니다.
        listenerRegistration = firestore.collection("users").addSnapshotListener((querySnapshot, e) -> {
            originalUsers.clear();

            if (querySnapshot != null) {
                for (QueryDocumentSnapshot snapshot : querySnapshot) {
                    FriendData item = snapshot.toObject(FriendData.class);
                    originalUsers.add(item);
                }
                performSearch("", ""); // 초기화 시 전체 데이터로 설정
            }
        });

    }

    // 검색 기능을 수행하는 메서드
    // 검색 기능을 수행하는 메서드
    public void performSearch(String searchWord, String option) {
        filteredUsers.clear();

        if (searchWord == null || option == null || searchWord.isEmpty() || option.isEmpty()) {
            // 검색어가 없을 때 전체 데이터를 불러옵니다.
            filteredUsers.addAll(originalUsers);
        } else {
            for (FriendData userData : originalUsers) {
                String name = userData.getName();
                String birth = userData.getBirth();
                String uid = userData.getUid(); // 친구의 고유 UID

                if (name != null && birth != null) {
                    // 검색 옵션에 따라 데이터를 필터링하여 추가합니다.
                    if (option.equals("name") && name.contains(searchWord)) {
                        FriendData friend = new FriendData(name, birth, uid); // 생성자에 데이터 추가
                        friend.setUid(uid);
                        filteredUsers.add(friend);
                        Log.d("DebugTag", "Added to filteredUsers: " + userData.getName()); // 로그 출력
                    } else if (option.equals("birth") && birth.contains(searchWord)) {
                        FriendData friend = new FriendData(name, birth, uid); // 생성자에 데이터 추가
                        friend.setUid(uid);
                        filteredUsers.add(friend);
                        Log.d("DebugTag", "Added to filteredUsers: " + userData.getName()); // 로그 출력
                    }
                }
            }
        }
        notifyDataSetChanged(); // 데이터 변경을 어댑터에 알려 화면을 갱신합니다.
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 아이템 뷰를 생성하는 메서드입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendData user = filteredUsers.get(position);
        Log.d("DebugTag", "FriendUid: " + user.getUid()); // 로그 출력
        holder.bind(user);
    }


    @Override
    public int getItemCount() {
        // 아이템 개수를 반환합니다.
        return filteredUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        FirebaseUser user;
        TextView uid;
        Button btnadd;

        public ViewHolder(View view) {
            super(view);
            // 뷰 홀더에서 아이템 뷰의 요소들을 초기화합니다.
            name = view.findViewById(R.id.name);
            uid = view.findViewById(R.id.uid);
            btnadd = view.findViewById(R.id.btnadd);
        }

        public void bind(FriendData user) {
            // 뷰 홀더에 데이터를 바인딩하여 화면에 표시합니다.
            name.setText(user.getName());
            uid.setText(user.getUid());

            btnadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String currentUid = currentUser.getUid(); // 현재 사용자의 UID

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // 상대방의 UID로 친구 문서를 추가
                        DocumentReference friendRef = db.collection("users")
                                .document(user.getUid()) // 상대방의 UID로 도큐먼트 접근
                                .collection("friends")
                                .document(currentUid); // 사용자 정의 문서 ID
                        Map<String, Object> friendData = new HashMap<>();

                        DocumentReference currentUserDocRef = db.collection("users").document(currentUid);
                        currentUserDocRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String currentUserName = documentSnapshot.getString("name");
                                String currentUserGender = documentSnapshot.getString("gender");
                                String currentUserHeight = documentSnapshot.getString("height");
                                String currentUserWeight = documentSnapshot.getString("weight");

                                friendData.put("name", currentUserName); // 현재 사용자의 이름
                                friendData.put("uid", currentUid);
                                friendData.put("gender", currentUserGender);
                                friendData.put("height", currentUserHeight);
                                friendData.put("weight", currentUserWeight);

                                // 나머지 정보도 필요한 속성에 맞게 추가

                                friendRef.set(friendData)
                                        .addOnSuccessListener(documentReference -> {
                                            // 상대방 친구 목록에 추가 성공 처리
                                        })
                                        .addOnFailureListener(e -> {
                                            // 상대방 친구 목록에 추가 실패 처리
                                        });

                                // 나의 친구 목록에 상대방을 추가
                                DocumentReference myFriendRef = db.collection("users")
                                        .document(currentUid) // 내 UID로 도큐먼트 접근
                                        .collection("friends")
                                        .document(user.getUid()); // 사용자 정의 문서 ID
                                Map<String, Object> myFriendData = new HashMap<>();
                                myFriendData.put("name", user.getName()); // 상대방의 이름
                                myFriendData.put("uid", user.getUid());
                                myFriendData.put("birth", user.getBirth());
                                myFriendData.put("gender", user.getGender());
                                myFriendData.put("height", user.getHeight());
                                myFriendData.put("weight", user.getWeight());
                                // 나머지 정보도 필요한 속성에 맞게 추가

                                myFriendRef.set(myFriendData)
                                        .addOnSuccessListener(documentReference -> {
                                            // 나의 친구 목록에 추가 성공 처리
                                        })
                                        .addOnFailureListener(e -> {
                                            // 나의 친구 목록에 추가 실패 처리
                                        });
                            }
                        }).addOnFailureListener(e -> {
                            // 데이터 가져오기 실패 처리
                        });
                    }
                }
            });
        }
    }
}