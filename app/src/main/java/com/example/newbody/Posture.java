package com.example.newbody;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.posture.PostureInfo;

import java.util.ArrayList;


public class Posture extends AppCompatActivity {
    private View ex_start;
    private View []ex = new View[11];
    private TextView[]exName = new TextView[11];
    private TextView selectE;
    private Button prev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_posture);

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
        ex[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[0].getText());
            }
        });
        ex[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[1].getText());
            }
        });
        ex[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[2].getText());
            }
        });

        ex[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[3].getText());
            }
        });

        ex[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[4].getText());
            }
        });

        ex[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[5].getText());
            }
        });

        ex[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[6].getText());
            }
        });

        ex[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[7].getText());
            }
        });

        ex[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[8].getText());
            }
        });

        ex[9].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[9].getText());
            }
        });

        ex[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[10].getText());
            }
        });


        ex_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectE.getText().equals("운동")){
                    Toast.makeText(Posture.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), PostureInfo.class);
                intent.putExtra("exercise", selectE.getText());
                startActivity(intent);
                finish();
            }
        });

    }
    private void initViews(){
        ex_start = findViewById(R.id.start_button);
        ex[0] = findViewById(R.id.ex_button1);
        ex[1] = findViewById(R.id.ex_button2);
        ex[2] = findViewById(R.id.ex_button3);
        ex[3] = findViewById(R.id.ex_button4);
        ex[4] = findViewById(R.id.ex_button5);
        ex[5] = findViewById(R.id.ex_button6);
        ex[6] = findViewById(R.id.ex_button7);
        ex[7] = findViewById(R.id.ex_button8);
        ex[8] = findViewById(R.id.ex_button9);
        ex[9] = findViewById(R.id.ex_button10);
        ex[10] = findViewById(R.id.ex_button11);
        exName[0] = findViewById(R.id.ex1_name);
        exName[1] = findViewById(R.id.ex2_name);
        exName[2] = findViewById(R.id.ex3_name);
        exName[3] = findViewById(R.id.ex4_name);
        exName[4] = findViewById(R.id.ex5_name);
        exName[5] = findViewById(R.id.ex6_name);
        exName[6] = findViewById(R.id.ex7_name);
        exName[7] = findViewById(R.id.ex8_name);
        exName[8] = findViewById(R.id.ex9_name);
        exName[9] = findViewById(R.id.ex10_name);
        exName[10] = findViewById(R.id.ex11_name);
        selectE = findViewById(R.id.exercise_select);
        prev = findViewById(R.id.prevButton);

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
            if(str.equals("스쿼트") || str.equals("푸쉬업") || str.equals("푸시업") || str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스") ||
                    str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈") || str.equals("레그 레이즈") || str.equals("레그레이즈") ||
                        str.equals("덤벨 컬") || str.equals("컬") || str.equals("덤벨 플라이") || str.equals("플라이") || str.equals("덤벨 트라이셉스 익스텐션") ||
                            str.equals("덤벨 트라이셉스 익스텐션") || str.equals("트라이셉스") || str.equals("덤벨컬") || str.equals("덤벨플라이") ||
                                str.equals("덤벨트라이셉스익스텐션")){
                if(str.equals("스쿼트")){
                    selectE.setText(exName[0].getText());
                }else if(str.equals("푸쉬업") || str.equals("푸시업")){
                    selectE.setText(exName[1].getText());
                }else if(str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스")){
                    selectE.setText(exName[2].getText());
                }else if(str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈")){
                    selectE.setText(exName[3].getText());
                }else if(str.equals("레그 레이즈") || str.equals("레그레이즈")){
                    selectE.setText(exName[4].getText());
                }else if(str.equals("덤벨 컬") || str.equals("컬") || str.equals("덤벨컬")) {
                    selectE.setText(exName[5].getText());
                }else if(str.equals("덤벨 플라이") || str.equals("플라이") || str.equals("덤벨플라이")) {
                    selectE.setText(exName[6].getText());
                }else if(str.equals("덤벨 트라이셉스 익스텐션") || str.equals("트라이셉스") || str.equals("덤벨트라이셉스익스텐션")) {
                    selectE.setText(exName[7].getText());
                }
            }else if(str.equals("시작") || str.equals("운동 시작")){
                if(selectE.getText().equals("운동")){
                    Toast.makeText(Posture.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
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
