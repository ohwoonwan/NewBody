package com.example.newbody.workout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.newbody.R;

public class Home_Training_side_lateral_raise1 extends AppCompatActivity {

    private Button prevButton;
    private Button nextButton;
    private Button timerButton; // 타이머 버튼
    private TextView timerTextView;

    private String selectedDifficulty;
    private CountDownTimer timer;
    private long timeLeftInMillis; // 타이머 남은 시간을 저장할 변수
    private boolean isTimerRunning; // 타이머가 동작 중인지 여부를 저장할 변수

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_training_side_lateral_raise1);


        mVideoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/side");
        mVideoView.setVideoURI(uri);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        // 이전 액티비티로 이동하는 버튼
        prevButton = findViewById(R.id.prev_button18);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 이전 액티비티로 이동
            }
        });

        // 다음 액티비티로 이동하는 버튼
        nextButton = findViewById(R.id.next_button18);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToHome_Training_Squaut1(); // Home_Training_Squaut1로 이동
            }
        });

        // 타이머 텍스트뷰
        timerTextView = findViewById(R.id.timerTextView18);

        // 타이머 버튼
        timerButton = findViewById(R.id.startbutton18);
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    pauseTimer(); // 타이머가 동작 중이면 일시정지
                } else {
                    startTimer(); // 타이머가 동작 중이 아니면 시작
                }
            }
        });

        // MainActivity로부터 선택한 난이도를 받아옴
        Intent intent = getIntent();
        selectedDifficulty = intent.getStringExtra("difficulty");

// 난이도 데이터가 전달되지 않은 경우 기본값으로 설정
        if (selectedDifficulty == null) {
            selectedDifficulty = "쉬움";
        }

// 타이머 자동 실행
        startTimer();
    }

    private void startTimer() {
        int timerDuration; // 타이머 길이를 담을 변수
        switch (selectedDifficulty) {
            case "쉬움":
                timerDuration = 1 * 10 * 1000; // 분*초*1000
                break;
            case "보통":
                timerDuration = 1 * 20 * 1000; // 분*초*1000
                break;
            case "어려움":
                timerDuration = 1 * 30 * 1000; // 분*초*1000
                break;
            default:
                timerDuration = 1 * 30 * 1000; // 기본값은 30초로 설정
        }

        if (isTimerRunning) {
            // 타이머가 이미 동작 중인 경우
            pauseTimer();
        } else {
            // 타이머가 동작 중이 아닌 경우
            // 타이머가 동작하는 동안 prev 버튼과 next 버튼을 비활성화
            //prevButton.setEnabled(false);
            //nextButton.setEnabled(false);
            // 남은 시간이 저장되어 있으면 해당 시간부터 타이머 시작
            if (timeLeftInMillis > 0) {
                timer = new CountDownTimer(timeLeftInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // 남은 시간을 분과 초로 변환하여 표시
                        timeLeftInMillis = millisUntilFinished;
                        long minutes = millisUntilFinished / 60000;
                        long seconds = (millisUntilFinished % 60000) / 1000;
                        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
                        timerTextView.setText(timeLeftFormatted);
                    }

                    @Override
                    public void onFinish() {
                        // 타이머가 끝나면 prev 버튼과 next 버튼을 다시 활성화
                        prevButton.setEnabled(true);
                        nextButton.setEnabled(true);
                        timerTextView.setText("타이머 종료");
                        timerButton.setText("START");
                        isTimerRunning = false;
                        moveToHome_Training_Squaut1();
                    }
                }.start();
            } else {
                // 저장된 시간이 없으면 새로운 타이머 시작
                timer = new CountDownTimer(timerDuration, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // 남은 시간을 분과 초로 변환하여 표시
                        timeLeftInMillis = millisUntilFinished;
                        long minutes = millisUntilFinished / 60000;
                        long seconds = (millisUntilFinished % 60000) / 1000;
                        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
                        timerTextView.setText(timeLeftFormatted);
                    }

                    @Override
                    public void onFinish() {
                        // 타이머가 끝나면 prev 버튼과 next 버튼을 다시 활성화
                        //prevButton.setEnabled(true);
                        //nextButton.setEnabled(true);
                        //timerTextView.setText("타이머 종료");
                        timerButton.setText("START");
                        isTimerRunning = false;
                        moveToHome_Training_Squaut1();
                    }
                }.start();
            }

            isTimerRunning = true;
            timerButton.setText("PAUSE");
        }
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null; // timer 객체 초기화
        }
        isTimerRunning = false;
        timerButton.setText("START");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료되면 타이머도 함께 종료
        if (timer != null) {
            timer.cancel();
        }
    }
    @Override
    public void onBackPressed() {
        // 액티비티 넘어가면 종료되면 타이머도 함께 종료
        if (timer != null) {
            timer.cancel();
        }
        Intent prevIntent = new Intent(Home_Training_side_lateral_raise1.this, Home_Training_Rest8.class);
        prevIntent.putExtra("difficulty", selectedDifficulty); // 선택된 난이도를 이전 액티비티로 전달
        startActivity(prevIntent);
        finish();

        // 이전 액티비티를 재생성하고 초기화하기 위해 다음 코드 실행
        overridePendingTransition(0, 0);
        recreate();
        overridePendingTransition(0, 0);
    }





    private void moveToHome_Training_Squaut1() {
        // 액티비티 넘어가면 종료되면 타이머도 함께 종료
        if (timer != null) {
            timer.cancel();
        }
        Intent intentSub2 = new Intent(Home_Training_side_lateral_raise1.this,Home_Training_Rest9.class);
        intentSub2.putExtra("difficulty", selectedDifficulty); // 선택된 난이도를 Home_Training_Squaut1로 전달
        startActivity(intentSub2);
    }
}

