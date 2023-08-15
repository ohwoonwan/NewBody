package com.example.newbody.PushMessage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.newbody.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AndroidNewFCM extends AppCompatActivity {

    @BindView(R.id.btn_get_token)
    Button btn_get_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_new_fcm);
        ButterKnife.bind(this);

        btn_get_token.setOnClickListener((view)->{
            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String token) {
                            Log.d(Utils.TAG,token);
                        }
                    })
                    .addOnFailureListener(e ->  {
                        Toast.makeText(this,"Failed to get token", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}