package com.example.newbody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagerMember extends AppCompatActivity {

    private CheckBox entire, free, money;
    private Button prev, search;

    private RecyclerView memberRecyclerView;
    private MemberAdaptor adapter;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private List<MemberItem> memberList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_member);

        prev = findViewById(R.id.prevButtonMemberManage);
        entire = findViewById(R.id.entire);
        free = findViewById(R.id.freeMember);
        money = findViewById(R.id.moneyMember);
        search = findViewById(R.id.searchMember);
        memberRecyclerView = findViewById(R.id.memberRecyclerView);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManagerMenu.class);
                startActivity(intent);
                finish();
            }
        });

        entire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(entire.isChecked()){
                    free.setChecked(true);
                    money.setChecked(true);
                }else if(!entire.isChecked()){
                    free.setChecked(false);
                    money.setChecked(false);
                }
            }
        });

        free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!free.isChecked()){
                    entire.setChecked(false);
                }else if(free.isChecked() && money.isChecked()){
                    entire.setChecked(true);
                }
            }
        });

        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!money.isChecked()){
                    entire.setChecked(false);
                }else if(free.isChecked() && money.isChecked()){
                    entire.setChecked(true);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseFirestore.getInstance();
                memberRecyclerView.setLayoutManager(new LinearLayoutManager(ManagerMember.this));

                fetchData();
                adapter = new MemberAdaptor(memberList);
                memberRecyclerView.setAdapter(adapter);
            }
        });
    }

    private void fetchData() {
        memberList.clear();

        if(entire.isChecked()){
            db.collection("users").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String name = document.getString("name");
                            String birth = document.getString("birth");
                            String gender = document.getString("gender");
                            String grade = document.getString("grade");

                            if (name == null) name = "정보없음";
                            if (birth == null) birth = "정보없음";
                            if (gender == null) gender = "정보없음";
                            if (grade == null) grade = "일반";

                            if(gender.equals("M") || gender.equals("남") || gender.equals("m") ||
                                    gender.equals("male") || gender.equals("xy") || gender.equals("man") || gender.equals("XY")){
                                gender = "남자";
                            }else if(gender.equals("W") || gender.equals("여") || gender.equals("w") ||
                                    gender.equals("female") || gender.equals("xx") || gender.equals("women") || gender.equals("XX")){
                                gender = "여자";
                            }

                            if(!name.equals("정보없음") || !birth.equals("정보없음") || !gender.equals("정보없음")){
                                memberList.add(new MemberItem(name, birth, gender, grade));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // 에러 처리
                    Log.d("Firestore Error", "Error getting documents: ", task.getException());
                }
            });
        }else if(money.isChecked() && !entire.isChecked()){
            db.collection("users").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String name = document.getString("name");
                            String birth = document.getString("birth");
                            String gender = document.getString("gender");
                            String grade = document.getString("grade");

                            if (name == null) name = "정보없음";
                            if (birth == null) birth = "정보없음";
                            if (gender == null) gender = "정보없음";
                            if (grade == null) grade = "일반";

                            if(gender.equals("M") || gender.equals("남") || gender.equals("m") ||
                                    gender.equals("male") || gender.equals("xy") || gender.equals("man") || gender.equals("XY")){
                                gender = "남자";
                            }else if(gender.equals("W") || gender.equals("여") || gender.equals("w") ||
                                    gender.equals("female") || gender.equals("xx") || gender.equals("women") || gender.equals("XX")){
                                gender = "여자";
                            }

                            if(grade.equals("프리미엄") && (!name.equals("정보없음") || !birth.equals("정보없음") || !gender.equals("정보없음"))){
                                memberList.add(new MemberItem(name, birth, gender, grade));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // 에러 처리
                    Log.d("Firestore Error", "Error getting documents: ", task.getException());
                }
            });
        }else if(free.isChecked() && !entire.isChecked()){
            db.collection("users").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String name = document.getString("name");
                            String birth = document.getString("birth");
                            String gender = document.getString("gender");
                            String grade = document.getString("grade");

                            if (name == null) name = "정보없음";
                            if (birth == null) birth = "정보없음";
                            if (gender == null) gender = "정보없음";
                            if (grade == null) grade = "일반";

                            if(gender.equals("M") || gender.equals("남") || gender.equals("m") ||
                                    gender.equals("male") || gender.equals("xy") || gender.equals("man") || gender.equals("XY")){
                                gender = "남자";
                            }else if(gender.equals("W") || gender.equals("여") || gender.equals("w") ||
                                    gender.equals("female") || gender.equals("xx") || gender.equals("women") || gender.equals("XX")){
                                gender = "여자";
                            }

                            if(grade.equals("일반") && (!name.equals("정보없음") || !birth.equals("정보없음") || !gender.equals("정보없음"))){
                                memberList.add(new MemberItem(name, birth, gender, grade));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // 에러 처리
                    Log.d("Firestore Error", "Error getting documents: ", task.getException());
                }
            });
        }
    }

    static class MemberItem {
        String name;
        String birth;
        String gender;
        String grade;

        public MemberItem(String name, String birth, String gender, String grade) {
            this.name = name;
            this.birth = birth;
            this.gender = gender;
            this.grade = grade;
        }
    }
}