package com.example.newbody;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MemberChangeActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    EditText nameEditText, genderEditText, birthEditText, weightEditText, heightEditText;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_change);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        nameEditText = findViewById(R.id.name_fix);
        genderEditText = findViewById(R.id.gender_fix);
        birthEditText = findViewById(R.id.birth_fix);
        weightEditText = findViewById(R.id.weight_fix);
        heightEditText = findViewById(R.id.height_fix);

        View updateButton = findViewById(R.id.fix_button);

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }else{
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    birthEditText.setText(document.getString("birth"));
                                    genderEditText.setText(document.getString("gender"));
                                    nameEditText.setText(document.getString("name"));
                                    weightEditText.setText(document.getString("weight"));
                                    heightEditText.setText(document.getString("height"));
                                }
                            } else {
                            }
                        }
                    });
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String gender = genderEditText.getText().toString();
                String birth = birthEditText.getText().toString();
                String weight = weightEditText.getText().toString();
                String height = heightEditText.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(MemberChangeActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(gender)) {
                    Toast.makeText(MemberChangeActivity.this, "성별을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(birth)) {
                    Toast.makeText(MemberChangeActivity.this, "생일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(weight)) {
                    Toast.makeText(MemberChangeActivity.this, "몸무게를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(height)) {
                    Toast.makeText(MemberChangeActivity.this, "키를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> userData = new HashMap<>();
                if (user != null) {
                    userData.put("name", name);
                    userData.put("gender", gender);
                    userData.put("birth", birth);
                    userData.put("weight", weight);
                    userData.put("height", height);
                }

                db.collection("users").document(user.getUid())
                        .update(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MemberChangeActivity.this, "Data updated successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Menu.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MemberChangeActivity.this, "Failed to update data.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}