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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.record.RecordDumbbell;
import com.example.newbody.record.RecordLegRaise;
import com.example.newbody.record.RecordPushup;
import com.example.newbody.record.RecordSidelateralraise;
import com.example.newbody.record.RecordSquat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class Record extends AppCompatActivity {

    private String select_time, select_ex;
    private long[] totalTimesInMillis = {1 * 60 * 1000, 2 * 60 * 1000, 3 * 60 * 1000};
    private Button time;
    private View ex_start;
    private View squatView, pushupView, dumbbellView, sideView, legView, dumbbellCurlView, dumbbellFlyView, dumbbellTricepsView;
    private View []ex = new View[8];
    private TextView []exName = new TextView[8];
    private TextView []premiumMessage = new TextView[3];
    private View []lockButton = new View[3];
    private TextView selectT, selectE, pre1, pre2;
    private int select_num;
    private Button prev;
    private Switch customized;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);

        initViews();
        premiumCheck();
        customized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(customized.isChecked()){
                    pre1.setText(" ");
                    pre2.setText(" ");
                    DocumentReference userRecordRef = db.collection("users").document(user.getUid());
                    userRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String preference1 = document.getString("preference1");
                                    pre1.setText(preference1);
                                    String preference2 = document.getString("preference2");
                                    pre2.setText(preference2);

                                    if(preference1.equals("도구 이용 운동")){
                                        squatView.setVisibility(View.GONE);
                                        pushupView.setVisibility(View.GONE);
                                        legView.setVisibility(View.GONE);
                                    }else if(preference1.equals("맨몸 운동")){
                                        dumbbellCurlView.setVisibility(View.GONE);
                                        dumbbellFlyView.setVisibility(View.GONE);
                                        dumbbellTricepsView.setVisibility(View.GONE);
                                        if(preference2.equals("팔 운동")){
                                            squatView.setVisibility(View.GONE);
                                            dumbbellView.setVisibility(View.GONE);
                                            sideView.setVisibility(View.GONE);
                                            legView.setVisibility(View.GONE);
                                        }else if(preference2.equals("하체 운동")){
                                            pushupView.setVisibility(View.GONE);
                                            dumbbellView.setVisibility(View.GONE);
                                            sideView.setVisibility(View.GONE);
                                        }else if(preference2.equals("복근 운동")){
                                            squatView.setVisibility(View.GONE);
                                            dumbbellView.setVisibility(View.GONE);
                                            sideView.setVisibility(View.GONE);
                                        }
                                    }
                                } else {
                                    Log.d("Firestore", "No such document");
                                }
                            } else {
                                Log.d("Firestore", "Failed to get document", task.getException());
                            }
                        }
                    });
                }else if(!customized.isChecked()){
                    squatView.setVisibility(View.VISIBLE);
                    pushupView.setVisibility(View.VISIBLE);
                    dumbbellView.setVisibility(View.VISIBLE);
                    sideView.setVisibility(View.VISIBLE);
                    legView.setVisibility(View.VISIBLE);
                    dumbbellCurlView.setVisibility(View.VISIBLE);
                    dumbbellFlyView.setVisibility(View.VISIBLE);
                    dumbbellTricepsView.setVisibility(View.VISIBLE);
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });

        ex[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[0].getText());
                select_num = 1;
            }
        });
        ex[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[1].getText());
                select_num = 2;
            }
        });
        ex[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[2].getText());
                select_num = 3;
            }
        });

        ex[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[3].getText());
                select_num = 4;
            }
        });

        ex[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[4].getText());
                select_num = 5;
            }
        });

        ex[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[5].getText());
                select_num = 6;
            }
        });

        ex[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[6].getText());
                select_num = 7;
            }
        });

        ex[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[7].getText());
                select_num = 8;
            }
        });

        ex_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectT.getText().equals("시간") || selectE.getText().equals("운동")){
                    Toast.makeText(Record.this, "시간 또는 운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(select_num == 1){
                    Intent intent = new Intent(Record.this, RecordSquat.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 2){
                    Intent intent = new Intent(Record.this, RecordPushup.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 3){
                    Intent intent = new Intent(Record.this, RecordDumbbell.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 4){
                    Intent intent = new Intent(Record.this, RecordSidelateralraise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 5){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 6){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 7){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 8){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    public void initViews(){
        time = findViewById(R.id.time);
        ex_start = findViewById(R.id.start_button);
        ex[0] = findViewById(R.id.ex_button1);
        ex[1] = findViewById(R.id.ex_button2);
        ex[2] = findViewById(R.id.ex_button3);
        ex[3] = findViewById(R.id.ex_button4);
        ex[4] = findViewById(R.id.ex_button5);
        ex[5] = findViewById(R.id.ex_button6);
        ex[6] = findViewById(R.id.ex_button7);
        ex[7] = findViewById(R.id.ex_button8);
        exName[0] = findViewById(R.id.ex1_name);
        exName[1] = findViewById(R.id.ex2_name);
        exName[2] = findViewById(R.id.ex3_name);
        exName[3] = findViewById(R.id.ex4_name);
        exName[4] = findViewById(R.id.ex5_name);
        exName[5] = findViewById(R.id.ex6_name);
        exName[6] = findViewById(R.id.ex7_name);
        exName[7] = findViewById(R.id.ex8_name);
        selectT = findViewById(R.id.time_select);
        selectE = findViewById(R.id.exercise_select);
        prev = findViewById(R.id.prevButtonRecord);
        squatView = findViewById(R.id.squatView);
        pushupView = findViewById(R.id.pushupView);
        dumbbellView = findViewById(R.id.dumbbellView);
        sideView = findViewById(R.id.sideView);
        legView = findViewById(R.id.legView);
        dumbbellCurlView = findViewById(R.id.dumbbellCurlView);
        dumbbellFlyView = findViewById(R.id.dumbbellFlyView);
        dumbbellTricepsView = findViewById(R.id.dumbbellTricepsView);
        customized = findViewById(R.id.customized1);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        pre1 = findViewById(R.id.pre1);
        pre2 = findViewById(R.id.pre2);
        premiumMessage[0] = findViewById(R.id.premiumMessage6);
        premiumMessage[1] = findViewById(R.id.premiumMessage7);
        premiumMessage[2] = findViewById(R.id.premiumMessage8);
        lockButton[0] = findViewById(R.id.ex_lock_button6);
        lockButton[1] = findViewById(R.id.ex_lock_button7);
        lockButton[2] = findViewById(R.id.ex_lock_button8);
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

                // 선택한 난이도에 따라 시간 배열을 업데이트
                if (select_time.equals("1분")) {
                    selectT.setText("1분");
                } else if (select_time.equals("2분")) {
                    selectT.setText("2분");
                } else if (select_time.equals("3분")) {
                    selectT.setText("3분");
                }
            }
        });
        builder.show();
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
                // 선택한 난이도에 따라 시간 배열을 업데이트
                if (str.equals("1분")) {
                    select_time = "1분";
                    time.setText(select_time);
                    selectT.setText("1분");
                } else if (str.equals("2분")) {
                    select_time = "2분";
                    time.setText(select_time);
                    selectT.setText("2분");
                } else if (str.equals("3분")) {
                    select_time = "3분";
                    time.setText(select_time);
                    selectT.setText("3분");
                }
            }else if(str.equals("스쿼트") || str.equals("푸쉬업") || str.equals("푸시업") || str.equals("덤벨 숄더 프레스") || str.equals("덤벨 숄더") || str.equals("덤벨숄더프레스") ||
                    str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈") || str.equals("레그 레이즈") || str.equals("레그레이즈") ||
                    str.equals("덤벨컬") || str.equals("덤벨 컬") || str.equals("덤벨 플라이") || str.equals("덤벨플라이") || str.equals("덤벨 트라이셉스 익스텐션") || str.equals("덤벨 트라이")){
                if(str.equals("스쿼트")){
                    selectE.setText(exName[0].getText());
                    select_num = 1;
                }else if(str.equals("푸쉬업") || str.equals("푸시업")){
                    selectE.setText(exName[1].getText());
                    select_num = 2;
                }else if(str.equals("덤벨 숄더 프레스") || str.equals("덤벨 숄더") || str.equals("덤벨숄더프레스")){
                    selectE.setText(exName[2].getText());
                    select_num = 3;
                }else if(str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈")){
                    selectE.setText(exName[3].getText());
                    select_num = 4;
                }else if(str.equals("레그 레이즈") || str.equals("레그레이즈")){
                    selectE.setText(exName[4].getText());
                    select_num = 5;
                }else if(str.equals("덤벨컬") || str.equals("덤벨 컬")){
                    selectE.setText(exName[5].getText());
                    select_num = 6;
                }else if(str.equals("덤벨 플라이") || str.equals("덤벨플라이")){
                    selectE.setText(exName[6].getText());
                    select_num = 7;
                }else if(str.equals("덤벨 트라이셉스 익스텐션") || str.equals("덤벨 트라이")){
                    selectE.setText(exName[7].getText());
                    select_num = 8;
                }
            }else if(str.equals("시작") || str.equals("운동 시작")){
                if(selectT.getText().equals("시간") || selectE.getText().equals("운동")){
                    Toast.makeText(Record.this, "시간 또는 운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(select_num == 1){
                    Intent intent = new Intent(Record.this, RecordSquat.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 2){
                    Intent intent = new Intent(Record.this, RecordPushup.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 3){
                    Intent intent = new Intent(Record.this, RecordDumbbell.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 4){
                    Intent intent = new Intent(Record.this, RecordSidelateralraise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 5){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 6){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 7){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }else if(select_num == 8){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                    finish();
                }
            }else if(str.equals("이전")){
                Intent intent = new Intent(getApplicationContext(), Menu.class);
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
                                        customized.setVisibility(View.VISIBLE);
                                        premiumMessage[0].setVisibility(View.GONE);
                                        premiumMessage[1].setVisibility(View.GONE);
                                        premiumMessage[2].setVisibility(View.GONE);
                                        lockButton[0].setVisibility(View.GONE);
                                        lockButton[1].setVisibility(View.GONE);
                                        lockButton[2].setVisibility(View.GONE);
                                        ex[5].setVisibility(View.VISIBLE);
                                        ex[6].setVisibility(View.VISIBLE);
                                        ex[7].setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                            }
                        }
                    });
        }
    }
}
