package com.example.newbody.videoinfo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.R;
import com.example.newbody.mainInfo.MainActivityB;

public class VideoWarmup extends AppCompatActivity {

    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_warmup);

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


    }

}
