package com.example.newbody.videoinfo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.R;

public class VideoLegRaise extends AppCompatActivity {

    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_legraise);

        mVideoView = findViewById(R.id.legraise);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/legraise");
        mVideoView.setVideoURI(uri);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
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

}