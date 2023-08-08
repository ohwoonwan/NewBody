package com.example.newbody;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.newbody.mainInfo.MainActivityA;

import org.checkerframework.checker.nullness.qual.NonNull;

public class CustomDialog extends Dialog {
    private TextView txt_contents;
    private Button shutdownClick;
    private Context mContext;

    public CustomDialog(@NonNull Context context, String contents) {
        super(context);
        mContext = context;
        setContentView(R.layout.activity_custom_dialog);

        txt_contents = findViewById(R.id.txt_contents);
        txt_contents.setText(contents);
        shutdownClick = findViewById(R.id.btn_shutdown);
        shutdownClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Record.class);
                mContext.startActivity(intent);
                dismiss(); // Dialog를 닫습니다.

                // 해당 Dialog를 호출한 Activity를 종료합니다.
                if (mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }
            }
        });

    }
}
