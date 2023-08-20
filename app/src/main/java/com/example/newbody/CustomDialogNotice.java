package com.example.newbody;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class CustomDialogNotice extends Dialog {
    private TextView txt_contents;
    private Button shutdownClick;
    private Context mContext;

    public CustomDialogNotice(@NonNull Context context, String contents) {
        super(context);
        mContext = context;
        setContentView(R.layout.activity_custom_dialog_notice);

        txt_contents = findViewById(R.id.txt_contents);
        txt_contents.setText(contents);
        shutdownClick = findViewById(R.id.btn_shutdown);
        shutdownClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, Home.class);
//                mContext.startActivity(intent);
                dismiss(); // Dialog를 닫습니다.
            }
        });

    }
}
