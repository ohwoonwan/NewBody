package com.example.newbody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Membership6 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private TextView pre1, pre2, pre3;
    private RadioButton radioButton1, radioButton2, radioButton3;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership6);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        View register = findViewById(R.id.button);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        pre1 = findViewById(R.id.ex1);
        pre2 = findViewById(R.id.ex2);
        pre3 = findViewById(R.id.ex3);

        Intent intent = getIntent();
        userUid = intent.getStringExtra("uid");

        final String[] selectEx = new String[1];

        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((RadioButton) v).isChecked()) {
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                    selectEx[0] = pre1.getText().toString();
                }
            }
        });

        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((RadioButton) v).isChecked()) {
                    radioButton1.setChecked(false);
                    radioButton3.setChecked(false);
                    selectEx[0] = pre2.getText().toString();
                }
            }
        });

        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((RadioButton) v).isChecked()) {
                    radioButton1.setChecked(false);
                    radioButton2.setChecked(false);
                    selectEx[0] = pre3.getText().toString();
                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!radioButton1.isChecked() && !radioButton2.isChecked() && !radioButton3.isChecked()){
                    Toast.makeText(Membership6.this, "선호하는 운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                savePreference(selectEx[0], user);
                Intent intent = new Intent(getApplicationContext(), Membership3.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void savePreference(String selectEx, FirebaseUser user){
        Map<String, Object> userData = new HashMap<>();
        final String collectionName = "users";
        userData.put("preference2", selectEx);

        DocumentReference userRecordRef = db.collection(collectionName).document(userUid);
        userRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userRecordRef.set(userData, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "Data successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                                    Log.w("Firestore", "Error writing document", e);
                                }
                            });
                } else {
                    Log.d("Firestore", "Failed to get document", task.getException());
                }
            }
        });
    }
}