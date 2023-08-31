package com.example.newbody;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PayActivity extends AppCompatActivity {

    private Button prev;
    private ImageView pay_button;

    private String productName = "프리미엄";  // 상품 이름
    private String productPrice = "4900";  // 상품 가격

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
                PayWebActivity pay = new PayWebActivity(productName, productPrice);
                Intent intent = new Intent(getApplicationContext(), pay.getClass());
                startActivity(intent);
            }
        });
    }

    public void initViews(){
        prev = findViewById(R.id.prevButtonPay);
        pay_button = findViewById(R.id.payButton);
    }
}