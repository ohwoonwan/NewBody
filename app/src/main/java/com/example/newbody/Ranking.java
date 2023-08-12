package com.example.newbody;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Ranking extends AppCompatActivity {

    private Button prev, range, exercise, time, ranking_button;
    private String select_time, select_ex, select_range;

    private RecyclerView rankingRecyclerView;
    private RankingAdapter adapter;
    private FirebaseFirestore db;
    private List<RankingItem> rankingList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initViews();
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }
        });

        range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showRangeDialog();}
        });
        exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showExerciseDialog();}
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showTimeDialog();}
        });

        ranking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(range.getText().equals("범위")){
                    Toast.makeText(Ranking.this, "범위를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(exercise.getText().equals("운동")){
                    Toast.makeText(Ranking.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(time.getText().equals("시간")){
                    Toast.makeText(Ranking.this, "시간을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                db = FirebaseFirestore.getInstance();
                rankingRecyclerView.setLayoutManager(new LinearLayoutManager(Ranking.this));

                fetchData();
                adapter = new RankingAdapter(rankingList);
                rankingRecyclerView.setAdapter(adapter);
            }
        });
    }
    public void initViews(){
        prev = findViewById(R.id.prevButtonRanking);
        range = findViewById(R.id.range);
        exercise = findViewById(R.id.exercise);
        time = findViewById(R.id.timeRanking);
        ranking_button = findViewById(R.id.ranking_info_button);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
    }

    private void showTimeDialog() {
        final String[] difficultyOptions = {"1분", "2분", "3분"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("시간 선택");
        builder.setItems(difficultyOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_time = difficultyOptions[which];
                time.setText(select_time);
            }
        });
        builder.show();
    }

    private void showExerciseDialog() {
        final String[] exOptions = {"스쿼트", "푸쉬업", "덤벨 숄더 프레스", "사이드 레터럴 레이즈", "레그 레이즈"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("운동 선택");
        builder.setItems(exOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_ex = exOptions[which];
                exercise.setText(select_ex);
            }
        });
        builder.show();
    }
    private void showRangeDialog() {
        final String[] rangeOptions = {"전체", "친구"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("범위 선택");
        builder.setItems(rangeOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_range = rangeOptions[which];
                range.setText(select_range);
            }
        });
        builder.show();
    }

    private void fetchData() {
        AtomicInteger rank_in = new AtomicInteger(1);
        rankingList.clear();
        String collectionName = null;
        if(range.getText().equals("전체")){
            if(exercise.getText().equals("스쿼트")){
                if(time.getText().equals("1분")){
                    collectionName = "countSquat1Minute";
                }else if(time.getText().equals("2분")){
                    collectionName = "countSquat2Minute";
                }else if(time.getText().equals("3분")){
                    collectionName = "countSquat3Minute";
                }
            }else if(exercise.getText().equals("푸쉬업")){
                if(time.getText().equals("1분")){
                    collectionName = "countPushup1Minute";
                }else if(time.getText().equals("2분")){
                    collectionName = "countPushup2Minute";
                }else if(time.getText().equals("3분")){
                    collectionName = "countPushup3Minute";
                }
            }else if(exercise.getText().equals("덤벨 숄더 프레스")){
                if(time.getText().equals("1분")){
                    collectionName = "countDumbbell1Minute";
                }else if(time.getText().equals("2분")){
                    collectionName = "countDumbbell2Minute";
                }else if(time.getText().equals("3분")){
                    collectionName = "countDumbbell3Minute";
                }
            }else if(exercise.getText().equals("사이드 레터럴 레이즈")){
                if(time.getText().equals("1분")){
                    collectionName = "countSide1Minute";
                }else if(time.getText().equals("2분")){
                    collectionName = "countSide2Minute";
                }else if(time.getText().equals("3분")){
                    collectionName = "countSide3Minute";
                }
            }else if(exercise.getText().equals("레그 레이즈")){
                if(time.getText().equals("1분")){
                    collectionName = "countLeg1Minute";
                }else if(time.getText().equals("2분")){
                    collectionName = "countLeg2Minute";
                }else if(time.getText().equals("3분")){
                    collectionName = "countLeg3Minute";
                }
            }
        }else if(range.getText().equals("친구")){

        }
        String finalCollectionName = collectionName;
        db.collection(collectionName).orderBy(collectionName, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            int score = document.getLong(finalCollectionName).intValue();

                            rankingList.add(new RankingItem(rank_in.get(), name, score));
                            rank_in.getAndIncrement();
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    static class RankingItem {
        int rank;
        String name;
        int score;

        public RankingItem(int rank, String name, int score) {
            this.rank = rank;
            this.name = name;
            this.score = score;
        }
    }
}