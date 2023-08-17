package com.example.newbody;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Ranking extends AppCompatActivity {

    private Button prev, range, exercise, time, ranking_button;
    private String select_time, select_ex, select_range;
    private ArrayList<String> uidList;
    private View rankingView;

    private RecyclerView rankingRecyclerView;
    private RankingAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private List<RankingItem> rankingList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);

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

                rankingView.setVisibility(View.VISIBLE);
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
        rankingView = findViewById(R.id.layout_ranking);
        uidList = new ArrayList<>();
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
        }else if(range.getText().equals("친구")){
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
            friendListInput();
            String finalCollectionName = collectionName;
            db.collection(collectionName)
                    .orderBy(collectionName, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docId = document.getId(); // 문서의 ID (여기서는 친구의 UID)

                                if (uidList.contains(docId)) { // 문서의 ID가 친구 UID 리스트에 있는 경우만 추가
                                    String name = document.getString("name");
                                    int score = document.getLong(finalCollectionName).intValue();

                                    rankingList.add(new RankingItem(rank_in.get(), name, score));
                                    rank_in.getAndIncrement();
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });

        }
    }

    public void friendListInput(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = currentUser != null ? currentUser.getUid() : null;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        uidList.clear();

        if (currentUid != null) {
            db.collection("users")
                    .document(currentUid)
                    .collection("friends")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String friendUid = document.getString("uid");
                                    uidList.add(friendUid);
                                }
                                uidList.add(currentUid);
                                for (String uid : uidList) {
                                    Log.d("Friend UID", uid);
                                }

                            } else {
                                Log.d("Firestore", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
            if (resultCode == 1) {
                VoiceTask voiceTask = new VoiceTask();
                voiceTask.execute();
            }
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = results.get(0);
            if(str.equals("1분") || str.equals("2분") || str.equals("3분")){
                time.setText(str);
            }else if(str.equals("전체") || str.equals("친구")){
                range.setText(str);
            }else if(str.equals("스쿼트") || str.equals("푸쉬업") || str.equals("푸시업") || str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스") ||
                    str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈") || str.equals("레그 레이즈") || str.equals("레그레이즈")){
                if(str.equals("스쿼트")){
                    exercise.setText("스쿼트");
                }else if(str.equals("푸쉬업") || str.equals("푸시업")){
                    exercise.setText("푸쉬업");
                }else if(str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스")){
                    exercise.setText("덤벨 숄더 프레스");
                }else if(str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈")){
                    exercise.setText("사이드 레터럴 레이즈");
                }else if(str.equals("레그 레이즈") || str.equals("레그레이즈")){
                    exercise.setText("레그 레이즈");
                }
            }else if(str.equals("이전")){
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }else if(str.equals("순위조회") || str.equals("순위 조회") || str.equals("순위") || str.equals("랭킹 보여줘")){
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
        }
    }

    private void restartVoiceRecognitionService() {
        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);
    }

    public class VoiceTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getVoice();
        }
    }

    private void getVoice() {
        Intent intent = new Intent();
        intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        String language = "ko-KR";
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 브로드캐스트 리시버 등록
        registerReceiver(receiver, new IntentFilter("com.example.newbody.RESULT_ACTION"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 브로드캐스트 리시버 등록 해제
        unregisterReceiver(receiver);
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