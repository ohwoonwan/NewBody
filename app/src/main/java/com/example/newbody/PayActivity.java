package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class PayActivity extends AppCompatActivity {

    private String application_id = "64f1c55dd25985001bda77d7";

    private Button prev;
    private ImageView pay_button;

    private String productName = "프리미엄";  // 상품 이름
    private String productPrice = "4900";  // 상품 가격

    FirebaseUser user;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    private FirebaseFirestore db;

    Spinner spinner_pg;
    Spinner spinner_method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        initViews();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                intent.putExtra("SELECTED_FRAGMENT_INDEX", 3);
                startActivity(intent);
                finish();
            }
        });

        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRequest();
            }
        });
    }

    public void goRequest() {

        BootUser user = new BootUser().setPhone("010-9175-3460"); // 구매자 정보
        BootExtra extra = new BootExtra()
                .setCardQuota("0,2,3");  // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)

        Double price = 100d;

        String pg = "이니시스";
        String method = "카드";

        Payload payload = new Payload();
        payload.setApplicationId(application_id)
                .setOrderName("프리미엄 멤버십 결제")
                .setPg(pg)
                .setOrderId("1234")
                .setMethod(method)
                .setPrice(price)
                .setUser(user)
                .setExtra(extra);

        Map<String, Object> map = new HashMap<>();
        map.put("1", "abcdef");
        map.put("2", "abcdef55");
        map.put("3", 1234);
        payload.setMetadata(map);

        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {
                        Log.d("cancel", data);
                    }

                    @Override
                    public void onError(String data) {
                        Log.d("error", data);
                    }

                    @Override
                    public void onClose() {
                        Bootpay.removePaymentWindow();
                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("issued", data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("confirm", data);
//                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                        return true; //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                        return false; //결제를 진행하지 않을때 false
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("done", data);
                        savePremium();
                        saveOrUpdatePaymentData(productPrice);
                        Intent intent = new Intent(getApplicationContext(), Menu.class);
                        startActivity(intent);
                        finish();
                    }

                }).requestPayment();
    }

    private void savePremium(){
        Map<String, Object> userData = new HashMap<>();
        final String collectionName = "users";
        userData.put("grade", "프리미엄");

        DocumentReference userRecordRef = db.collection(collectionName).document(user.getUid());
        userRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
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

    private void saveOrUpdatePaymentData(String paymentAmount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        final String collectionName = "payments";

        DocumentReference paymentRecordRef = db.collection(collectionName).document(currentDate);
        paymentRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // 문서가 이미 존재하면, 금액을 업데이트
                        long existingAmount = document.getLong("amount");
                        long newAmount = existingAmount + Long.parseLong(paymentAmount);
                        paymentRecordRef.update("amount", newAmount);
                    } else {
                        // 문서가 존재하지 않으면, 새로운 문서 생성
                        Map<String, Object> paymentData = new HashMap<>();
                        paymentData.put("amount", Long.parseLong(paymentAmount));
                        paymentRecordRef.set(paymentData);
                    }
                } else {
                    Log.d("Firestore", "Failed to get document", task.getException());
                }
            }
        });
    }

    public void initViews(){
        prev = findViewById(R.id.prevButtonPay);
        pay_button = findViewById(R.id.payButton);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }
}