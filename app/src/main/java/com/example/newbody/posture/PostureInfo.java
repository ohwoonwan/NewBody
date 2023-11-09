package com.example.newbody.posture;

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
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.example.newbody.Posture;
import com.example.newbody.R;
import com.example.newbody.Record;
import com.example.newbody.VoiceRecognitionService;
import com.example.newbody.YogaPosture;
import com.example.newbody.record.RecordSquat;
import com.example.newbody.record.RecordSquatMain;
import com.example.newbody.yoga.Cat;
import com.example.newbody.yoga.Cobra;
import com.example.newbody.yoga.DownDog;

import java.util.ArrayList;

public class PostureInfo extends AppCompatActivity {

    private Button start, prev;
    private VideoView postureVideo;
    private String exName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posture_info);

        Intent intentS = new Intent(this, VoiceRecognitionService.class);
        startService(intentS);

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
        }else if(exName.equals("고양이 자세")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/cat");
        }else if(exName.equals("다운 독")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/downdog");
        }else if(exName.equals("코브라 자세")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/cobra");
        }else if(exName.equals("덤벨 컬")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/dumbbellcurl");
        }else if(exName.equals("덤벨 플라이")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/dumbbellfly");
        }else if(exName.equals("덤벨 트라이셉스 익스텐션")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/dumbbelltriceps");
        }else if(exName.equals("플랭크")){
            uri = Uri.parse("android.resource://" + getPackageName() + "/raw/dumbbelltriceps");
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
                Intent intent = null;
                if(exName.equals("고양이 자세") || exName.equals("다운 독") || exName.equals("코브라 자세")){
                    intent = new Intent(getApplicationContext(), YogaPosture.class);
                }else{
                    intent = new Intent(getApplicationContext(), Posture.class);
                }
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
                }else if(exName.equals("고양이 자세")){
                    intentN = new Intent(getApplicationContext(), Cat.class);
                }else if(exName.equals("다운 독")){
                    intentN = new Intent(getApplicationContext(), DownDog.class);
                }else if(exName.equals("코브라 자세")){
                    intentN = new Intent(getApplicationContext(), Cobra.class);
                }else if(exName.equals("덤벨 컬")){
                    intentN = new Intent(getApplicationContext(), PostureCurl.class);
                }else if(exName.equals("덤벨 플라이")){
                    intentN = new Intent(getApplicationContext(), PostureFly.class);
                }else if(exName.equals("덤벨 트라이셉스 익스텐션")){
                    intentN = new Intent(getApplicationContext(), PostureTriceps.class);
                }else if(exName.equals("플랭크")){
                    intentN = new Intent(getApplicationContext(), PostureFlank.class);
                }
                startActivity(intentN);
                finish();
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
            if(str.equals("시작") || str.equals("운동 시작")){
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
                }else if(exName.equals("고양이 자세")){
                    intentN = new Intent(getApplicationContext(), Cat.class);
                }else if(exName.equals("다운 독")){
                    intentN = new Intent(getApplicationContext(), DownDog.class);
                }else if(exName.equals("코브라 자세")){
                    intentN = new Intent(getApplicationContext(), Cobra.class);
                }else if(exName.equals("덤벨 컬")){
                    intentN = new Intent(getApplicationContext(), PostureCurl.class);
                }else if(exName.equals("덤벨 플라이")){
                    intentN = new Intent(getApplicationContext(), PostureFly.class);
                }else if(exName.equals("덤벨 트라이셉스 익스텐션")){
                    intentN = new Intent(getApplicationContext(), PostureTriceps.class);
                }else if(exName.equals("플랭크")){
                    intentN = new Intent(getApplicationContext(), PostureFlank.class);
                }
                startActivity(intentN);
                finish();
            }else if(str.equals("이전")){
                Intent intent = null;
                if(exName.equals("고양이 자세") || exName.equals("다운 독") || exName.equals("코브라 자세")){
                    intent = new Intent(getApplicationContext(), YogaPosture.class);
                }else{
                    intent = new Intent(getApplicationContext(), Posture.class);
                }
                startActivity(intent);
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