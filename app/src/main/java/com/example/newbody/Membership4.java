package com.example.newbody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Membership4 extends AppCompatActivity {

    EditText inputPhoneNum, inputCheckNum;
    TextView time, check;
    Button sendSMSBtn, checkBtn;
    String checkNum;
    private boolean checkNext = false;
    static final int SMS_SEND_PERMISSION = 1;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership4);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        pref = getPreferences(MODE_PRIVATE);
        editor = pref.edit();

        inputPhoneNum = findViewById(R.id.input_phone_num);
        sendSMSBtn = findViewById(R.id.send_sms_button);
        inputCheckNum = findViewById(R.id.input_check_num);
        checkBtn = findViewById(R.id.check_button);
        time = findViewById(R.id.timeText);
        check = findViewById(R.id.checkPhoneNum);
        View register = findViewById(R.id.button);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
                Toast.makeText(this, "SMS 권한이 필요합니다. ", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_SEND_PERMISSION);
        }

        sendSMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = "+82 " + inputPhoneNum.getText().toString().substring(1);
                startPhoneNumberVerification(num);
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPhoneNumberWithCode(verificationId, inputCheckNum.getText().toString());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNext){
                    savePhoneNumber(user);

                    Intent intent = new Intent(getApplicationContext(), Membership5.class);
                    intent.putExtra("uid", user.getUid());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(Membership4.this, "전화번호 인증을 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(Membership4.this, "인증 불가" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verId, PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = verId;
                        Toast.makeText(Membership4.this, "인증번호가 발송되었습니다. ", Toast.LENGTH_SHORT).show();
                        time.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        check.setText("인증이 완료되었습니다. ");
                        check.setTextColor(Color.parseColor("#00cc00"));
                        checkNext = true;
                    } else {
                        check.setText("인증번호가 일치하지 않습니다. ");
                        check.setTextColor(Color.parseColor("#ff3300"));
                    }
                });
    }

    private void savePhoneNumber(FirebaseUser user){
        Map<String, Object> userData = new HashMap<>();
        final String collectionName = "users";
        userData.put("phone", inputPhoneNum.getText().toString());

        DocumentReference userRecordRef = db.collection(collectionName).document(user.getUid());
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