package com.example.newbody;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Menu extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Home home;
    private Picture picture;
    private Friend friend;
    private Person person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.action_picture:
                        setFrag(1);
                        break;
                    case R.id.action_friend:
                        setFrag(2);
                        break;
                    case R.id.action_notifications:
                        setFrag(3);
                        break;
                }
            }
        });
        home = new Home();
        picture = new Picture();
        friend = new Friend();
        person = new Person();

        setFrag(0); // 첫 프래그먼트 화면


    }

    // 프래그먼트 교체가 일어나는 실행문
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
            case 0:
                ft.replace(R.id.main_frame, home);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, picture);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, friend);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, person);
                ft.commit();
                break;
        }
    }
}
