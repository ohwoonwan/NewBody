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

        startVideo();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
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

}
