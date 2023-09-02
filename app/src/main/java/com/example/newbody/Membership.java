package com.example.newbody;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Membership extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    EditText email, password, passwordCheck;
    TextView check_text;
    private boolean passwordCheckCheck = false;
    View google;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        View register = findViewById(R.id.register);
        Button login = findViewById(R.id.login);

        google = findViewById(R.id.google);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordCheck = findViewById(R.id.passwordCheck);
        check_text = findViewById(R.id.check_text);

        // GoogleSignInClient 객체 초기화
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        passwordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(password.getText().toString().equals(passwordCheck.getText().toString())) {
                    passwordCheckCheck = true;
                    check_text.setText("비밀번호와 일치합니다. ");
                    check_text.setTextColor(Color.parseColor("#00cc00"));
                } else {
                    passwordCheckCheck = false;
                    check_text.setText("비밀번호를 확인해주세요. ");
                    check_text.setTextColor(Color.parseColor("#ff3300"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailS, passwordS, passwordCheckS;
                emailS = String.valueOf(email.getText());
                passwordS = String.valueOf(password.getText());
                passwordCheckS = String.valueOf(passwordCheck.getText());

                if(TextUtils.isEmpty(emailS)){
                    Toast.makeText(Membership.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passwordS)){
                    Toast.makeText(Membership.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passwordCheckS)){
                    Toast.makeText(Membership.this, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(passwordCheckCheck == true){
                    // Firebase Authentication을 이용하여 사용자를 생성합니다.
                    mAuth.createUserWithEmailAndPassword(emailS, passwordS)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), Membership1.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Membership.this, "회원가입 실패.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(Membership.this, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // GoogleSignInClient.getSignInIntent(...)의 결과 처리
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In이 성공했을 경우, Firebase에 인증
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In 실패
                Toast.makeText(Membership.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Google 로그인 토큰을 이용하여 Firebase에 인증
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), Membership1.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 인증 실패
                            Toast.makeText(Membership.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
