package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ManagerLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);

        View login = findViewById(R.id.managerLogin);
        EditText password = findViewById(R.id.managerPassword);

        String passwordManager = "1234";
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equals(passwordManager)){
                    Intent intent = new Intent(getApplicationContext(), ManagerMenu.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(ManagerLogin.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}