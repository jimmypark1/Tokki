package com.Whowant.Tokki.UI.Activity.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.SNS.Facebook.FacebookSignupActivity;
import com.Whowant.Tokki.SNS.KakaoTalk.KakaoSDKAdapter;
import com.Whowant.Tokki.SNS.KakaoTalk.KakaoSignupActivity;
import com.Whowant.Tokki.SNS.Naver.NaverSignupActivity;
import com.Whowant.Tokki.UI.Activity.Admin.CommentManagementActivity;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.UI.Popup.CommonPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.Whowant.Tokki.Utils.SimplePreference;
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

import static com.Whowant.Tokki.Utils.CommonUtils.getNetworkState;

public class PanbookLoginActivity extends AppCompatActivity  {
    private EditText inputIDView;
    private EditText inputPWView;

    ImageView sns0;
    ImageView sns1;
    ImageView sns2;
    ImageView sns3;

    // KakaoTalk Login
    private PanbookLoginActivity.SessionCallback kakaoCallback;

    // Google Login
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    private Context mContext;
    private String name, snsID, email;                  // ???????????? ??? ???????????? ????????? ?????? -> ?????? ?????????????????? extra bundle ??? ???????????? ???????????? ????????? ????????????
    private Uri photoUrl;                               // ?????? ?????? url
    private int snsNum;                                 // sns ????????? ?????? SNS ???   0 - ?????? ????????????, 1 - Kakaotalk, 2 - Naver, 3 - Daum, 4 - FAcebook, 5 - Google
    private boolean bFinish = false;                    //  ????????? ?????? ????????? ???????????? ?????? flag


    Button loginBtn;
    Button registerBtn;

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

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        sns0  = findViewById(R.id.sns0);
        sns1  = findViewById(R.id.sns1);
        sns2  = findViewById(R.id.sns2);
        sns3  = findViewById(R.id.sns3);

        sns0.setVisibility(View.INVISIBLE);
        sns1.setVisibility(View.INVISIBLE);
        sns2.setVisibility(View.INVISIBLE);
        sns3.setVisibility(View.INVISIBLE);

        initUI();

        int nSns = SimplePreference.getIntegerPreference(PanbookLoginActivity.this, "USER_INFO", "SNS_TYPE", 0);
        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        int nRegisterSNS = pref.getInt("REGISTER_SNS", 0);

        if(nSns > 0)
        {
            sns0.setVisibility(View.INVISIBLE);
            sns1.setVisibility(View.INVISIBLE);
            sns2.setVisibility(View.INVISIBLE);
            sns3.setVisibility(View.INVISIBLE);
            if(nSns == 1)
            {
                sns0.setVisibility(View.VISIBLE);

            }
            else if(nSns == 2)
            {
                sns1.setVisibility(View.VISIBLE);

            }
            else if(nSns == 4)
            {
                sns2.setVisibility(View.VISIBLE);

            }
            else if(nSns == 5)
            {
                sns3.setVisibility(View.VISIBLE);

            }

        }
        else
        {

        }


        inputIDView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(inputIDView.getText().toString().length() > 0) {

                    loginBtn.setBackgroundResource(R.drawable.login_btn_en_bg);
                    loginBtn.setTextColor(Color.parseColor("#ffffff"));
                    registerBtn.setBackgroundResource(R.drawable.login_register_bt);
                    registerBtn.setTextColor(Color.parseColor("#6ca5ff"));

                    inputIDView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg_en);
                    //
                }
                else
                {
                    initUI();
                    inputIDView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputPWView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(inputPWView.getText().toString().length() > 0) {
                    inputPWView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg_en);

                }
                else
                {
                    inputPWView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputIDView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event == null)
                    return false;

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
/*
        inputIDView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {
                if (hasfocus) {

                    if(inputPWView.getText().toString().length() > 0)
                    {
                        inputPWView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg_en);

                    }
                    else
                    {
                        inputPWView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg);

                    }
                    loginBtn.setBackgroundResource(R.drawable.login_btn_en_bg);
                    loginBtn.setTextColor(Color.parseColor("#ffffff"));
                    registerBtn.setBackgroundResource(R.drawable.login_register_bt);
                    registerBtn.setTextColor(Color.parseColor("#6ca5ff"));

                    inputIDView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg_en);

                } else {

                }
            }
        });
        inputPWView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasfocus) {

                if(hasfocus) {
                    if(inputIDView.getText().toString().length() > 0)
                    {
                        inputIDView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg_en);

                    }
                    else
                    {
                        inputIDView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg);

                    }
                    inputPWView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg_en);

                }
                else
                {
                    inputPWView.setBackgroundResource(R.drawable.round_square_gray_stroke_bg);

                }
            }
        });

 */
        mContext = this;

        // ????????? ????????? ????????? ?????? ????????? ??????
       // SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
       // int nRegisterSNS = pref.getInt("REGISTER_SNS", 0);
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

    void initUI()
    {

        registerBtn.setBackgroundResource(R.drawable.login_btn_en_bg);
        registerBtn.setTextColor(Color.parseColor("#ffffff"));
        loginBtn.setBackgroundResource(R.drawable.login_btn_dis);
        loginBtn.setTextColor(Color.parseColor("#d4d4d8"));

        //login_register_bt

    }
    public void clickReserPassword(View view)
    {
        Intent intent = new Intent(PanbookLoginActivity.this,PhoneNumAuthActivity.class);
        intent.putExtra("AUTH_TYPE",1);
        startActivity(intent);
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
            Toast.makeText(PanbookLoginActivity.this, "????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        if(strPW.length() == 0) {
            Toast.makeText(PanbookLoginActivity.this, "??????????????? ??????????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getNetworkState(mContext)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PanbookLoginActivity.this);
            builder.setMessage("??????????????? ????????? ??? ????????????. ???????????? ?????? ????????? ????????? ?????????.");
            builder.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    requestLogin();
                }
            });

            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }

        CommonUtils.showProgressDialog(PanbookLoginActivity.this, "?????? ????????? ???????????? ????????????. ????????? ????????? ?????????.");

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
                                Toast.makeText(PanbookLoginActivity.this, "???????????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
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
                                    String strRecommendCode = resultObject.getString("RECOMMEND_CODE");
                                    int nGender = resultObject.getInt("USER_GENDER");
                                    int    nCoinCount = resultObject.getInt("COIN_COUNT");

                                    SharedPreferences.Editor editor = pref.edit();

                                    editor.putInt("SNS_TYPE", 0);

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
                                    editor.putString("RECOMMEND_CODE", strRecommendCode);

                                    editor.commit();

                                    setResult(RESULT_OK);
                                    finish();
                                    Intent intent = new Intent(PanbookLoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(PanbookLoginActivity.this, "???????????? ??????????????????. ID??? ??????????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
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
            if (requestCode == 1010) {                              // SNS ????????? ????????? ???????????? ???????????? ?????? ??? ???????????? ????????? ?????? ???????????? OK ???????????? ?????? ???????????? ??????
                Intent intent = new Intent(PanbookLoginActivity.this, AgreementActivity.class);
             //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
              //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                intent.putExtra("SNS", snsNum);
                intent.putExtra("SNS_ID", snsID);
                intent.putExtra("USER_NAME", name);
                intent.putExtra("USER_EMAIL", email);
                intent.putExtra("USER_PHOTO", photoUrl);

                startActivityForResult(intent,7878);

//                startActivity(new Intent(PanbookLoginActivity.this, AgreementActivity.class));
            } else if (requestCode == 1000) {                       // Naver ????????? ????????? ???????????? ??????. RESULT_OK ?????? ????????? ???????????? MainActivity ??? ??????


                final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();

                editor.putInt("SNS_TYPE", 2);

                editor.commit();

                Intent intent = new Intent(PanbookLoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        else if (requestCode == RC_SIGN_IN) {                 // ??? ?????? ????????? ?????? ????????? ?????? ?????? ????????? ???????????? Firebase Login ?????? ??????
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

    public void onClickFindAccountBtn(View view) {
        startActivity(new Intent(PanbookLoginActivity.this, FindAccountActivity.class));
    }

    public void onClickKakaoLoginBtn(View view) {
        if (!getNetworkState(mContext)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PanbookLoginActivity.this);
            builder.setMessage("??????????????? ????????? ??? ????????????. ???????????? ?????? ????????? ????????? ?????????.");
            builder.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    onClickKakaoLoginBtn(view);
                }
            });

            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }
        initKakaotalkLogin();
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, PanbookLoginActivity.this);
    }

    public void onClickNaverLoginBtn(View view) {
        if (!getNetworkState(mContext)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PanbookLoginActivity.this);
            builder.setMessage("??????????????? ????????? ??? ????????????. ???????????? ?????? ????????? ????????? ?????????.");
            builder.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    onClickNaverLoginBtn(view);
                }
            });

            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }
        startActivityForResult(new Intent(PanbookLoginActivity.this, NaverSignupActivity.class), 1000);
    }

    public void onClickDaumLoginBtn(View view) {                    // XML ?????? GONE ????????? ???????????? ?????? ???????????? ??????

    }

    public void onClickFacebookLoginBtn(View view) {
        if (!getNetworkState(mContext)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PanbookLoginActivity.this);
            builder.setMessage("??????????????? ????????? ??? ????????????. ???????????? ?????? ????????? ????????? ?????????.");
            builder.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    onClickFacebookLoginBtn(view);
                }
            });

            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }
        startActivityForResult(new Intent(PanbookLoginActivity.this, FacebookSignupActivity.class), 1000);
    }

    public void onClickGoogleLoginBtn(View view) {
        if (!getNetworkState(mContext)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PanbookLoginActivity.this);
            builder.setMessage("??????????????? ????????? ??? ????????????. ???????????? ?????? ????????? ????????? ?????????.");
            builder.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    onClickGoogleLoginBtn(view);
                }
            });

            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }
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

                            final SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

                            SharedPreferences.Editor editor = pref.edit();

                            editor.putInt("SNS_TYPE", 5);

                            editor.commit();

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
                Toast.makeText(PanbookLoginActivity.this, "???????????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PanbookLoginActivity.this, "???????????? ??????????????????. SNS ???????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();

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

                                        Intent intent = new Intent(PanbookLoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(PanbookLoginActivity.this, "?????? ???????????? ?????????????????????. ?????? ??? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(PanbookLoginActivity.this, "???????????? ????????? ???????????? ????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
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
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        intent.putExtra("TITLE", "?????? ?????? ??????");
                                        intent.putExtra("CONTENTS", "???????????? ?????? ?????? ?????? ???????????????.\n?????? ???????????? ???????????????????");
                                        intent.putExtra("TWOBTN", true);
                                        startActivityForResult(intent, 1010);
                                          Toast.makeText(PanbookLoginActivity.this, "?????? ???????????? ??????????????????. ?????? ?????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(PanbookLoginActivity.this, "?????? ???????????? ??????????????????. ????????? ?????? ????????? ?????????.", Toast.LENGTH_LONG).show();
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
