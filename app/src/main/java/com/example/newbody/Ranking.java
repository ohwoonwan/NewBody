package com.example.newbody;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Ranking extends AppCompatActivity {

    private Button prev, range, exercise, time;
    private String select_time, select_ex, select_range;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initViews();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }
        });

        range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showRangeDialog();}
        });
        exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showExerciseDialog();}
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showTimeDialog();}
        });
    }
    public void initViews(){
        prev = findViewById(R.id.prevButtonRanking);
        range = findViewById(R.id.range);
        exercise = findViewById(R.id.exercise);
        time = findViewById(R.id.timeRanking);
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
            }
        });
        builder.show();
    }

    private void showExerciseDialog() {
        final String[] exOptions = {"스쿼트", "푸쉬업", "덤벨 숄더 프레스", "사이드 레터럴 레이즈", "레그 레이즈"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("운동 선택");
        builder.setItems(exOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_ex = exOptions[which];
                exercise.setText(select_ex);
            }
        });
        builder.show();
    }
    private void showRangeDialog() {
        final String[] rangeOptions = {"전체", "친구"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("범위 선택");
        builder.setItems(rangeOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_range = rangeOptions[which];
                range.setText(select_range);
            }
        });
        builder.show();
    }
}