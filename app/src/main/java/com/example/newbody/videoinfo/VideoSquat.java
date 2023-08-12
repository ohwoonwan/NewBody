package com.example.newbody.videoinfo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.R;

public class VideoSquat extends AppCompatActivity {

    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_squat);

        mVideoView = findViewById(R.id.squat);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/squat");
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