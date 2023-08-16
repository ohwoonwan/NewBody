package com.example.newbody.PushMessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.example.newbody.Membership3;
import com.example.newbody.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AndroidNewFCM extends AppCompatActivity {

    @BindView(R.id.btn_get_token)
    Button btn_get_token;

    private static final String TAG = "AndroidNewFCM";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_new_fcm);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();

        btn_get_token.setOnClickListener((view) -> {
            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(token -> {
                        Log.d(TAG, token);

                        // 토큰을 Firestore에 저장하는 로직
                        Map<String, Object> tokenData = new HashMap<>();
                        tokenData.put("token", token);

                        db.collection("pushMessage").add(tokenData)
                                .addOnSuccessListener(documentReference -> Log.d(TAG, "Token saved with ID: " + documentReference.getId()))
                                .addOnFailureListener(e -> Log.w(TAG, "Error adding token", e));

                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to get token", Toast.LENGTH_SHORT).show());
        });

    }

    public void saveUserDataAndNotification(String userUID, Map<String, Object> userData, String name) {
        db.collection("users").document(userUID)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                    return;
                                }
                                String token = task.getResult();
                                Map<String, Object> tokenData = new HashMap<>();
                                tokenData.put("token", token);

                                db.collection("pushMessage").add(tokenData)
                                        .addOnSuccessListener(documentReference -> Log.d(TAG, "Token saved with ID: " + documentReference.getId()))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error adding token", e));
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Failed to get FCM token", e);
                            });

                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("title", "환영합니다!");
                    notificationData.put("body", name + "님 회원가입을 축하합니다.");

                    db.collection("pushMessage").add(notificationData)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("Firestore", "Notification saved with ID: " + documentReference.getId());
                                PushNotificationService pushService = new PushNotificationService();
                                pushService.sendNotificationToUser(userUID, "환영합니다!", name + "님 회원가입을 축하합니다.");
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error adding notification", e));

                    Toast.makeText(AndroidNewFCM.this, "Data saved successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Membership3.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to set user data in Firestore", e);
                    Toast.makeText(AndroidNewFCM.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                });
    }
}

class PushNotificationService {
    public void sendNotificationToUser(String userFCMToken, String title, String body) {
        try {
            String fcmUrl = "https://fcm.googleapis.com/fcm/send";
            JSONObject root = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);
            root.put("notification", notification);
            root.put("to", userFCMToken);

            URL url = new URL(fcmUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            String serverKey = "BKKukGOqiZ3gPBnvKuH4WQ4g8uuKisF5vSYw4o6DRLjqlF3g6EGBZPCFKxgRYeMiUTgqlWgfjHMMx1-MRDzopIQ";
            connection.setRequestProperty("Authorization", "key=" + serverKey);
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = connection.getOutputStream();
            os.write(root.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("FCM", "Notification sent successfully");
            } else {
                Log.e("FCM", "Failed to send notification. Response code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e("FCM", "Exception while sending notification", e);
        }
    }
}
