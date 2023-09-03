package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newbody.posture.PostureInfo;

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
}