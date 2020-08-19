package com.Whowant.Tokki.SNS.Naver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.AgreementActivity;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class NaverSignupActivity extends AppCompatActivity {
    // NaverLogin
    private OAuthLogin mOAuthLoginModule;
    private Activity mContext;
    private String strEmail;
    private String strSNSID;
    private String strNickName;
    private String strPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_signup);

        mContext = this;

        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                NaverSignupActivity.this
                ,"4dlg_Fv_nDuSfls6rwVP"
                ,"eGR04oow6M"
                ,"Tokki"
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );

        mOAuthLoginModule.startOauthLoginActivity(NaverSignupActivity.this, mOAuthLoginHandler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(NaverSignupActivity.this, AgreementActivity.class);
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
                intent.putExtra("SNS", 2);
                startActivity(intent);
            } else {
                finish();
            }
        }
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                String tokenType = mOAuthLoginModule.getTokenType(mContext);
                Log.d("TOKEN", "DEBUG token type : " + OAuthLogin.getInstance().getTokenType(mContext));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final JSONObject resultObject = HttpClient.requestNaverME(new OkHttpClient(), accessToken, tokenType);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(resultObject.getString("message").equals("success")) {
                                        JSONObject object = resultObject.getJSONObject("response");

                                        strSNSID = object.getString("id");
                                        strEmail = object.getString("email");
                                        strNickName = object.getString("name");
                                        strPhoto = object.getString("profile_image");

                                        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                                        HttpClient.checkSocialLogin(NaverSignupActivity.this, strSNSID, pref.getString("FCM_TOKEN", ""));
                                    } else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }).start();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();
                        boolean bLogout = mOAuthLoginModule.logoutAndDeleteToken(NaverSignupActivity.this);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                }).start();
            }
        };
    };
}