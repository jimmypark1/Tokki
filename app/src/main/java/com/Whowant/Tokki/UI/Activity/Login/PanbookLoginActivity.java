package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class PanbookLoginActivity extends AppCompatActivity {
    private EditText inputIDView;
    private EditText inputPWView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panbook_login);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Panbook 로그인");
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

    public void onClickRegisterBtn(View view) {
        Intent intent = new Intent(PanbookLoginActivity.this, AgreementActivity.class);
        startActivity(intent);
//        Intent intent = new Intent(PanbookLoginActivity.this, PersonalInfoActivity.class);
//        startActivity(intent);
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

    public void onClickBackBtn(View view) {
        finish();
    }

    public void onClickFindAccountBtn(View view) {
        startActivity(new Intent(PanbookLoginActivity.this, FindAccountActivity.class));
    }

    public void onClickFindPWBtn(View view) {
        startActivity(new Intent(PanbookLoginActivity.this, FindPasswordActivity.class));
    }
}
