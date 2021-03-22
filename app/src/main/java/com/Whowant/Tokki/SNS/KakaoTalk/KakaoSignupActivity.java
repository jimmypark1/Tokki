package com.Whowant.Tokki.SNS.KakaoTalk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.AgreementActivity;
import com.Whowant.Tokki.UI.Activity.Login.LoginSelectActivity;
import com.Whowant.Tokki.UI.Activity.Login.PanbookLoginActivity;
import com.Whowant.Tokki.UI.Activity.Login.PersonalInfoActivity;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KakaoSignupActivity extends AppCompatActivity {
    private String strEmail;
    private String strNickName;
    private String strSNSID;
    private String strPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_signup);
        requestMe();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("asdf", "asdf");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(KakaoSignupActivity.this, AgreementActivity.class);
                /*
                strEmail = null;
                if(response.getKakaoAccount().hasEmail().getBoolean() == true)
                    strEmail = response.getKakaoAccount().getEmail();

                Map<String, String> properties = response.getProperties();

                strSNSID = "" + response.getId();
                strNickName = properties.get("nickname");
                strPhoto = properties.get("profile_image");
                 */
                intent.putExtra("USER_EMAIL", strEmail);
                intent.putExtra("SNS_ID", strSNSID);
                intent.putExtra("USER_NAME", strNickName);
                intent.putExtra("USER_PHOTO", strPhoto);
                intent.putExtra("SNS", 1);
                startActivity(intent);
            } else {
                KakaoSDKAdapter.unregisterKakaoTalk(KakaoSignupActivity.this);
                finish();
            }
        }
    }

    private void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(MeV2Response response) {              // 로그인 후 정보 가져오기 완료
                strEmail = null;
                if(response.getKakaoAccount().hasEmail().getBoolean() == true)
                    strEmail = response.getKakaoAccount().getEmail();

                Map<String, String> properties = response.getProperties();

                strSNSID = "" + response.getId();
                strNickName = properties.get("nickname");
                strPhoto = properties.get("profile_image");

                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                HttpClient.checkSocialLogin(KakaoSignupActivity.this, strSNSID, pref.getString("FCM_TOKEN", ""));
            }
        });
    }


    private void redirectInfoActivity(String strNickname, String strEmail, String strProfileImgUrl, String strSNSID) {
        Intent intent = new Intent(KakaoSignupActivity.this, PersonalInfoActivity.class);
        intent.putExtra("SNS", 1);
        intent.putExtra("SNS_ID", strSNSID);
        intent.putExtra("USER_NAME", strNickname);
        intent.putExtra("USER_EMAIL", strEmail);
        intent.putExtra("USER_PHOTO", strProfileImgUrl);
        startActivity(intent);
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }

    protected void redirectLoginActivity() {
        Intent intent = new Intent(this, PanbookLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
