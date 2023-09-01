package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class PayWebActivity extends AppCompatActivity {

    // Request 작업을 할 Queue
    static RequestQueue requestQueue;

    // 결제 정보를 받을 변수
    static String productName; // 상품 이름
    static String productPrice; // 상품 가격

    // 웹 뷰
    WebView webView;
    FirebaseUser user;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    private FirebaseFirestore db;


    // json 파싱
    Gson gson;

    // 커스텀 웹 뷰 클라이언트
    MyWebViewClient myWebViewClient;

    // 결제 고유 번호
    String tidPin;

    // 결제 요청 토큰
    String pgToken;

    // 기본 생성자
    // - Activity는 기본 생성자가 없으면 Manifest에서 사용하지 못함.
    // - 만약 생성자를 오버라이딩 했다면 기본 생성자를 작성해 둘것!
    public PayWebActivity() {

    }
    // 상품 이름과 가격을 초기화할 생성자
    public PayWebActivity(String productName, String productPrice) {
        PayWebActivity.productName = productName;
        PayWebActivity.productPrice = productPrice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_web);

        // 초기화
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        myWebViewClient = new MyWebViewClient();
        webView = findViewById(R.id.webView);
        gson = new Gson();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        // 웹 뷰 설정
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(myWebViewClient);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // 실행 시 바로 결제 Http 통신 실행
        requestQueue.add(myWebViewClient.readyRequest);
    }

    public class MyWebViewClient extends WebViewClient {

        // 에러 - 통신을 받을 Response 변수
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Debug", "Error : " + error);
            }
        };

        // 결제 준비 단계 - 통신을 받을 Response 변수
        Response.Listener<String> readyResponse = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Debug", response);
                // 결제가 성공 했다면 돌려받는 JSON객체를 파싱함.
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(response);

                // get("받을 Key")로 Json 데이터를 받음
                // - 결제 요청에 필요한 next_redirect_mobile_url, tid를 파싱
                String url = element.getAsJsonObject().get("next_redirect_mobile_url").getAsString();
                String tid = element.getAsJsonObject().get("tid").getAsString();
                Log.e("Debug", "url : " + url);
                Log.e("Debug", "tid : " + tid);

                webView.loadUrl(url);
                tidPin = tid;
            }
        };

        // 결제 준비 단계 - 통신을 넘겨줄 Request 변수
        StringRequest readyRequest = new StringRequest(Request.Method.POST, "https://kapi.kakao.com/v1/payment/ready", readyResponse, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("Debug", "name : " + productName);
                Log.e("Debug", "price : " + productPrice);

                Map<String, String> params = new HashMap<>();
                params.put("cid", "TC0ONETIME"); // 가맹점 코드
                params.put("partner_order_id", "1001"); // 가맹점 주문 번호
                params.put("partner_user_id", "gorany"); // 가맹점 회원 아이디
                params.put("item_name", productName); // 상품 이름
                params.put("quantity", "1"); // 상품 수량
                params.put("total_amount", productPrice); // 상품 총액
                params.put("tax_free_amount", "0"); // 상품 비과세
                params.put("approval_url", "https://www.naver.com/success"); // 결제 성공시 돌려 받을 url 주소
                params.put("cancel_url", "https://www.naver.com/cancel"); // 결제 취소시 돌려 받을 url 주소
                params.put("fail_url", "https://www.naver.com/fali"); // 결제 실패시 돌려 받을 url 주소
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "KakaoAK " + "8ba05249ce54d7303546e829203dcff6");
                return headers;
            }
        };

        // 결제 요청 단계 - 통신을 받을 Response 변수
        Response.Listener<String> approvalResponse = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Debug", response);
            }
        };

        // 결제 요청 단계 - 통신을 넘겨줄 Request 변수
        StringRequest approvalRequest = new StringRequest(Request.Method.POST, "https://kapi.kakao.com/v1/payment/approve", approvalResponse, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cid", "TC0ONETIME");
                params.put("tid", tidPin);
                params.put("partner_order_id", "1001");
                params.put("partner_user_id", "gorany");
                params.put("pg_token", pgToken);
                params.put("total_amount", productPrice);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "KakaoAK " + "8ba05249ce54d7303546e829203dcff6");
                return headers;
            }
        };

        // URL 변경시 발생 이벤트
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("Debug", "url" + url);
            if (url != null && url.contains("pg_token=")) {
                String pg_Token = url.substring(url.indexOf("pg_token=") + 9);
                pgToken = pg_Token;

                // 결제 요청을 보내기 전에 데이터 업데이트
                updateUserDataWithPayment(productPrice);

                requestQueue.add(approvalRequest);

                // 결제가 완료되면 WebView를 종료하고 PaymentActivity로 돌아감
                finish();  // 현재 Activity를 종료하여 PaymentActivity로 돌아감
                return true;  // URL을 처리했음을 나타내기 위해 true 반환
            } else if (url != null && url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (url != null && url.contains("cancel")) {
                // 결제 취소 로직
                Toast.makeText(PayWebActivity.this, "결제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            } else if (url != null && url.contains("fail")) {
                // 결제 실패 로직
                Toast.makeText(PayWebActivity.this, "결제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
            view.loadUrl(url);
            return false;
        }
    }

    // 결제 성공 시 사용자 데이터를 결제 금액만큼 업데이트하는 메소드
    private void updateUserDataWithPayment(String paymentAmount) {
        // 유저의 고유 식별자 가져오기 (유저 ID나 다른 키일 수 있음)
        String userIdentifier = user.getUid(); // 실제 유저의 고유 식별자로 변경
        Map<String, Object> userData = new HashMap<>();
        final String collectionName = "users";
        userData.put("grade", "프리미엄");


        DocumentReference userRecordRef = db.collection(collectionName).document(userIdentifier);
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

}