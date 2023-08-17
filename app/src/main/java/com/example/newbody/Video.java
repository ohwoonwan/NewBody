package com.example.newbody;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.posture.PostureInfo;
import com.example.newbody.videoinfo.VideoDumbbell;
import com.example.newbody.videoinfo.VideoLegRaise;
import com.example.newbody.videoinfo.VideoPushups;
import com.example.newbody.videoinfo.VideoSide;
import com.example.newbody.videoinfo.VideoSquat;
import com.example.newbody.videoinfo.VideoWarmup;
import com.example.newbody.workout.Home_Training_WarmUp;

import java.util.ArrayList;

public class Video extends AppCompatActivity {
    private String selectedDifficulty; // 난이도를 저장할 변수
    private Button difficulty, start, prev;
    private View []ex = new View[6];
    private TextView[] LevelCountView = new TextView[6];
    private View warmupVideo;
    private View pushupsVideo;
    private View squatVideo;
    private View dumbbellVideo;
    private View sideVideo;
    private View legraiseVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);

        difficulty = findViewById(R.id.difficulty);
        ex[0] = findViewById(R.id.ex_button1);
        ex[1] = findViewById(R.id.ex_button2);
        ex[2] = findViewById(R.id.ex_button3);
        ex[3] = findViewById(R.id.ex_button4);
        ex[4] = findViewById(R.id.ex_button5);
        ex[5] = findViewById(R.id.ex_button6);
        LevelCountView[0] = findViewById(R.id.levelcountview1);
        LevelCountView[1] = findViewById(R.id.levelcountview2);
        LevelCountView[2] = findViewById(R.id.levelcountview3);
        LevelCountView[3] = findViewById(R.id.levelcountview4);
        LevelCountView[4] = findViewById(R.id.levelcountview5);
        LevelCountView[5] = findViewById(R.id.levelcountview6);
        start = findViewById(R.id.start_b);
        prev = findViewById(R.id.prevButtonTraning);

        warmupVideo = findViewById(R.id.ellipse_1);
        squatVideo = findViewById(R.id.ellipse_2);
        pushupsVideo = findViewById(R.id.ellipse_3);
        dumbbellVideo = findViewById(R.id.ellipse_4);
        sideVideo = findViewById(R.id.ellipse_5);
        legraiseVideo = findViewById(R.id.ellipse_6);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }
        });

        difficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDifficultyDialog();
            }
        });

        warmupVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VideoWarmup.class);
                startActivity(intent);
            }
        });

        squatVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VideoSquat.class);
                startActivity(intent);
            }
        });

        pushupsVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VideoPushups.class);
                startActivity(intent);
            }
        });

        dumbbellVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VideoDumbbell.class);
                startActivity(intent);
            }
        });

        sideVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VideoSide.class);
                startActivity(intent);
            }
        });

        legraiseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VideoLegRaise.class);
                startActivity(intent);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (selectedDifficulty != null) {

                    Intent intentSub1 = new Intent(Video.this, Home_Training_WarmUp.class);
                    intentSub1.putExtra("difficulty", selectedDifficulty); // 선택한 난이도 정보를 넘겨줌
                    startActivity(intentSub1);

                }
                else {
                    Toast.makeText(Video.this, "난이도를 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showDifficultyDialog() {
        final String[] difficultyOptions = {"쉬움", "보통", "어려움"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("난이도 선택");
        builder.setItems(difficultyOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedDifficulty = difficultyOptions[which];
                difficulty.setText(selectedDifficulty);

                // 선택한 난이도에 따라 시간 배열을 업데이트
                if (selectedDifficulty.equals("쉬움")) {
                    LevelCountView[0].setText("05:00"); // 준비운동
                    LevelCountView[1].setText("15개"); // 스쿼트
                    LevelCountView[2].setText("7개"); // 푸시업
                    LevelCountView[3].setText("10개"); // 덤프
                    LevelCountView[4].setText("15개"); // 사레레
                    LevelCountView[5].setText("7개"); // 레그라이즈
                } else if (selectedDifficulty.equals("보통")) {
                    LevelCountView[0].setText("05:00"); // 준비운동
                    LevelCountView[1].setText("20개"); // 스쿼트
                    LevelCountView[2].setText("20개"); // 푸시업
                    LevelCountView[3].setText("10개"); // 덤프
                    LevelCountView[4].setText("25개"); // 사레레
                    LevelCountView[5].setText("15개"); // 레그라이즈
                } else if (selectedDifficulty.equals("어려움")) {
                    LevelCountView[0].setText("05:00"); // 준비운동
                    LevelCountView[1].setText("25개"); // 스쿼트
                    LevelCountView[2].setText("35개"); // 푸시업
                    LevelCountView[3].setText("10개"); // 덤프
                    LevelCountView[4].setText("35개"); // 사레레
                    LevelCountView[5].setText("20개"); // 레그라이즈
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
            if(str.equals("스쿼트") || str.equals("푸쉬업") || str.equals("푸시업") || str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스") ||
                    str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈") || str.equals("레그 레이즈") ||
                    str.equals("레그레이즈") || str.equals("준비 운동") || str.equals("준비운동")){
                if(str.equals("스쿼트")){
                    Intent intent = new Intent(getApplicationContext(), VideoSquat.class);
                    startActivity(intent);
                }else if(str.equals("푸쉬업") || str.equals("푸시업")){
                    Intent intent = new Intent(getApplicationContext(), VideoPushups.class);
                    startActivity(intent);
                }else if(str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스")){
                    Intent intent = new Intent(getApplicationContext(), VideoDumbbell.class);
                    startActivity(intent);
                }else if(str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈")){
                    Intent intent = new Intent(getApplicationContext(), VideoSide.class);
                    startActivity(intent);
                }else if(str.equals("레그 레이즈") || str.equals("레그레이즈")){
                    Intent intent = new Intent(getApplicationContext(), VideoLegRaise.class);
                    startActivity(intent);
                }else if(str.equals("준비 운동") || str.equals("준비운동")){
                    Intent intent = new Intent(getApplicationContext(), VideoWarmup.class);
                    startActivity(intent);
                }
            }else if(str.equals("쉬움") || str.equals("보통") || str.equals("어려움")){
                difficulty.setText(str);
                selectedDifficulty = difficulty.getText().toString();

                // 선택한 난이도에 따라 시간 배열을 업데이트
                if (selectedDifficulty.equals("쉬움")) {
                    LevelCountView[0].setText("05:00"); // 준비운동
                    LevelCountView[1].setText("15개"); // 스쿼트
                    LevelCountView[2].setText("7개"); // 푸시업
                    LevelCountView[3].setText("10개"); // 덤프
                    LevelCountView[4].setText("15개"); // 사레레
                    LevelCountView[5].setText("7개"); // 레그라이즈
                } else if (selectedDifficulty.equals("보통")) {
                    LevelCountView[0].setText("05:00"); // 준비운동
                    LevelCountView[1].setText("20개"); // 스쿼트
                    LevelCountView[2].setText("20개"); // 푸시업
                    LevelCountView[3].setText("10개"); // 덤프
                    LevelCountView[4].setText("25개"); // 사레레
                    LevelCountView[5].setText("15개"); // 레그라이즈
                } else if (selectedDifficulty.equals("어려움")) {
                    LevelCountView[0].setText("05:00"); // 준비운동
                    LevelCountView[1].setText("25개"); // 스쿼트
                    LevelCountView[2].setText("35개"); // 푸시업
                    LevelCountView[3].setText("10개"); // 덤프
                    LevelCountView[4].setText("35개"); // 사레레
                    LevelCountView[5].setText("20개"); // 레그라이즈
                }
            }else if(str.equals("이전")){
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }else if(str.equals("시작") || str.equals("운동 시작")){
                if (selectedDifficulty != null) {

                    Intent intentSub1 = new Intent(Video.this, Home_Training_WarmUp.class);
                    intentSub1.putExtra("difficulty", selectedDifficulty); // 선택한 난이도 정보를 넘겨줌
                    startActivity(intentSub1);

                }
                else {
                    Toast.makeText(Video.this, "난이도를 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Menu.class);
        startActivity(intent);
        finish();
    }
}