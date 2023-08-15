package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class Target extends AppCompatActivity {

    private Button prev, change;
    private boolean checkChange = true;
    private TextView []exName = new TextView[5];
    private EditText []ex = new EditText[5];
    private TextView []exNum = new TextView[5];
    private Switch []switches = new Switch[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        initViews();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkChange){
                    changeOn();
                    checkChange = false;
                }else{
                    changeOff();
                    checkChange = true;
                }
            }
        });
    }
    public void initViews(){
        prev = findViewById(R.id.prevButtonTarget);
        change = findViewById(R.id.changeButton);
        exName[0] = findViewById(R.id.ex1_name);
        exName[1] = findViewById(R.id.ex2_name);
        exName[2] = findViewById(R.id.ex3_name);
        exName[3] = findViewById(R.id.ex4_name);
        exName[4] = findViewById(R.id.ex5_name);
        ex[0] = findViewById(R.id.ex1_cnt);
        ex[1] = findViewById(R.id.ex2_cnt);
        ex[2] = findViewById(R.id.ex3_cnt);
        ex[3] = findViewById(R.id.ex4_cnt);
        ex[4] = findViewById(R.id.ex5_cnt);
        exNum[0] = findViewById(R.id.targetNum1);
        exNum[1] = findViewById(R.id.targetNum2);
        exNum[2] = findViewById(R.id.targetNum3);
        exNum[3] = findViewById(R.id.targetNum4);
        exNum[4] = findViewById(R.id.targetNum5);
        switches[0] = findViewById(R.id.switch1);
        switches[1] = findViewById(R.id.switch2);
        switches[2] = findViewById(R.id.switch3);
        switches[3] = findViewById(R.id.switch4);
        switches[4] = findViewById(R.id.switch5);

        switches[0].setVisibility(View.GONE);
        switches[1].setVisibility(View.GONE);
        switches[2].setVisibility(View.GONE);
        switches[3].setVisibility(View.GONE);
        switches[4].setVisibility(View.GONE);
        ex[0].setVisibility(View.GONE);
        ex[1].setVisibility(View.GONE);
        ex[2].setVisibility(View.GONE);
        ex[3].setVisibility(View.GONE);
        ex[4].setVisibility(View.GONE);
        exNum[0].setVisibility(View.VISIBLE);
        exNum[1].setVisibility(View.VISIBLE);
        exNum[2].setVisibility(View.VISIBLE);
        exNum[3].setVisibility(View.VISIBLE);
        exNum[4].setVisibility(View.VISIBLE);
        change.setText("수정");
    }

    public void changeOff(){
        exNum[0].setText(ex[0].getText().toString());
        exNum[1].setText(ex[1].getText().toString());
        exNum[2].setText(ex[2].getText().toString());
        exNum[3].setText(ex[3].getText().toString());
        exNum[4].setText(ex[4].getText().toString());
        switches[0].setVisibility(View.GONE);
        switches[1].setVisibility(View.GONE);
        switches[2].setVisibility(View.GONE);
        switches[3].setVisibility(View.GONE);
        switches[4].setVisibility(View.GONE);
        ex[0].setVisibility(View.GONE);
        ex[1].setVisibility(View.GONE);
        ex[2].setVisibility(View.GONE);
        ex[3].setVisibility(View.GONE);
        ex[4].setVisibility(View.GONE);
        exNum[0].setVisibility(View.VISIBLE);
        exNum[1].setVisibility(View.VISIBLE);
        exNum[2].setVisibility(View.VISIBLE);
        exNum[3].setVisibility(View.VISIBLE);
        exNum[4].setVisibility(View.VISIBLE);
        change.setText("수정");
    }
    public void changeOn(){
        ex[0].setText(exNum[0].getText().toString());
        ex[1].setText(exNum[1].getText().toString());
        ex[2].setText(exNum[2].getText().toString());
        ex[3].setText(exNum[3].getText().toString());
        ex[4].setText(exNum[4].getText().toString());
        switches[0].setVisibility(View.VISIBLE);
        switches[1].setVisibility(View.VISIBLE);
        switches[2].setVisibility(View.VISIBLE);
        switches[3].setVisibility(View.VISIBLE);
        switches[4].setVisibility(View.VISIBLE);
        ex[0].setVisibility(View.VISIBLE);
        ex[1].setVisibility(View.VISIBLE);
        ex[2].setVisibility(View.VISIBLE);
        ex[3].setVisibility(View.VISIBLE);
        ex[4].setVisibility(View.VISIBLE);
        exNum[0].setVisibility(View.INVISIBLE);
        exNum[1].setVisibility(View.INVISIBLE);
        exNum[2].setVisibility(View.INVISIBLE);
        exNum[3].setVisibility(View.INVISIBLE);
        exNum[4].setVisibility(View.INVISIBLE);
        change.setText("완료");
    }

}