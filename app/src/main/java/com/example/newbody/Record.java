package com.example.newbody;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.record.RecordDumbbell;
import com.example.newbody.record.RecordLegRaise;
import com.example.newbody.record.RecordPushup;
import com.example.newbody.record.RecordSidelateralraise;
import com.example.newbody.record.RecordSquat;

import java.util.ArrayList;

public class Record extends AppCompatActivity {

    private String select_time, select_ex;
    private long[] totalTimesInMillis = {1 * 60 * 1000, 2 * 60 * 1000, 3 * 60 * 1000};
    private Button time;
    private View ex_start;
    private View []ex = new View[5];
    private TextView []exName = new TextView[5];
    private TextView selectT, selectE;
    private int select_num;
    private Button prev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);

        initViews();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });

        ex[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[0].getText());
                select_num = 1;
            }
        });
        ex[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[1].getText());
                select_num = 2;
            }
        });
        ex[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[2].getText());
                select_num = 3;
            }
        });

        ex[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[3].getText());
                select_num = 4;
            }
        });

        ex[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectE.setText(exName[4].getText());
                select_num =5;
            }
        });

        ex_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(select_num == 1){
                    Intent intent = new Intent(Record.this, RecordSquat.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }else if(select_num == 2){
                    Intent intent = new Intent(Record.this, RecordPushup.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }else if(select_num == 3){
                    Intent intent = new Intent(Record.this, RecordDumbbell.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }else if(select_num == 4){
                    Intent intent = new Intent(Record.this, RecordSidelateralraise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }else if(select_num == 5){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }
            }
        });

    }

    public void initViews(){
        time = findViewById(R.id.time);
        ex_start = findViewById(R.id.start_button);
        ex[0] = findViewById(R.id.ex_button1);
        ex[1] = findViewById(R.id.ex_button2);
        ex[2] = findViewById(R.id.ex_button3);
        ex[3] = findViewById(R.id.ex_button4);
        ex[4] = findViewById(R.id.ex_button5);
        exName[0] = findViewById(R.id.ex1_name);
        exName[1] = findViewById(R.id.ex2_name);
        exName[2] = findViewById(R.id.ex3_name);
        exName[3] = findViewById(R.id.ex4_name);
        exName[4] = findViewById(R.id.ex5_name);
        selectT = findViewById(R.id.time_select);
        selectE = findViewById(R.id.exercise_select);
        prev = findViewById(R.id.prevButton);
    }

    private void showTimeDialog() {
        final String[] difficultyOptions = {"1분", "2분", "3분"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("시간 선택");
        builder.setItems(difficultyOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_time = difficultyOptions[which];
                time.setText(select_time);

                // 선택한 난이도에 따라 시간 배열을 업데이트
                if (select_time.equals("1분")) {
                    selectT.setText("1분");
                } else if (select_time.equals("2분")) {
                    selectT.setText("2분");
                } else if (select_time.equals("3분")) {
                    selectT.setText("3분");
                }
            }
        });
        builder.show();
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
            if(str.equals("1분") || str.equals("2분") || str.equals("3분")){
                // 선택한 난이도에 따라 시간 배열을 업데이트
                if (str.equals("1분")) {
                    select_time = "1분";
                    time.setText(select_time);
                    selectT.setText("1분");
                } else if (str.equals("2분")) {
                    select_time = "2분";
                    time.setText(select_time);
                    selectT.setText("2분");
                } else if (str.equals("3분")) {
                    select_time = "3분";
                    time.setText(select_time);
                    selectT.setText("3분");
                }
            }else if(str.equals("스쿼트") || str.equals("푸쉬업") || str.equals("덤벨 숄더 프레스") || str.equals("덤벨") ||
                    str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("레그 레이즈")){
                if(str.equals("스쿼트")){
                    selectE.setText(exName[0].getText());
                    select_num = 1;
                }else if(str.equals("푸쉬업")){
                    selectE.setText(exName[1].getText());
                    select_num = 2;
                }else if(str.equals("덤벨 숄더 프레스") || str.equals("덤벨")){
                    selectE.setText(exName[2].getText());
                    select_num = 3;
                }else if(str.equals("사이드 레터럴 레이즈") || str.equals("사레레")){
                    selectE.setText(exName[3].getText());
                    select_num = 4;
                }else if(str.equals("레그 레이즈")){
                    selectE.setText(exName[4].getText());
                    select_num = 5;
                }
            }else if(str.equals("시작") || str.equals("운동 시작")){
                if(select_num == 1){
                    Intent intent = new Intent(Record.this, RecordSquat.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }else if(select_num == 2){
                    Intent intent = new Intent(Record.this, RecordPushup.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }else if(select_num == 3){
                    Intent intent = new Intent(Record.this, RecordDumbbell.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }else if(select_num == 4){
                    Intent intent = new Intent(Record.this, RecordSidelateralraise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }else if(select_num == 5){
                    Intent intent = new Intent(Record.this, RecordLegRaise.class);
                    if (select_time.equals("1분")) {
                        intent.putExtra("time", totalTimesInMillis[0]);
                    } else if (select_time.equals("2분")) {
                        intent.putExtra("time", totalTimesInMillis[1]);
                    } else if (select_time.equals("3분")) {
                        intent.putExtra("time", totalTimesInMillis[2]);
                    }
                    startActivity(intent);
                }
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
