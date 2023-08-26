package com.example.newbody;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "{17eb8d27f560f7f29c4891c9eaeae167}");
    }
}
