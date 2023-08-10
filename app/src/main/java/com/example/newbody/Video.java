package com.example.newbody;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.videoinfo.VideoDumbbell;
import com.example.newbody.videoinfo.VideoPushups;
import com.example.newbody.videoinfo.VideoSquat;
import com.example.newbody.videoinfo.VideoWarmup;
import com.example.newbody.workout.Home_Training_WarmUp;

public class Video extends AppCompatActivity {
    private String selectedDifficulty; // 난이도를 저장할 변수
    private long[] totalTimesInMillis = {1 * 10 * 1000, 1 * 10 * 1000, 1 * 10 * 1000, 1 * 10 * 1000};

    private Button difficulty, start;
    private View []ex = new View[4];
    private TextView []exTime = new TextView[4];
    private CountDownTimer[] countDownTimers = new CountDownTimer[4];// 타이머 객체들을 저장할 배열
    private boolean isTimerRunning = false; // 타이머 실행 여부를 확인하는 변수
    private View warmupVideo;
    private View pushupsVideo;
    private View squatVideo;
    private View dumbbellVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        difficulty = findViewById(R.id.difficulty);

        ex[0] = findViewById(R.id.ex_button1);
        ex[1] = findViewById(R.id.ex_button2);
        ex[2] = findViewById(R.id.ex_button3);
        ex[3] = findViewById(R.id.ex_button4);
        exTime[0] = findViewById(R.id.time1);
        exTime[1] = findViewById(R.id.time2);
        exTime[2] = findViewById(R.id.time3);
        exTime[3] = findViewById(R.id.time4);
        start = findViewById(R.id.start_b);

        warmupVideo = findViewById(R.id.ellipse_1);
        squatVideo = findViewById(R.id.ellipse_2);
        pushupsVideo = findViewById(R.id.ellipse_3);
        dumbbellVideo = findViewById(R.id.ellipse_4);

        if(selectedDifficulty == null){
            selectedDifficulty = "쉬움"; // 초기 값은 쉬움
            difficulty.setText(selectedDifficulty);
        }

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

        // 버튼들에 대해 클릭 이벤트 리스너를 설정
        for (int i = 0; i < ex.length; i++) {
            final int index = i;
            ex[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isTimerRunning) { // 타이머가 실행 중이 아닐 때만 타이머 시작
                        startTimer(index);
                    } else {
                        Toast.makeText(Video.this, "다른 타이머가 실행 중입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                    totalTimesInMillis = new long[]{1 * 60 * 1000, 2 * 60 * 1000, 3 * 60 * 1000, 4 * 60 * 1000};
                } else if (selectedDifficulty.equals("보통")) {
                    totalTimesInMillis = new long[]{2 * 10 * 1000, 4 * 60 * 1000, 6 * 60 * 1000, 7 * 60 * 1000};
                } else if (selectedDifficulty.equals("어려움")) {
                    totalTimesInMillis = new long[]{4 * 10 * 1000, 4 * 60 * 1000, 12 * 60 * 1000, 14 * 60 * 1000};
                }
            }
        });
        builder.show();
    }
    // 타이머 시작 메소드
    private void startTimer(final int index) {
        // 기존에 존재하는 타이머를 취소하고 새로운 타이머를 생성하여 시작
        if (countDownTimers[index] != null) {
            countDownTimers[index].cancel();
        }

        ex[index].setVisibility(View.GONE);
        exTime[index].setVisibility(View.VISIBLE);
        countDownTimers[index] = new CountDownTimer(totalTimesInMillis[index], 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 남은 시간을 분과 초로 변환하여 버튼에 표시
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
                exTime[index].setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                exTime[index].setText("END"); // 타이머가 종료되면 버튼 텍스트를 "END"로 변경
                // 타이머가 끝난 후 처리할 작업을 추가 가능
                isTimerRunning = false; // 타이머 종료 시 isTimerRunning을 false로 변경

                ex[index].setVisibility(View.VISIBLE);
                exTime[index].setVisibility(View.GONE);
            }
        };
        countDownTimers[index].start();
        isTimerRunning = true; // 타이머 시작 시 isTimerRunning을 true로 변경
    }
}