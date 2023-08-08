package com.example.newbody.videoinfo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.R;

public class VideoPushups extends AppCompatActivity {

    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_pushups);

        mVideoView = findViewById(R.id.pushups);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/pushups");
        mVideoView.setVideoURI(uri);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

}