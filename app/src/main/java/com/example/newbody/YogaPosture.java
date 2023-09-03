package com.example.newbody;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newbody.posture.PostureInfo;

import java.util.ArrayList;

public class YogaPosture extends AppCompatActivity {

    private View ex_start;
    private View []yoga = new View[3];
    private TextView[]yogaName = new TextView[3];
    private TextView selectE;
    private View catView, downDogView, cobraView;
    private Button prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga_posture);

        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);

        initViews();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }
        });

        yoga[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(yogaName[0].getText());
            }
        });
        yoga[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(yogaName[1].getText());
            }
        });
        yoga[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(yogaName[2].getText());
            }
        });

        ex_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectE.getText().equals("운동")){
                    Toast.makeText(YogaPosture.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), PostureInfo.class);
                intent.putExtra("exercise", selectE.getText());
                startActivity(intent);
                finish();
            }
        });
    }

    public void initViews(){
        prev = findViewById(R.id.prevButtonYogaPosture);
        ex_start = findViewById(R.id.start_button);
        yoga[0] = findViewById(R.id.yoga_button1);
        yoga[1] = findViewById(R.id.yoga_button2);
        yoga[2] = findViewById(R.id.yoga_button3);
        yogaName[0] = findViewById(R.id.yoga1_name);
        yogaName[1] = findViewById(R.id.yoga2_name);
        yogaName[2] = findViewById(R.id.yoga3_name);
        selectE = findViewById(R.id.exercise_select);
        catView = findViewById(R.id.catYogaView1);
        downDogView = findViewById(R.id.downdogYogaView1);
        cobraView = findViewById(R.id.cobraYogaView1);
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
            if(str.equals("고양이") || str.equals("고양이자세") || str.equals("고양이 자세") || str.equals("다운 독") || str.equals("다운독") || str.equals("다운독 자세") ||
                    str.equals("코브라") || str.equals("코브라자세") || str.equals("코브라 자세")){
                if(str.equals("고양이") || str.equals("고양이자세") || str.equals("고양이 자세")){
                    selectE.setText(yogaName[0].getText());
                }else if(str.equals("다운 독") || str.equals("다운독") || str.equals("다운독 자세")){
                    selectE.setText(yogaName[1].getText());
                }else if(str.equals("코브라") || str.equals("코브라자세") || str.equals("코브라 자세")){
                    selectE.setText(yogaName[2].getText());
                }
            }else if(str.equals("시작") || str.equals("운동 시작")){
                if(selectE.getText().equals("운동")){
                    Toast.makeText(YogaPosture.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), PostureInfo.class);
                intent.putExtra("exercise", selectE.getText());
                startActivity(intent);
                finish();
            }else if(str.equals("이전")){
                Intent intent = new Intent(getApplicationContext(), Menu.class);
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
        // 브로드캐스트 리시버 등록 해제
        unregisterReceiver(receiver);
    }
}