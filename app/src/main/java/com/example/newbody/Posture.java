package com.example.newbody;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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

import com.example.newbody.posture.PostureInfo;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;


public class Posture extends AppCompatActivity {
    private View ex_start;
    private View []ex = new View[9];
    private TextView []exName = new TextView[9];
    private TextView []premiumMessage = new TextView[3];
    private View []lockButton = new View[3];
    private TextView selectE, pre1, pre2;
    private Button prev;
    private Switch customized;
    private View squatView, pushupView, dumbbellView, sideView, legView, dumbbellCurlView, dumbbellFlyView, dumbbellTricepsView, flankView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_posture);

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
                    flankView.setVisibility(View.VISIBLE);
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
        ex[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[0].getText());
                ex[0].setBackgroundResource(R.drawable.button_check);

                for (int i = 0; i < ex.length; i++) {
                    if (ex[i] != ex[0]) {
                        ex[i].setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });
        ex[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[1].getText());
                ex[1].setBackgroundResource(R.drawable.button_check);

                for (int i = 0; i < ex.length; i++) {
                    if (ex[i] != ex[1]) {
                        ex[i].setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });
        ex[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[2].getText());
                ex[2].setBackgroundResource(R.drawable.button_check);

                for (int i = 0; i < ex.length; i++) {
                    if (ex[i] != ex[2]) {
                        ex[i].setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });

        ex[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[3].getText());
                ex[3].setBackgroundResource(R.drawable.button_check);

                for (int i = 0; i < ex.length; i++) {
                    if (ex[i] != ex[3]) {
                        ex[i].setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });

        ex[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[4].getText());
                ex[4].setBackgroundResource(R.drawable.button_check);

                for (int i = 0; i < ex.length; i++) {
                    if (ex[i] != ex[4]) {
                        ex[i].setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });

        ex[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[5].getText());
                ex[5].setBackgroundResource(R.drawable.button_check);

                for (int i = 0; i < ex.length; i++) {
                    if (ex[i] != ex[5]) {
                        ex[i].setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });

        ex[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[6].getText());
                ex[6].setBackgroundResource(R.drawable.button_check);

                for (int i = 0; i < ex.length; i++) {
                    if (ex[i] != ex[6]) {
                        ex[i].setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });

        ex[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[7].getText());
                ex[7].setBackgroundResource(R.drawable.button_check);

                for (int i = 0; i < ex.length; i++) {
                    if (ex[i] != ex[7]) {
                        ex[i].setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });

        ex[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[8].getText());
                ex[8].setBackgroundResource(R.drawable.button_check);

                for (int i = 0; i < ex.length; i++) {
                    if (ex[i] != ex[8]) {
                        ex[i].setBackgroundResource(R.drawable.button);
                    }
                }
            }
        });

        lockButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Posture.this, "프리미엄 전용 운동입니다", Toast.LENGTH_SHORT).show();
            }
        });

        lockButton[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Posture.this, "프리미엄 전용 운동입니다", Toast.LENGTH_SHORT).show();
            }
        });

        lockButton[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Posture.this, "프리미엄 전용 운동입니다", Toast.LENGTH_SHORT).show();
            }
        });

        ex_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectE.getText().equals("운동")){
                    Toast.makeText(Posture.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), PostureInfo.class);
                intent.putExtra("exercise", selectE.getText());
                startActivity(intent);
                finish();
            }
        });

    }
    private void initViews(){
        ex_start = findViewById(R.id.start_button);
        ex[0] = findViewById(R.id.ex_button1);
        ex[1] = findViewById(R.id.ex_button2);
        ex[2] = findViewById(R.id.ex_button3);
        ex[3] = findViewById(R.id.ex_button4);
        ex[4] = findViewById(R.id.ex_button5);
        ex[5] = findViewById(R.id.ex_button6);
        ex[6] = findViewById(R.id.ex_button7);
        ex[7] = findViewById(R.id.ex_button8);
        ex[8] = findViewById(R.id.ex_button9);
        exName[0] = findViewById(R.id.ex1_name);
        exName[1] = findViewById(R.id.ex2_name);
        exName[2] = findViewById(R.id.ex3_name);
        exName[3] = findViewById(R.id.ex4_name);
        exName[4] = findViewById(R.id.ex5_name);
        exName[5] = findViewById(R.id.ex6_name);
        exName[6] = findViewById(R.id.ex7_name);
        exName[7] = findViewById(R.id.ex8_name);
        exName[8] = findViewById(R.id.ex9_name);
        selectE = findViewById(R.id.exercise_select);
        prev = findViewById(R.id.prevButton);
        squatView = findViewById(R.id.squatView1);
        pushupView = findViewById(R.id.pushupView1);
        dumbbellView = findViewById(R.id.dumbbellView1);
        sideView = findViewById(R.id.sideView1);
        legView = findViewById(R.id.legView1);
        dumbbellCurlView = findViewById(R.id.dumbbellCurlView);
        dumbbellFlyView = findViewById(R.id.dumbbellFlyView);
        dumbbellTricepsView = findViewById(R.id.dumbbellTricepsView);
        flankView = findViewById(R.id.flankView1);
        customized = findViewById(R.id.customized2);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        pre1 = findViewById(R.id.pre11);
        pre2 = findViewById(R.id.pre22);
        premiumMessage[0] = findViewById(R.id.premiumMessage6);
        premiumMessage[1] = findViewById(R.id.premiumMessage7);
        premiumMessage[2] = findViewById(R.id.premiumMessage8);
        lockButton[0] = findViewById(R.id.ex_lock_button6);
        lockButton[1] = findViewById(R.id.ex_lock_button7);
        lockButton[2] = findViewById(R.id.ex_lock_button8);
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
            if(str.equals("스쿼트") || str.equals("푸쉬업") || str.equals("푸시업") || str.equals("덤벨 숄더 프레스") || str.equals("덤벨 숄더") || str.equals("덤벨숄더프레스") ||
                    str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈") || str.equals("레그 레이즈") || str.equals("레그레이즈") ||
                    str.equals("덤벨컬") || str.equals("덤벨 컬") || str.equals("덤벨 플라이") || str.equals("덤벨플라이") || str.equals("덤벨 트라이셉스 익스텐션") || str.equals("덤벨 트라이")){
                if(str.equals("스쿼트")){
                    selectE.setText(exName[0].getText());
                }else if(str.equals("푸쉬업") || str.equals("푸시업")){
                    selectE.setText(exName[1].getText());
                }else if(str.equals("덤벨 숄더 프레스") || str.equals("덤벨 숄더") || str.equals("덤벨숄더프레스")){
                    selectE.setText(exName[2].getText());
                }else if(str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈")){
                    selectE.setText(exName[3].getText());
                }else if(str.equals("레그 레이즈") || str.equals("레그레이즈")){
                    selectE.setText(exName[4].getText());
                }else if(str.equals("덤벨컬") || str.equals("덤벨 컬")){
                    selectE.setText(exName[5].getText());
                }else if(str.equals("덤벨 플라이") || str.equals("덤벨플라이")){
                    selectE.setText(exName[6].getText());
                }else if(str.equals("덤벨 트라이셉스 익스텐션") || str.equals("덤벨 트라이")){
                    selectE.setText(exName[7].getText());
                }
            }else if(str.equals("시작") || str.equals("운동 시작")){
                if(selectE.getText().equals("운동")){
                    Toast.makeText(Posture.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), PostureInfo.class);
                intent.putExtra("exercise", selectE.getText());
                startActivity(intent);
                finish();
            }else if(str.equals("이전")){
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
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
