package com.Whowant.Tokki.UI.Activity.Intro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Login.LoginSelectActivity;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

public class IntroActivity extends AppCompatActivity {
    private String strFCMToken;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM_LOG", "getInstanceId failed", task.getException());

                            goNextStep();
                            return;
                        }

                        // Get new Instance ID token
                        strFCMToken = task.getResult().getToken();

                        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                        String strToken = pref.getString("FCM_TOKEN", "");

                        if(!strToken.equals(strFCMToken)) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("FCM_TOKEN", strFCMToken);
                            editor.commit();
                        }

                        goNextStep();
                    }
                });
    }

    private void goNextStep() {
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int nRegisterSNS = pref.getInt("REGISTER_SNS", 0);
        String strUserID = pref.getString("USER_ID", "Guest");
        String strUserPW = pref.getString("USER_PW", "");

        if(nRegisterSNS == 0) {
            if(strUserID.length() > 0 && strUserPW.length() > 0 && !strUserID.equals("Guest")) {
                requestPanbookLogin(strUserID, strUserPW);
            } else {
                startActivity(new Intent(IntroActivity.this, LoginSelectActivity.class));
                finish();
            }
        } else {
            String strSNSID = pref.getString("SNS_ID", "");

            if(strSNSID.length() > 0 && !strUserID.equals("Guest")) {             // 소셜 로그인 이라면
                requestSNSLogin(strUserID, strSNSID);
            } else {
                startActivity(new Intent(IntroActivity.this, LoginSelectActivity.class));
                finish();
            }
        }
    }

    private void goToMain() {
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("USER_ID", "Guest");
        editor.putString("USER_NAME", "Guest");
        editor.commit();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, 500);
    }

    private void requestPanbookLogin(String strUserID, String strUserPW) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject resultObject = HttpClient.requestPanbookLogin(new OkHttpClient(), strUserID, strUserPW, strFCMToken);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(resultObject == null) {
                                Toast.makeText(IntroActivity.this, "서버와의 연결이 원활하지 않습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                                finish();
                                return;
                            }

                            try {
                                final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

                                if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    String strUserName = resultObject.getString("USER_NAME");
                                    String strUserEmail = resultObject.getString("USER_EMAIL");
                                    String strUserPhoto = resultObject.getString("USER_PHOTO");
                                    String strUserPhoneNum = resultObject.getString("USER_PHONENUM");
                                    int nRegisterSNS = resultObject.getInt("REGISTER_SNS");
                                    String strUserAdmin = resultObject.getString("USER_ADMIN");
                                    String strUserDesc = resultObject.getString("USER_DESC");
                                    int    nCoinCount = resultObject.getInt("COIN_COUNT");

                                    SharedPreferences.Editor editor = pref.edit();
                                    String strBirthday = resultObject.getString("USER_BIRTHDAY");
                                    int nGender = resultObject.getInt("USER_GENDER");

                                    editor.putString("USER_ID", strUserID);
                                    editor.putString("USER_PW", strUserPW);
                                    editor.putString("USER_NAME", strUserName);
                                    editor.putString("USER_EMAIL", strUserEmail);
                                    editor.putString("USER_PHOTO", strUserPhoto);
                                    editor.putString("USER_PHONENUM", strUserPhoneNum);
                                    editor.putInt("REGISTER_SNS", nRegisterSNS);
                                    editor.putString("ADMIN", strUserAdmin);
                                    editor.putString("USER_DESC", strUserDesc);
                                    editor.putString("USER_BIRTHDAY", strBirthday);
                                    editor.putInt("USER_GENDER", nGender);
                                    editor.putInt("COIN_COUNT", nCoinCount);
                                    editor.commit();

                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }, 500);
                                } else {
                                    Toast.makeText(IntroActivity.this, "소셜 로그인에 실패하였습니다. 잠시 후 다시 이용해 주세요.", Toast.LENGTH_LONG).show();
                                    CommonUtils.resetUserInfo(pref);

                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            goToMain();
                                        }
                                    }, 500);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                                final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                                CommonUtils.resetUserInfo(pref);

                                new Handler().postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        goToMain();
                                    }
                                }, 500);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();

                    final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    CommonUtils.resetUserInfo(pref);

                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            goToMain();
                        }
                    }, 500);
                }
            }
        }).start();
    }

    private void requestSNSLogin(String strUserID, String SNSID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject resultObject = HttpClient.requestSocialLogin(new OkHttpClient(), SNSID, strFCMToken);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(resultObject == null) {
                                Toast.makeText(IntroActivity.this, "서버와의 연결이 원활하지 않습니다. 수동으로 로그인해 주세요.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(IntroActivity.this, LoginSelectActivity.class));
                                finish();
                                return;
                            }

                            try {
                                final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

                                if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    String strUserName = resultObject.getString("USER_NAME");
                                    String strUserEmail = resultObject.getString("USER_EMAIL");
                                    String strUserPhoto = resultObject.getString("USER_PHOTO");
                                    String strUserPhoneNum = resultObject.getString("USER_PHONENUM");
                                    int nRegisterSNS = resultObject.getInt("REGISTER_SNS");
                                    String strUserAdmin = resultObject.getString("USER_ADMIN");
                                    String strUserDesc = resultObject.getString("USER_DESC");
                                    String strBirthday = resultObject.getString("USER_BIRTHDAY");
                                    int nGender = resultObject.getInt("USER_GENDER");
                                    int    nCoinCount = resultObject.getInt("COIN_COUNT");

                                    SharedPreferences.Editor editor = pref.edit();

                                    editor.putString("USER_ID", strUserID);
                                    editor.putString("USER_NAME", strUserName);
                                    editor.putString("USER_EMAIL", strUserEmail);
                                    editor.putString("USER_PHOTO", strUserPhoto);
                                    editor.putString("USER_PHONENUM", strUserPhoneNum);
                                    editor.putInt("REGISTER_SNS", nRegisterSNS);
                                    editor.putString("SNS_ID", SNSID);
                                    editor.putString("ADMIN", strUserAdmin);
                                    editor.putString("USER_DESC", strUserDesc);
                                    editor.putString("USER_BIRTHDAY", strBirthday);
                                    editor.putInt("USER_GENDER", nGender);
                                    editor.putInt("COIN_COUNT", nCoinCount);
                                    editor.commit();

                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }, 500);
                                } else {
                                    Toast.makeText(IntroActivity.this, "로그인에 실패했습니다. 수동으로 로그인해 주세요.", Toast.LENGTH_LONG).show();
                                    CommonUtils.resetUserInfo(pref);

                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            startActivity(new Intent(IntroActivity.this, LoginSelectActivity.class));
                                            finish();
                                        }
                                    }, 500);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                                final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                                CommonUtils.resetUserInfo(pref);

                                new Handler().postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        startActivity(new Intent(IntroActivity.this, LoginSelectActivity.class));
                                        finish();
                                    }
                                }, 500);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();

                    final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    CommonUtils.resetUserInfo(pref);

                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            startActivity(new Intent(IntroActivity.this, LoginSelectActivity.class));
                            finish();
                        }
                    }, 500);
                }
            }
        }).start();
    }
}
