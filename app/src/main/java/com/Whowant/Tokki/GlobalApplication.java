package com.Whowant.Tokki;

import android.app.Application;

import com.Whowant.Tokki.SNS.KakaoTalk.KakaoSDKAdapter;
import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends Application {                                        // Application 생명주기를 이용해 앱이 살아있을때 카카오 객체를 초기화
    private static volatile GlobalApplication instance = null;

    public static GlobalApplication getGlobalApplicationContext() {
        if (instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }
}
