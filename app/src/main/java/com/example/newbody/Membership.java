package com.example.newbody;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class Membership extends AppCompatActivity {

    EditText name, email, password;
    FirebaseAuth mAuth;

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            Intent intent = new Intent(getApplicationContext(), Menu.class);
//            startActivity(intent);
//            finish();
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        View register = findViewById(R.id.register);
        Button login = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameS, emailS, passwordS;
                nameS = String.valueOf(name.getText());
                emailS = String.valueOf(email.getText());
                passwordS = String.valueOf(password.getText());

                if(TextUtils.isEmpty(nameS)){
                    Toast.makeText(Membership.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(emailS)){
                    Toast.makeText(Membership.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passwordS)){
                    Toast.makeText(Membership.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase Authentication을 이용하여 사용자를 생성합니다.
                mAuth.createUserWithEmailAndPassword(emailS, passwordS)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Membership.this, "Account created.", Toast.LENGTH_SHORT).show();
                                    // 사용자 생성에 성공한 경우
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // Firestore에 사용자의 이름을 저장합니다.
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("name", name);
                                    Intent intent = new Intent(getApplicationContext(), Membership2.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Membership.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
