package com.example.newbody.workout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.newbody.R;
import com.example.newbody.Video;
import com.example.newbody.VoiceRecognitionService;

import java.util.ArrayList;

public class Home_Training_Squat2 extends AppCompatActivity {

    private Button prevButton;
    private Button nextButton;
    private Button timerButton; // 타이머 버튼
    private TextView timerTextView;

    private VoiceTask voiceTask;
    private TextView CountView;
    private String selectedDifficulty;
    private CountDownTimer timer;
    private long timeLeftInMillis; // 타이머 남은 시간을 저장할 변수
    private boolean isTimerRunning; // 타이머가 동작 중인지 여부를 저장할 변수
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_training_squat2);

        Intent intentV = new Intent(this, VoiceRecognitionService.class);
        startService(intentV);

        CountView = findViewById(R.id.CountView4);

        mVideoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/squat");
        mVideoView.setVideoURI(uri);

        startVideo();//반복재생

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        // 이전 액티비티로 이동하는 버튼
        prevButton = findViewById(R.id.prev_button4);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 이전 액티비티로 이동
            }
        });

        // 다음 액티비티로 이동하는 버튼
        nextButton = findViewById(R.id.next_button4);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToHome_Training_Squaut1(); // Home_Training_Squaut1로 이동
            }
        });

        // 타이머 텍스트뷰
        timerTextView = findViewById(R.id.timerTextView4);

        // 타이머 버튼
        timerButton = findViewById(R.id.startbutton4);
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    pauseTimer(); // 타이머 일시정지
                    pauseVideo(); // 동영상 일시정지
                } else {
                    startTimer(); // 타이머 시작
                    startVideo(); // 동영상 재생
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
                timerDuration = 52 * 1000; // 분*초*1000
                CountView.setText("주어진 시간 동안 15개를 성공 시키세요!");
                break;
            case "보통":
                timerDuration = 65 * 1000; // 분*초*1000
                CountView.setText("주어진 시간 동안 20개를 성공 시키세요!");
                break;
            case "어려움":
                timerDuration = 78 * 1000; // 분*초*1000
                CountView.setText("주어진 시간 동안 25개를 성공 시키세요!");
                break;
            default:
                timerDuration = 52 * 1000; // 기본값은 30초로 설정

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
    private void startVideo() {
        if (mVideoView != null) {
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 동영상이 끝나면 다시 재생
                    mp.start();
                }
            });

            if (!mVideoView.isPlaying()) {
                mVideoView.start();
            }
        }
    }

    private void pauseVideo() {
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
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
    public void onBackPressed() {
        // 액티비티 넘어가면 종료되면 타이머도 함께 종료
        if (timer != null) {
            timer.cancel();
        }
        Intent prevIntent = new Intent(Home_Training_Squat2.this, Home_Training_Rest1.class);
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
        Intent intentSub2 = new Intent(Home_Training_Squat2.this, Home_Training_Rest2.class);
        intentSub2.putExtra("difficulty", selectedDifficulty); // 선택된 난이도를 Home_Training_Squaut1로 전달
        startActivity(intentSub2);
        finish();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
            if (resultCode == 1) {
                if (voiceTask != null) {  // 이미 실행 중인 작업이 있다면 취소
                    voiceTask.cancel(true);
                }
                voiceTask = new VoiceTask();  // 새로운 작업 인스턴스 생성
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
            if(str.equals("일시정지") || str.equals("정지") || str.equals("시작") || str.equals("재시작") || str.equals("재생") || str.equals("일시 정지")){
                if (isTimerRunning) {
                    pauseTimer(); // 타이머 일시정지
                    pauseVideo(); // 동영상 일시정지
                } else {
                    startTimer(); // 타이머 시작
                    startVideo(); // 동영상 재생
                }
            }else if(str.equals("이전")){
                onBackPressed();
            }else if(str.equals("다음")){
                moveToHome_Training_Squaut1();
            }else if(str.equals("나가기") || str.equals("종료")){
                Intent intent = new Intent(this, Video.class);
                startService(intent);
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
            // 비동기 작업이 취소되었는지 확인
            if (isCancelled()) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // 비동기 작업이 취소되었는지 확인
            if (!isCancelled()) {
                getVoice();
            }
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
        pauseVideo(); // 동영상 일시정지

        if (timer != null) {
            timer.cancel();
        }

        if (voiceTask != null) {
            voiceTask.cancel(true); // 비동기 작업 취소
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mVideoView.stopPlayback(); // 비디오 정지 및 리소스 해제

        // 브로드캐스트 리시버 등록 해제
        unregisterReceiver(receiver);
    }
}

