package com.example.newbody.record;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.newbody.R;

public class RecordDumbbell extends AppCompatActivity {

    Button start;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_dumbbell);

        start = findViewById(R.id.startDumbbell);
        Intent intent = getIntent();
        time = intent.getLongExtra("time", 0);

        Toast.makeText(getApplicationContext(), "시간 : " + time, Toast.LENGTH_SHORT).show();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordDumbbell.this, RecordDumbbellMain.class);
                intent.putExtra("time", time);
                startActivity(intent);
            }
        });
    }
}