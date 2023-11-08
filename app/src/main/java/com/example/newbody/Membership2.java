package com.example.newbody;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Membership2 extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership2);

        View register = findViewById(R.id.button);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText) findViewById(R.id.username)).getText().toString();
                String gender = ((EditText) findViewById(R.id.sex)).getText().toString();
                String birth = ((EditText) findViewById(R.id.birth)).getText().toString();
                String weight = ((EditText) findViewById(R.id.weight)).getText().toString();
                String height = ((EditText) findViewById(R.id.height)).getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(Membership2.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(gender)) {
                    Toast.makeText(Membership2.this, "성별을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(birth)) {
                    Toast.makeText(Membership2.this, "생일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(weight)) {
                    Toast.makeText(Membership2.this, "몸무게를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(height)) {
                    Toast.makeText(Membership2.this, "키를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();

                    FirebaseUser user = mAuth.getCurrentUser();
                    Map<String, Object> userData = new HashMap<>();
                    if (user != null) {
                        userData.put("name", name);
                        userData.put("gender", gender);
                        userData.put("birth", birth);
                        userData.put("weight", weight);
                        userData.put("height", height);
                        userData.put("fcmToken", token); // 토큰 값을 저장합니다.
                        userData.put("uid", user.getUid());
                        db.collection("users").document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(getApplicationContext(), Membership5.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                });

            }
        });

    }
}
