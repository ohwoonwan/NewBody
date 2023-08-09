package com.example.newbody;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newbody.record.RecordDumbbell;
import com.example.newbody.record.RecordDumbbellCurl;
import com.example.newbody.record.RecordPushup;
import com.example.newbody.record.RecordSidelateralraise;
import com.example.newbody.record.RecordSquat;

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
                    Intent intent = new Intent(Record.this, RecordDumbbellCurl.class);
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
}
