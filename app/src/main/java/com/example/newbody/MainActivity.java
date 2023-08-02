package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_RUN_KEY = "firstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences에서 "firstRun" 키의 값을 불러옴
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean(FIRST_RUN_KEY, true);

        View button = findViewById(R.id.button);

        // 처음 실행되는 경우에만 실행
        if (isFirstRun) {
            // SharedPreferences에 "firstRun" 값을 false로 저장하여 다음에 앱이 실행될 때는 이 부분이 실행되지 않도록 함
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_RUN_KEY, false);
            editor.apply();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MainActivityA.class);
                    startActivity(intent);
                }
            });
        }else{
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                    startActivity(intent);
                }
            });
        }
    }
}