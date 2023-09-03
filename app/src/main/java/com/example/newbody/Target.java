package com.example.newbody;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newbody.videoinfo.VideoDumbbell;
import com.example.newbody.videoinfo.VideoLegRaise;
import com.example.newbody.videoinfo.VideoPushups;
import com.example.newbody.videoinfo.VideoSide;
import com.example.newbody.videoinfo.VideoSquat;
import com.example.newbody.videoinfo.VideoWarmup;
import com.example.newbody.workout.Home_Training_WarmUp;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Target extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    private Button prev, change;
    private boolean checkChange = true;
    private RecyclerView targetRecyclerView;
    private TargetAdapter adapter;
    private String selectCnt;
    private TextView []exName = new TextView[8];
    private Button []ex = new Button[8];
    private TextView []exNum = new TextView[8];
    private Switch []switches = new Switch[8];
    private View []premiumView = new View[3];
    private List<TargetItem> targetList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        initViews();
        premiumCheck();
        targetRecyclerView.setLayoutManager(new LinearLayoutManager(Target.this));

        startTarget(user);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                intent.putExtra("SELECTED_FRAGMENT_INDEX", 3);
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
        exName[5] = findViewById(R.id.ex6_name);
        exName[6] = findViewById(R.id.ex7_name);
        exName[7] = findViewById(R.id.ex8_name);
        ex[0] = findViewById(R.id.ex1_cnt);
        ex[1] = findViewById(R.id.ex2_cnt);
        ex[2] = findViewById(R.id.ex3_cnt);
        ex[3] = findViewById(R.id.ex4_cnt);
        ex[4] = findViewById(R.id.ex5_cnt);
        ex[5] = findViewById(R.id.ex6_cnt);
        ex[6] = findViewById(R.id.ex7_cnt);
        ex[7] = findViewById(R.id.ex8_cnt);
        exNum[0] = findViewById(R.id.targetNum1);
        exNum[1] = findViewById(R.id.targetNum2);
        exNum[2] = findViewById(R.id.targetNum3);
        exNum[3] = findViewById(R.id.targetNum4);
        exNum[4] = findViewById(R.id.targetNum5);
        exNum[5] = findViewById(R.id.targetNum6);
        exNum[6] = findViewById(R.id.targetNum7);
        exNum[7] = findViewById(R.id.targetNum8);
        switches[0] = findViewById(R.id.switch1);
        switches[1] = findViewById(R.id.switch2);
        switches[2] = findViewById(R.id.switch3);
        switches[3] = findViewById(R.id.switch4);
        switches[4] = findViewById(R.id.switch5);
        switches[5] = findViewById(R.id.switch6);
        switches[6] = findViewById(R.id.switch7);
        switches[7] = findViewById(R.id.switch8);
        targetRecyclerView = findViewById(R.id.targetRecyclerView);
        premiumView[0] = findViewById(R.id.premiumView1);
        premiumView[1] = findViewById(R.id.premiumView2);
        premiumView[2] = findViewById(R.id.premiumView3);

        switches[0].setVisibility(View.GONE);
        switches[1].setVisibility(View.GONE);
        switches[2].setVisibility(View.GONE);
        switches[3].setVisibility(View.GONE);
        switches[4].setVisibility(View.GONE);
        switches[5].setVisibility(View.GONE);
        switches[6].setVisibility(View.GONE);
        switches[7].setVisibility(View.GONE);
        ex[0].setVisibility(View.GONE);
        ex[1].setVisibility(View.GONE);
        ex[2].setVisibility(View.GONE);
        ex[3].setVisibility(View.GONE);
        ex[4].setVisibility(View.GONE);
        ex[5].setVisibility(View.GONE);
        ex[6].setVisibility(View.GONE);
        ex[7].setVisibility(View.GONE);
        exNum[0].setVisibility(View.VISIBLE);
        exNum[1].setVisibility(View.VISIBLE);
        exNum[2].setVisibility(View.VISIBLE);
        exNum[3].setVisibility(View.VISIBLE);
        exNum[4].setVisibility(View.VISIBLE);
        exNum[5].setVisibility(View.VISIBLE);
        exNum[6].setVisibility(View.VISIBLE);
        exNum[7].setVisibility(View.VISIBLE);
        change.setText("수정");
    }

    public void changeOff(){
        exNum[0].setText(ex[0].getText().toString());
        exNum[1].setText(ex[1].getText().toString());
        exNum[2].setText(ex[2].getText().toString());
        exNum[3].setText(ex[3].getText().toString());
        exNum[4].setText(ex[4].getText().toString());
        exNum[5].setText(ex[5].getText().toString());
        exNum[6].setText(ex[6].getText().toString());
        exNum[7].setText(ex[7].getText().toString());
        switches[0].setVisibility(View.GONE);
        switches[1].setVisibility(View.GONE);
        switches[2].setVisibility(View.GONE);
        switches[3].setVisibility(View.GONE);
        switches[4].setVisibility(View.GONE);
        switches[5].setVisibility(View.GONE);
        switches[6].setVisibility(View.GONE);
        switches[7].setVisibility(View.GONE);
        ex[0].setVisibility(View.GONE);
        ex[1].setVisibility(View.GONE);
        ex[2].setVisibility(View.GONE);
        ex[3].setVisibility(View.GONE);
        ex[4].setVisibility(View.GONE);
        ex[5].setVisibility(View.GONE);
        ex[6].setVisibility(View.GONE);
        ex[7].setVisibility(View.GONE);
        exNum[0].setVisibility(View.VISIBLE);
        exNum[1].setVisibility(View.VISIBLE);
        exNum[2].setVisibility(View.VISIBLE);
        exNum[3].setVisibility(View.VISIBLE);
        exNum[4].setVisibility(View.VISIBLE);
        exNum[5].setVisibility(View.VISIBLE);
        exNum[6].setVisibility(View.VISIBLE);
        exNum[7].setVisibility(View.VISIBLE);
        change.setText("수정");

        saveTarget(user);

        fetchData();
        adapter = new TargetAdapter(targetList);
        targetRecyclerView.setAdapter(adapter);
    }
    public void changeOn(){
        ex[0].setText(exNum[0].getText().toString());
        ex[1].setText(exNum[1].getText().toString());
        ex[2].setText(exNum[2].getText().toString());
        ex[3].setText(exNum[3].getText().toString());
        ex[4].setText(exNum[4].getText().toString());
        ex[5].setText(exNum[5].getText().toString());
        ex[6].setText(exNum[6].getText().toString());
        ex[7].setText(exNum[7].getText().toString());
        switches[0].setVisibility(View.VISIBLE);
        switches[1].setVisibility(View.VISIBLE);
        switches[2].setVisibility(View.VISIBLE);
        switches[3].setVisibility(View.VISIBLE);
        switches[4].setVisibility(View.VISIBLE);
        switches[5].setVisibility(View.VISIBLE);
        switches[6].setVisibility(View.VISIBLE);
        switches[7].setVisibility(View.VISIBLE);
        ex[0].setVisibility(View.VISIBLE);
        ex[1].setVisibility(View.VISIBLE);
        ex[2].setVisibility(View.VISIBLE);
        ex[3].setVisibility(View.VISIBLE);
        ex[4].setVisibility(View.VISIBLE);
        ex[5].setVisibility(View.VISIBLE);
        ex[6].setVisibility(View.VISIBLE);
        ex[7].setVisibility(View.VISIBLE);
        exNum[0].setVisibility(View.INVISIBLE);
        exNum[1].setVisibility(View.INVISIBLE);
        exNum[2].setVisibility(View.INVISIBLE);
        exNum[3].setVisibility(View.INVISIBLE);
        exNum[4].setVisibility(View.INVISIBLE);
        exNum[5].setVisibility(View.INVISIBLE);
        exNum[6].setVisibility(View.INVISIBLE);
        exNum[7].setVisibility(View.INVISIBLE);

        for(int i = 0; i < 8; i++){
            int finalI = i;
            ex[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCntDialog(ex[finalI]);
                }
            });
        }

        change.setText("완료");


    }

    private void showCntDialog(Button ex) {
        final String[] difficultyOptions = {"5개", "10개", "15개", "20개", "25개", "30개"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("개수 선택");
        builder.setItems(difficultyOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectCnt = difficultyOptions[which];
                ex.setText(selectCnt);
            }
        });
        builder.show();
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
                                exNum[5].setText(document.getString(exName[5].getText().toString()));
                                exNum[6].setText(document.getString(exName[6].getText().toString()));
                                exNum[7].setText(document.getString(exName[7].getText().toString()));
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
            userData.put(exName[5].getText().toString(), ex[5].getText().toString());
            userData.put(exName[6].getText().toString(), ex[6].getText().toString());
            userData.put(exName[7].getText().toString(), ex[7].getText().toString());
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
        int myScore;

        public TargetItem(String exerciseName, int myScore) {
            this.exerciseName = exerciseName;
            this.myScore = myScore;
        }
    }

    private void fetchData() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(new Date());
        String collection = null;
        String key = null;
        String exercise = null;
        int ex_num = 0;
        targetList.clear();
        for (int i = 0; i < switches.length; i++) {
            if (switches[i].isChecked()) {
                if(i == 0){
                    collection = "dailySquatRecords";
                    key = currentDate+"squatCount";
                    exercise = "스쿼트";
                }else if(i == 1){
                    collection = "dailyPushupRecords";
                    key = currentDate+"pushupCount";
                    exercise = "푸쉬업";
                }else if(i == 2){
                    collection = "dailyDumbbellRecords";
                    key = currentDate+"dumbbellCount";
                    exercise = "덤벨 숄더 프레스";
                }else if(i == 3){
                    collection = "dailySideRecords";
                    key = currentDate+"sideCount";
                    exercise = "사이드 래터럴 레이즈";
                }else if(i == 4){
                    collection = "dailyLegRecords";
                    key = currentDate+"legCount";
                    exercise = "레그 레이즈";
                }else if(i == 5){
                    collection = "dailyCurlRecords";
                    key = currentDate+"CurlCount";
                    exercise = "덤벨 컬";
                }else if(i == 6){
                    collection = "dailyFlyRecords";
                    key = currentDate+"flyCount";
                    exercise = "덤벨 플라이";
                }else if(i == 7){
                    collection = "dailyTricepsRecords";
                    key = currentDate+"tricepsCount";
                    exercise = "덤벨 트라이셉스 익스텐션";
                }
                ex_num = extractNumber(exNum[i].getText().toString());
                loadSquatRecordWithDate(collection, key, exercise, ex_num);
            }
        }
    }
    private void loadSquatRecordWithDate(String collection, String date, String ex, int en){
        final String collectionName = collection;
        String userId = user.getUid();
        String key = date;

        DocumentReference userRecordRef = db.collection(collectionName).document(userId);
        userRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int score = 0;
                        if(document.getLong(key) != null){
                            score = document.getLong(key).intValue();
                        }
                        int scoreSet = (int) ((double)score/(double)en*100);
                        String ex_name = ex + " ( " + score + " / " + en + " ) ";
                        targetList.add(new TargetItem(ex_name, scoreSet));
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "Failed to get document", task.getException());
                }
            }
        });
    }

    public static int extractNumber(String input) {
        Pattern pattern = Pattern.compile("\\d+"); // 숫자에 해당하는 정규식
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        } else {
            return 0; // 숫자가 없을 경우 0을 반환
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
            if(str.equals("스쿼트") || str.equals("푸쉬업") || str.equals("푸시업") || str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스") ||
                    str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈") || str.equals("레그 레이즈") ||
                    str.equals("레그레이즈") || str.equals("준비 운동") || str.equals("준비운동")){
                if(str.equals("스쿼트")){

                }else if(str.equals("푸쉬업") || str.equals("푸시업")){

                }else if(str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스")){

                }else if(str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈")){

                }else if(str.equals("레그 레이즈") || str.equals("레그레이즈")){

                }else if(str.equals("준비 운동") || str.equals("준비운동")){

                }
            }else if(str.equals("수정") || str.equals("완료")) {
                if(checkChange){
                    changeOn();
                    checkChange = false;
                }else{
                    changeOff();
                    checkChange = true;
                }
            }else if(str.equals("이전")){
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                intent.putExtra("SELECTED_FRAGMENT_INDEX", 3);
                startActivity(intent);
                finish();
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

    public void premiumCheck(){
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }else{
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String grade = document.getString("grade");
                                    if (grade == null) grade = "일반";

                                    if(grade.equals("프리미엄")){
                                        premiumView[0].setVisibility(View.VISIBLE);
                                        premiumView[1].setVisibility(View.VISIBLE);
                                        premiumView[2].setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                            }
                        }
                    });
        }
    }

}