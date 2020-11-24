package com.Whowant.Tokki.UI.Activity.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class PanbookLoginActivity extends AppCompatActivity {
    private EditText inputIDView;
    private EditText inputPWView;

    // KakaoTalk Login
    private PanbookLoginActivity.SessionCallback kakaoCallback;

    // Google Login
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    private Context mContext;
    private String name, snsID, email;                  // 회원가입 및 로그인에 필요한 정보 -> 이후 액티비티들에 extra bundle 로 전달하여 회원가입 처리에 사용한다
    private Uri photoUrl;                               // 회원 사진 url
    private int snsNum;                                 // sns 로그인 시의 SNS 값   0 - 펜북 회원가입, 1 - Kakaotalk, 2 - Naver, 3 - Daum, 4 - FAcebook, 5 - Google
    private boolean bFinish = false;                    //  백버튼 두번 클릭에 사용하기 위한 flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_renewal);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        inputIDView = findViewById(R.id.inputIDView);
        inputPWView = findViewById(R.id.inputPWView);

        inputIDView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if(event.getAction() == KeyEvent.ACTION_DOWN)
                        return true;

                    inputPWView.requestFocus();
                    return true;
                }

                return false;
            }
        });

        inputPWView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event == null)
                    return false;

                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if(event.getAction() == KeyEvent.ACTION_DOWN)
                        return true;

                    requestLogin();
                    return true;
                }

                return false;
            }
        });

        mContext = this;

        // 로그인 이력이 있다면 자동 로그인 진행
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int nRegisterSNS = pref.getInt("REGISTER_SNS", 0);
        String strUserID = pref.getString("USER_ID", "Guest");
        String strUserPW = pref.getString("USER_PW", "");

//        strUserID = "";
//        strUserPW = "";
//        nRegisterSNS = ;

        if(nRegisterSNS == 0) {                 // SNS 로그인이 아닌 경우 자체 로그인(panbook login) 처리
            if(strUserID.length() > 0 && strUserPW.length() > 0) {
                requestPanbookLogin(strUserID, strUserPW);
            }
        } else {                                // SNS 로그인인 경우 SNS 로그인 처리
            String strSNSID = pref.getString("SNS_ID", "");
//            strSNSID = "";
            if(strSNSID.length() > 0) {             // 소셜 로그인 이라면
                requestSNSLogin(strUserID, strSNSID, false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickLoginBtn(View view) {
        requestLogin();
    }

    private void requestLogin() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputIDView.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(inputPWView.getWindowToken(), 0);

        String strID = inputIDView.getText().toString();
        String strPW = inputPWView.getText().toString();

        if(strID.length() == 0) {
            Toast.makeText(PanbookLoginActivity.this, "ID를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(strPW.length() == 0) {
            Toast.makeText(PanbookLoginActivity.this, "패스워드를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        CommonUtils.showProgressDialog(PanbookLoginActivity.this, "유저 정보를 확인하고 있습니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    JSONObject resultObject = HttpClient.requestPanbookLogin(new OkHttpClient(), strID, strPW, pref.getString("FCM_TOKEN", ""));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.hideProgressDialog();

                            if(resultObject == null) {
                                Toast.makeText(PanbookLoginActivity.this, "서버와의 연결이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
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
                                    int    nCoinCount = resultObject.getInt("COIN_COUNT");

                                    SharedPreferences.Editor editor = pref.edit();

                                    editor.putString("USER_ID", strID);
                                    editor.putString("USER_PW", strPW);
                                    editor.putString("USER_NAME", strUserName);
                                    editor.putString("USER_EMAIL", strUserEmail);
                                    editor.putString("USER_PHOTO", strUserPhoto);
                                    editor.putString("USER_PHONENUM", strUserPhoneNum);
                                    editor.putString("USER_DESC", strUserDesc);
                                    editor.putInt("REGISTER_SNS", nRegisterSNS);
                                    editor.putString("ADMIN", strUserAdmin);
                                    editor.putString("USER_BIRTHDAY", strBirthday);
                                    editor.putInt("USER_GENDER", nGender);
                                    editor.putInt("COIN_COUNT", nCoinCount);

                                    editor.commit();

                                    setResult(RESULT_OK);
                                    finish();
                                    Intent intent = new Intent(PanbookLoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(PanbookLoginActivity.this, "로그인에 실패했습니다. ID와 패스워드를 다시 확인해 주세요.", Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        if(!bFinish) {
            Toast.makeText(this, "한 번 더 뒤로가기 버튼을 누르시면 종료합니다.", Toast.LENGTH_SHORT).show();
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
        kakaoCallback = new PanbookLoginActivity.SessionCallback();
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
            if (requestCode == 1010) {                              // SNS 로그인 시도시 회원가입 되어있지 않을 때 회원가입 여부를 묻는 팝업에서 OK 클릭했을 경우 들어오는 부분
                Intent intent = new Intent(PanbookLoginActivity.this, AgreementActivity.class);
                intent.putExtra("SNS", snsNum);
                intent.putExtra("SNS_ID", snsID);
                intent.putExtra("USER_NAME", name);
                intent.putExtra("USER_EMAIL", email);
                intent.putExtra("USER_PHOTO", photoUrl);
                startActivity(intent);
//                startActivity(new Intent(PanbookLoginActivity.this, AgreementActivity.class));
            } else if (requestCode == 1000) {                       // Naver 로그인 결과를 받아오는 부분. RESULT_OK 라면 로그인 성공으로 MainActivity 로 이동
                Intent intent = new Intent(PanbookLoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (requestCode == RC_SIGN_IN) {                 // 그 외의 경우는 구글 로그인 성공 했을 경우로 판단하여 Firebase Login 으로 이행
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
        startActivity(new Intent(PanbookLoginActivity.this, FindAccountActivity.class));
    }

    public void onClickKakaoLoginBtn(View view) {
        initKakaotalkLogin();
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, PanbookLoginActivity.this);
    }

    public void onClickNaverLoginBtn(View view) {
        startActivityForResult(new Intent(PanbookLoginActivity.this, NaverSignupActivity.class), 1000);
    }

    public void onClickDaumLoginBtn(View view) {                    // XML 에서 GONE 처리된 부분으로 현재 사용하지 않음

    }

    public void onClickFacebookLoginBtn(View view) {
        startActivityForResult(new Intent(PanbookLoginActivity.this, FacebookSignupActivity.class), 1000);
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
                            PanbookLoginActivity.this.name = name;
                            PanbookLoginActivity.this.email = email;
                            PanbookLoginActivity.this.photoUrl = photoUrl;
                            PanbookLoginActivity.this.snsID = user.getUid();

                            requestSNSLogin(email, user.getUid(), true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Google Login", "signInWithCredential:failure", task.getException());
                            Toast.makeText(PanbookLoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
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

            Intent intent = new Intent(PanbookLoginActivity.this, KakaoSignupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(intent, 1000);
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
                Toast.makeText(PanbookLoginActivity.this, "카카오톡 로그인이 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PanbookLoginActivity.this, "로그인에 실패했습니다. SNS 로그인을 다시 진행해 주세요.", Toast.LENGTH_LONG).show();

                                    int nRegisterSNS = pref.getInt("REGISTER_SNS", 0);

                                    if(nRegisterSNS == 1) {
                                        KakaoSDKAdapter.unregisterKakaoTalk(PanbookLoginActivity.this);
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
                                    Intent intent = new Intent(PanbookLoginActivity.this, MainActivity.class);
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
        Intent intent = new Intent(PanbookLoginActivity.this, PhoneNumAuthActivity.class);
        startActivity(intent);
//        Intent intent = new Intent(PanbookLoginActivity.this, PersonalInfoActivity.class);
//        startActivity(intent);
    }

    public void onClickTokkiLoginBtn(View view) {
        startActivity(new Intent(PanbookLoginActivity.this, PanbookLoginActivity.class));
    }

    public void onClickFindPWBtn(View view) {
        startActivity(new Intent(PanbookLoginActivity.this, FindPasswordActivity.class));
    }

    private void requestPanbookLogin(String strUserID, String strUserPW) {                              // SNS 로그인이 아닌 일반 회원가입 시의 로그인 처리
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
                                Toast.makeText(mContext, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
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

                                        Intent intent = new Intent(PanbookLoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(PanbookLoginActivity.this, "소셜 로그인에 실패하였습니다. 잠시 후 다시 이용해 주세요.", Toast.LENGTH_LONG).show();
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

    private void requestSNSLogin(String strUserID, String SNSID, boolean bRegi) {                   // bRegi : 회원가입 단계에서 SNS 로그인 시도인지 확인하는 flag
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
                                Toast.makeText(PanbookLoginActivity.this, "서버와의 연결이 원활하지 않습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
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

                                    Intent intent = new Intent(PanbookLoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    if(bRegi) {
                                        Intent intent = new Intent(PanbookLoginActivity.this, CommonPopup.class);
                                        intent.putExtra("TITLE", "회원 가입 안내");
                                        intent.putExtra("CONTENTS", "회원가입 되지 않은 소셜 계정입니다.\n간편 회원가입 하시겠습니까?");
                                        intent.putExtra("TWOBTN", true);
                                        startActivityForResult(intent, 1010);
//                                        Toast.makeText(PanbookLoginActivity.this, "소셜 로그인에 실패했습니다. 회원 가입 화면으로 이동됩니다.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(PanbookLoginActivity.this, "소셜 로그인에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
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
