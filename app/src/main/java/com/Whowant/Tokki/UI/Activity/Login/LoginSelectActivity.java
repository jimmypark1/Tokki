package com.Whowant.Tokki.UI.Activity.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.SNS.Facebook.FacebookSignupActivity;
import com.Whowant.Tokki.SNS.KakaoTalk.KakaoSDKAdapter;
import com.Whowant.Tokki.SNS.KakaoTalk.KakaoSignupActivity;
import com.Whowant.Tokki.SNS.Naver.NaverSignupActivity;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.UI.Popup.CommonPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class LoginSelectActivity extends AppCompatActivity {
    // KakaoTalk Login
    private SessionCallback kakaoCallback;

    // Google Login
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    private Context mContext;
    private String name, snsID, email;                  // ???????????? ??? ???????????? ????????? ?????? -> ?????? ?????????????????? extra bundle ??? ???????????? ???????????? ????????? ????????????
    private Uri photoUrl;                               // ?????? ?????? url
    private int snsNum;                                 // sns ????????? ?????? SNS ???   0 - ?????? ????????????, 1 - Kakaotalk, 2 - Naver, 3 - Daum, 4 - FAcebook, 5 - Google

//    private EditText inputIDView;
//    private EditText inputPWView;
    private boolean bFinish = false;                    //  ????????? ?????? ????????? ???????????? ?????? flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_select_renewal);

        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();                  // ??? ????????? ????????? ???????????? ????????? ?????? ??????

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        mContext = this;

        // ????????? ????????? ????????? ?????? ????????? ??????
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int nRegisterSNS = pref.getInt("REGISTER_SNS", 0);
        String strUserID = pref.getString("USER_ID", "Guest");
        String strUserPW = pref.getString("USER_PW", "");

//        strUserID = "";
//        strUserPW = "";
//        nRegisterSNS = ;

        if(nRegisterSNS == 0) {                 // SNS ???????????? ?????? ?????? ?????? ?????????(panbook login) ??????
            if(strUserID.length() > 0 && strUserPW.length() > 0) {
                requestPanbookLogin(strUserID, strUserPW);
            }
        } else {                                // SNS ???????????? ?????? SNS ????????? ??????
            String strSNSID = pref.getString("SNS_ID", "");
//            strSNSID = "";
            if(strSNSID.length() > 0) {             // ?????? ????????? ?????????
                requestSNSLogin(strUserID, strSNSID, false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!bFinish) {
            Toast.makeText(this, "??? ??? ??? ?????? ?????? ????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
            bFinish = true;

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    bFinish = false;
                }
            }, 2000);

            return;
        }

        super.onBackPressed();
    }

    private void initKakaotalkLogin() {                         // kakao Login
        kakaoCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(kakaoCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(kakaoCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1010) {                              // SNS ????????? ????????? ???????????? ???????????? ?????? ??? ???????????? ????????? ?????? ???????????? OK ???????????? ?????? ???????????? ??????
                Intent intent = new Intent(LoginSelectActivity.this, AgreementActivity.class);
                intent.putExtra("SNS", snsNum);
                intent.putExtra("SNS_ID", snsID);
                intent.putExtra("USER_NAME", name);
                intent.putExtra("USER_EMAIL", email);
                intent.putExtra("USER_PHOTO", photoUrl);
                startActivity(intent);
//                startActivity(new Intent(LoginSelectActivity.this, AgreementActivity.class));
            } else if (requestCode == 1000) {                       // Naver ????????? ????????? ???????????? ??????. RESULT_OK ?????? ????????? ???????????? MainActivity ??? ??????
                Intent intent = new Intent(LoginSelectActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (requestCode == RC_SIGN_IN) {                 // ??? ?????? ????????? ?????? ????????? ?????? ?????? ????????? ???????????? Firebase Login ?????? ??????
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    e.printStackTrace();
                    Log.w("Google Login", "Google sign in failed", e);
                }
            }
        }
    }

    public void onClickFindAccountBtn(View view) {
        startActivity(new Intent(LoginSelectActivity.this, FindAccountActivity.class));
    }

    public void onClickKakaoLoginBtn(View view) {
        initKakaotalkLogin();
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginSelectActivity.this);
    }

    public void onClickNaverLoginBtn(View view) {
        startActivityForResult(new Intent(LoginSelectActivity.this, NaverSignupActivity.class), 1000);
    }

    public void onClickDaumLoginBtn(View view) {                    // XML ?????? GONE ????????? ???????????? ?????? ???????????? ??????

    }

    public void onClickFacebookLoginBtn(View view) {
        startActivityForResult(new Intent(LoginSelectActivity.this, FacebookSignupActivity.class), 1000);
    }

    public void onClickGoogleLoginBtn(View view) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Google Login Callback
     */

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Google Login", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Google Login", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = mAuth.getUid();
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            Uri photoUrl = user.getPhotoUrl();

                            snsNum = 1;
                            LoginSelectActivity.this.name = name;
                            LoginSelectActivity.this.email = email;
                            LoginSelectActivity.this.photoUrl = photoUrl;
                            LoginSelectActivity.this.snsID = user.getUid();

                            requestSNSLogin(email, user.getUid(), true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Google Login", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginSelectActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * KakaoTalk Login Callback
     */

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
            String strUserID = pref.getString("USER_ID", "Guest");
            String strSNSID = pref.getString("SNS_ID", "");

            if(strUserID.length() > 0 && strSNSID.length() > 0 && !strUserID.equals("Guest")) {
                requestPanbookLogin(strSNSID);
                return;
            }

            Intent intent = new Intent(LoginSelectActivity.this, KakaoSignupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(intent, 1000);
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
                Toast.makeText(LoginSelectActivity.this, "???????????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Naver Login Callback
     *
     */




    private void requestPanbookLogin(String strSNSID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    JSONObject resultObject = HttpClient.requestSocialLogin(new OkHttpClient(), strSNSID, pref.getString("FCM_TOKEN", ""));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            try {
                                if(resultObject == null || !resultObject.getString("RESULT").equals("SUCCESS")) {
                                    Toast.makeText(LoginSelectActivity.this, "???????????? ??????????????????. SNS ???????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();

                                    int nRegisterSNS = pref.getInt("REGISTER_SNS", 0);

                                    if(nRegisterSNS == 1) {
                                        KakaoSDKAdapter.unregisterKakaoTalk(LoginSelectActivity.this);
                                    }
                                } else if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                    String strID = resultObject.getString("USER_ID");
                                    String strUserName = resultObject.getString("USER_NAME");
                                    String strUserEmail = resultObject.getString("USER_EMAIL");
                                    String strUserPhoto = resultObject.getString("USER_PHOTO");
                                    String strUserPhoneNum = resultObject.getString("USER_PHONENUM");
                                    int nRegisterSNS = resultObject.getInt("REGISTER_SNS");
                                    String strUserAdmin = resultObject.getString("USER_ADMIN");
                                    String strUserDesc = resultObject.getString("USER_DESC");
                                    String strBirthday = resultObject.getString("USER_BIRTHDAY");
                                    int nGender = resultObject.getInt("USER_GENDER");
                                    int nCoinCount = resultObject.getInt("COIN_COUNT");

                                    SharedPreferences.Editor editor = pref.edit();

                                    editor.putString("USER_ID", strID);
                                    editor.putString("USER_NAME", strUserName);
                                    editor.putString("USER_EMAIL", strUserEmail);
                                    editor.putString("USER_PHOTO", strUserPhoto);
                                    editor.putString("USER_PHONENUM", strUserPhoneNum);
                                    editor.putInt("REGISTER_SNS", nRegisterSNS);
                                    editor.putString("SNS_ID", strSNSID);
                                    editor.putString("ADMIN", strUserAdmin);
                                    editor.putString("USER_DESC", strUserDesc);
                                    editor.putString("USER_BIRTHDAY", strBirthday);
                                    editor.putInt("USER_GENDER", nGender);
                                    editor.putInt("COIN_COUNT", nCoinCount);
                                    editor.commit();

//                                    setResult(RESULT_OK);
//                                    finish();
                                    Intent intent = new Intent(LoginSelectActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onClickRegisterBtn(View view) {
        Intent intent = new Intent(LoginSelectActivity.this, AgreementActivity.class);
        startActivity(intent);
//        Intent intent = new Intent(PanbookLoginActivity.this, PersonalInfoActivity.class);
//        startActivity(intent);
    }

    public void onClickTokkiLoginBtn(View view) {
        startActivity(new Intent(LoginSelectActivity.this, PanbookLoginActivity.class));
    }

    public void onClickFindPWBtn(View view) {
        startActivity(new Intent(LoginSelectActivity.this, FindPasswordActivity.class));
    }

    private void requestPanbookLogin(String strUserID, String strUserPW) {                              // SNS ???????????? ?????? ?????? ???????????? ?????? ????????? ??????
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    JSONObject resultObject = HttpClient.requestPanbookLogin(new OkHttpClient(), strUserID, strUserPW, pref.getString("FCM_TOKEN", ""));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(resultObject == null) {
                                Toast.makeText(mContext, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    if(resultObject.getString("RESULT").equals("SUCCESS")) {
                                        String strUserName = resultObject.getString("USER_NAME");
                                        String strUserEmail = resultObject.getString("USER_EMAIL");
                                        String strUserPhoto = resultObject.getString("USER_PHOTO");
                                        String strUserPhoneNum = resultObject.getString("USER_PHONENUM");
                                        int nRegisterSNS = resultObject.getInt("REGISTER_SNS");
                                        String strUserAdmin = resultObject.getString("USER_ADMIN");
                                        String strUserDesc = resultObject.getString("USER_DESC");
                                        String strBirthday = resultObject.getString("USER_BIRTHDAY");
                                        int    nGender = resultObject.getInt("USER_GENDER");
                                        int    nCoinCount = resultObject.getInt("COIN_COUNT");
                                        SharedPreferences.Editor editor = pref.edit();

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

                                        Intent intent = new Intent(LoginSelectActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(LoginSelectActivity.this, "?????? ???????????? ?????????????????????. ?????? ??? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void requestSNSLogin(String strUserID, String SNSID, boolean bRegi) {                   // bRegi : ???????????? ???????????? SNS ????????? ???????????? ???????????? flag
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    JSONObject resultObject = HttpClient.requestSocialLogin(new OkHttpClient(), SNSID, pref.getString("FCM_TOKEN", ""));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(resultObject == null) {
                                Toast.makeText(LoginSelectActivity.this, "???????????? ????????? ???????????? ????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            try {
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
                                    editor.commit();

                                    Intent intent = new Intent(LoginSelectActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    if(bRegi) {
                                        Intent intent = new Intent(LoginSelectActivity.this, CommonPopup.class);
                                        intent.putExtra("TITLE", "?????? ?????? ??????");
                                        intent.putExtra("CONTENTS", "???????????? ?????? ?????? ?????? ???????????????.\n?????? ???????????? ???????????????????");
                                        intent.putExtra("TWOBTN", true);
                                        startActivityForResult(intent, 1010);
//                                        Toast.makeText(LoginSelectActivity.this, "?????? ???????????? ??????????????????. ?????? ?????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(LoginSelectActivity.this, "?????? ???????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}