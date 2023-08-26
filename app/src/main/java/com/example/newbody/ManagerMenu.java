package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ManagerMenu extends AppCompatActivity {

    private View member, money, satisfaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_menu);

        member = findViewById(R.id.memberManage);
        money = findViewById(R.id.moneyManage);
        satisfaction = findViewById(R.id.satisfactionManage);

        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ManagerMenu.this, "회원 관리 메뉴", Toast.LENGTH_SHORT).show();
            }
        });

        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ManagerMenu.this, "매출 관리 메뉴", Toast.LENGTH_SHORT).show();
            }
        });

        satisfaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ManagerMenu.this, "만족도 관리 메뉴", Toast.LENGTH_SHORT).show();
            }
        });
    }
}