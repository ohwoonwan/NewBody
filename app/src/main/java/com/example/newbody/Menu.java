package com.example.newbody;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Home home;
    private Picture picture;
    private Calendar calendar;
    private Person person;

    private int selectMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        selectMenu = 0;
        bottomNavigationView = findViewById(R.id.bottomNavi);

        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        selectMenu = 0;
                        setFrag(0);
                        break;
                    case R.id.action_picture:
                        selectMenu = 1;
                        setFrag(1);
                        break;
                    case R.id.action_calendar:
                        selectMenu = 2;
                        setFrag(2);
                        break;
                    case R.id.action_notifications:
                        selectMenu = 3;
                        setFrag(3);
                        break;
                }
                return true;
            }
        });
        home = new Home();
        picture = new Picture();
        calendar = new Calendar();
        person = new Person();

        setFrag(0); // 첫 프래그먼트 화면
    }

    // 프래그먼트 교체가 일어나는 실행문
    private void setFrag(int n){
        // 먼저 리스너를 제거
        bottomNavigationView.setOnNavigationItemSelectedListener(null);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
            case 0:
                ft.replace(R.id.main_frame, home);
                ft.commit();
                bottomNavigationView.setSelectedItemId(R.id.action_home);
                break;
            case 1:
                ft.replace(R.id.main_frame, picture);
                ft.commit();
                bottomNavigationView.setSelectedItemId(R.id.action_picture);
                break;
            case 2:
                ft.replace(R.id.main_frame, calendar);
                ft.commit();
                bottomNavigationView.setSelectedItemId(R.id.action_calendar);
                break;
            case 3:
                ft.replace(R.id.main_frame, person);
                ft.commit();
                bottomNavigationView.setSelectedItemId(R.id.action_notifications);
                break;
        }

        // 다시 리스너를 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        selectMenu = 0;
                        setFrag(0);
                        break;
                    case R.id.action_picture:
                        selectMenu = 1;
                        setFrag(1);
                        break;
                    case R.id.action_calendar:
                        selectMenu = 2;
                        setFrag(2);
                        break;
                    case R.id.action_notifications:
                        selectMenu = 3;
                        setFrag(3);
                        break;
                }
                return true;
            }
        });
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
            if(str.equals("홈") || str.equals("홈 화면")){
                setFrag(0);
            }else if(str.equals("사진") || str.equals("카메라")){
                setFrag(1);
            }else if(str.equals("스케줄러") || str.equals("달력")){
                setFrag(2);
            }else if(str.equals("정보") || str.equals("내 정보")){
                setFrag(3);
            }
            if(selectMenu == 0){
                if(str.equals("자세교정") || str.equals("자세 교정")){
                    Intent intent = new Intent(getApplicationContext(), Posture.class);
                    startActivity(intent);
                }else if(str.equals("홈트레이닝") || str.equals("홈 트레이닝")){
                    Intent intent = new Intent(getApplicationContext(), Video.class);
                    startActivity(intent);
                }else if(str.equals("기록측정") || str.equals("기록 측정")){
                    Intent intent = new Intent(getApplicationContext(), Record.class);
                    startActivity(intent);
                }else if(str.equals("랭킹") || str.equals("순위")){
                    Intent intent = new Intent(getApplicationContext(), Ranking.class);
                    startActivity(intent);
                }
            }else if(selectMenu == 1){
                if(str.equals("사진 촬영") || str.equals("사진촬영")) {
                    ((Picture) picture).takePicture(); // Picture 프래그먼트의 takePicture 메서드 호출
                }
            }else if(selectMenu == 3){
                if(str.equals("수정") || str.equals("정보 수정")){
                    Intent intent = new Intent(getApplicationContext(), MemberChangeActivity.class);
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
