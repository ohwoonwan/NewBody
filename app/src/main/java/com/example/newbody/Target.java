package com.example.newbody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Target extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    private Button prev, change;
    private boolean checkChange = true;
    private RecyclerView targetRecyclerView;
    private TextView []exName = new TextView[5];
    private EditText []ex = new EditText[5];
    private TextView []exNum = new TextView[5];
    private Switch []switches = new Switch[5];
    private List<TargetItem> targetList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        initViews();
        targetRecyclerView.setLayoutManager(new LinearLayoutManager(Target.this));

        startTarget(user);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkChange){
                    changeOn();
                    checkChange = false;
                }else{
                    changeOff();
                    checkChange = true;
                }
            }
        });
    }
    public void initViews(){
        prev = findViewById(R.id.prevButtonTarget);
        change = findViewById(R.id.changeButton);
        exName[0] = findViewById(R.id.ex1_name);
        exName[1] = findViewById(R.id.ex2_name);
        exName[2] = findViewById(R.id.ex3_name);
        exName[3] = findViewById(R.id.ex4_name);
        exName[4] = findViewById(R.id.ex5_name);
        ex[0] = findViewById(R.id.ex1_cnt);
        ex[1] = findViewById(R.id.ex2_cnt);
        ex[2] = findViewById(R.id.ex3_cnt);
        ex[3] = findViewById(R.id.ex4_cnt);
        ex[4] = findViewById(R.id.ex5_cnt);
        exNum[0] = findViewById(R.id.targetNum1);
        exNum[1] = findViewById(R.id.targetNum2);
        exNum[2] = findViewById(R.id.targetNum3);
        exNum[3] = findViewById(R.id.targetNum4);
        exNum[4] = findViewById(R.id.targetNum5);
        switches[0] = findViewById(R.id.switch1);
        switches[1] = findViewById(R.id.switch2);
        switches[2] = findViewById(R.id.switch3);
        switches[3] = findViewById(R.id.switch4);
        switches[4] = findViewById(R.id.switch5);
        targetRecyclerView = findViewById(R.id.targetRecyclerView);

        switches[0].setVisibility(View.GONE);
        switches[1].setVisibility(View.GONE);
        switches[2].setVisibility(View.GONE);
        switches[3].setVisibility(View.GONE);
        switches[4].setVisibility(View.GONE);
        ex[0].setVisibility(View.GONE);
        ex[1].setVisibility(View.GONE);
        ex[2].setVisibility(View.GONE);
        ex[3].setVisibility(View.GONE);
        ex[4].setVisibility(View.GONE);
        exNum[0].setVisibility(View.VISIBLE);
        exNum[1].setVisibility(View.VISIBLE);
        exNum[2].setVisibility(View.VISIBLE);
        exNum[3].setVisibility(View.VISIBLE);
        exNum[4].setVisibility(View.VISIBLE);
        change.setText("수정");
    }

    public void changeOff(){
        exNum[0].setText(ex[0].getText().toString());
        exNum[1].setText(ex[1].getText().toString());
        exNum[2].setText(ex[2].getText().toString());
        exNum[3].setText(ex[3].getText().toString());
        exNum[4].setText(ex[4].getText().toString());
        switches[0].setVisibility(View.GONE);
        switches[1].setVisibility(View.GONE);
        switches[2].setVisibility(View.GONE);
        switches[3].setVisibility(View.GONE);
        switches[4].setVisibility(View.GONE);
        ex[0].setVisibility(View.GONE);
        ex[1].setVisibility(View.GONE);
        ex[2].setVisibility(View.GONE);
        ex[3].setVisibility(View.GONE);
        ex[4].setVisibility(View.GONE);
        exNum[0].setVisibility(View.VISIBLE);
        exNum[1].setVisibility(View.VISIBLE);
        exNum[2].setVisibility(View.VISIBLE);
        exNum[3].setVisibility(View.VISIBLE);
        exNum[4].setVisibility(View.VISIBLE);
        change.setText("수정");

        saveTarget(user);
    }
    public void changeOn(){
        ex[0].setText(exNum[0].getText().toString());
        ex[1].setText(exNum[1].getText().toString());
        ex[2].setText(exNum[2].getText().toString());
        ex[3].setText(exNum[3].getText().toString());
        ex[4].setText(exNum[4].getText().toString());
        switches[0].setVisibility(View.VISIBLE);
        switches[1].setVisibility(View.VISIBLE);
        switches[2].setVisibility(View.VISIBLE);
        switches[3].setVisibility(View.VISIBLE);
        switches[4].setVisibility(View.VISIBLE);
        ex[0].setVisibility(View.VISIBLE);
        ex[1].setVisibility(View.VISIBLE);
        ex[2].setVisibility(View.VISIBLE);
        ex[3].setVisibility(View.VISIBLE);
        ex[4].setVisibility(View.VISIBLE);
        exNum[0].setVisibility(View.INVISIBLE);
        exNum[1].setVisibility(View.INVISIBLE);
        exNum[2].setVisibility(View.INVISIBLE);
        exNum[3].setVisibility(View.INVISIBLE);
        exNum[4].setVisibility(View.INVISIBLE);
        change.setText("완료");


    }

    private void startTarget(FirebaseUser user){

        // 날짜 정보 생성
        DateFormat dateFormatTwo = new SimpleDateFormat("yyyyMMdd");
        String targetDate = dateFormatTwo.format(new Date());
        String userId = user.getUid();

        db.collection("dailyTarget")
                .whereEqualTo("date", targetDate)
                .whereEqualTo("uid", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 해당 날짜의 데이터를 UI에 표시
                                exNum[0].setText(document.getString(exName[0].getText().toString()));
                                exNum[1].setText(document.getString(exName[1].getText().toString()));
                                exNum[2].setText(document.getString(exName[2].getText().toString()));
                                exNum[3].setText(document.getString(exName[3].getText().toString()));
                                exNum[4].setText(document.getString(exName[4].getText().toString()));
                            }
                        } else {
                            // 쿼리가 실패한 경우 에러 처리
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void saveTarget(FirebaseUser user){
        Map<String, Object> userData = new HashMap<>();
        final String collectionName = "dailyTarget";

        // 날짜 정보 생성
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(new Date());

        if(user != null){
            userData.put("date", currentDate);
            userData.put("uid", user.getUid());
            userData.put(exName[0].getText().toString(), ex[0].getText().toString());
            userData.put(exName[1].getText().toString(), ex[1].getText().toString());
            userData.put(exName[2].getText().toString(), ex[2].getText().toString());
            userData.put(exName[3].getText().toString(), ex[3].getText().toString());
            userData.put(exName[4].getText().toString(), ex[4].getText().toString());
        }

        db.collection(collectionName).document(user.getUid())
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    static class TargetItem {
        String exerciseName;
        int targetScore;
        int myScore;

        public TargetItem(String exerciseName, int targetScore, int myScore) {
            this.exerciseName = exerciseName;
            this.targetScore = targetScore;
            this.myScore = myScore;
        }
    }

    private void fetchData() {
        targetList.clear();
        for (int i = 0; i < switches.length; i++) {
            if (switches[i].isChecked()) {

            }
        }
    }
}