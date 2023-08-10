package com.example.newbody.posture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.example.newbody.Posture;
import com.example.newbody.R;
import com.example.newbody.Record;

public class PostureInfo extends AppCompatActivity {

    private Button start, prev;
    private VideoView postureVideo;
    private String exName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posture_info);

        prev = findViewById(R.id.prevButtonPostureInfo);
        start = findViewById(R.id.startPosture);
        postureVideo = findViewById(R.id.postureVideo);

        Intent intent = getIntent();
        exName = intent.getStringExtra("exercise");

        Uri uri = null;
        if (exName.equals("스쿼트")) {
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/squat");
        }else if(exName.equals("푸쉬업")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/pushups");
        }else if(exName.equals("덤벨 숄더 프레스")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/dumbbell");
        }else if(exName.equals("사이드 레터럴 레이즈")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/side");
        }else if(exName.equals("레그 레이즈")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/legraise");
        }
        postureVideo.setVideoURI(uri);

        postureVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        postureVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 동영상 재생이 완료되면 다시 시작
                postureVideo.start();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Posture.class);
                startActivity(intent);
                finish();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentN = null;
                if (exName.equals("스쿼트")) {
                    intentN = new Intent(getApplicationContext(), PostureSquat.class);
                }else if(exName.equals("푸쉬업")){
                    intentN = new Intent(getApplicationContext(), PosturePushup.class);
                }else if(exName.equals("덤벨 숄더 프레스")){
                    intentN = new Intent(getApplicationContext(), PostureDumbbell.class);
                }else if(exName.equals("사이드 레터럴 레이즈")){
                    intentN = new Intent(getApplicationContext(), PostureSide.class);
                }else if(exName.equals("레그 레이즈")){
                    intentN = new Intent(getApplicationContext(), PostureLeg.class);
                }
                startActivity(intentN);
                finish();
            }
        });


    }
}