package com.example.newbody.videoinfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.R;
import com.example.newbody.Video;
import com.example.newbody.VoiceRecognitionService;
import com.example.newbody.mainInfo.MainActivityB;

import java.util.ArrayList;

public class VideoWarmup extends AppCompatActivity {

    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_warmup);

        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);

        mVideoView = findViewById(R.id.warmup);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/warmup");
        mVideoView.setVideoURI(uri);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 비디오의 가로 세로 크기를 얻기
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();

                // 화면의 너비에 대한 비디오의 비율 계산
                float videoProportion = (float) videoWidth / (float) videoHeight;

                // VideoView의 너비와 높이를 얻기
                int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

                // 화면의 비율 계산
                float screenProportion = (float) screenWidth / (float) screenHeight;

                android.view.ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();

                if (videoProportion > screenProportion) {
                    // 비디오가 화면보다 더 넓은 경우
                    lp.width = screenWidth;
                    lp.height = (int) ((float) screenWidth / videoProportion);
                } else {
                    // 비디오가 화면보다 더 높은 경우
                    lp.width = (int) (videoProportion * (float) screenHeight);
                    lp.height = screenHeight;
                }

                // 계산된 너비와 높이로 VideoView의 크기를 업데이트
                mVideoView.setLayoutParams(lp);

                // 비디오를 시작합니다.
                mp.start();
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 동영상 재생이 완료되면 다시 시작
                mVideoView.start();
            }
        });


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
            if(str.equals("이전") || str.equals("나가기")){
                Intent intent = new Intent(getApplicationContext(), Video.class);
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
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
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

}
