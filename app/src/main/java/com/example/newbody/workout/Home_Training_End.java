package com.example.newbody.workout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.newbody.Home;
import com.example.newbody.R;
import com.example.newbody.Video;

public class Home_Training_End extends AppCompatActivity {

    private TextView timerTextView;

    private CountDownTimer timer;
    private long timeLeftInMillis = 10 * 1000; // 10초로 고정
    private boolean isTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_training_end);

        timerTextView = findViewById(R.id.timerTextView14);

        startTimer();
    }

    private void startTimer() {
        if (!isTimerRunning) {
            timer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    long seconds = millisUntilFinished / 1000;
                    timerTextView.setText(String.valueOf(seconds));
                }

                @Override
                public void onFinish() {
                    moveToNextScreen();
                }
            }.start();

            isTimerRunning = true;
        }
    }

    private void moveToNextScreen() {
        if (timer != null) {
            timer.cancel();
        }
        // 현재 액티비티에서 이전에 열려 있던 모든 액티비티를 종료하는 코드
        Intent intent = new Intent(getApplicationContext(), Video.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}

