package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.newbody.posture.PostureInfo;


public class Posture extends AppCompatActivity {
    private View ex_start;
    private View []ex = new View[4];
    private TextView[]exName = new TextView[4];
    private TextView selectE;
    private Button prev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_posture);

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

        ex_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        exName[0] = findViewById(R.id.ex1_name);
        exName[1] = findViewById(R.id.ex2_name);
        exName[2] = findViewById(R.id.ex3_name);
        exName[3] = findViewById(R.id.ex4_name);
        selectE = findViewById(R.id.exercise_select);
        prev = findViewById(R.id.prevButton);
    }
}
