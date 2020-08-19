package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Popup.CommonPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.OkHttpClient;

public class FindAccountActivity extends AppCompatActivity {
    private EditText inputEmailView;
    private EditText inputAuthNumView;
    private Button registerBtn;
    private Button sendMailBtn;

    private String strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_account);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        inputEmailView = findViewById(R.id.inputEmailView);
        inputAuthNumView = findViewById(R.id.inputAuthNumView);
        registerBtn = findViewById(R.id.registerBtn);
        sendMailBtn = findViewById(R.id.sendMailBtn);

        inputAuthNumView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(inputAuthNumView.getText().length() >= 6) {
                    registerBtn.setEnabled(true);
                    registerBtn.setBackgroundResource(R.drawable.common_btn_bg);
                } else {
                    registerBtn.setEnabled(false);
                    registerBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("계정 찾기");
        }
    }

    public void onClickTopLeftBtn(View view) {
        finish();
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

    public void onClickFindPassBtn(View view) {
        startActivity(new Intent(FindAccountActivity.this, FindPasswordActivity.class));
        finish();
    }

    public void onClickSendBtn(View view) {
        strEmail = inputEmailView.getText().toString();

        if(strEmail.length() == 0) {
            Toast.makeText(this, "이메일 주소를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        sendMailBtn.setText("재전송");
        registerBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
        registerBtn.setTextColor(Color.parseColor("#ffffff"));
        registerBtn.setEnabled(true);

        CommonUtils.showProgressDialog(FindAccountActivity.this, "인증번호 전송을 요청하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendEmail(new OkHttpClient(), strEmail);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(FindAccountActivity.this, "인증번호 전송을 실패했습니다. 이메일 주소를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        inputAuthNumView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();

    }

    public void onClickRegisterBtn(View view) {
        final String strAuthNum = inputAuthNumView.getText().toString();

        if(strAuthNum.length() < 6) {
            Toast.makeText(this, "인증번호 6자리를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(FindAccountActivity.this, "인증번호를 확인하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestAuthNum(new OkHttpClient(), strEmail, strAuthNum);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!bResult) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(FindAccountActivity.this, "인증번호가 틀렸습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        findAccount();
                    }
                });
            }
        }).start();
    }


    public void findAccount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("USER_EMAIL", strEmail);

                final JSONObject resultObject = HttpClient.findAccount(new OkHttpClient(), userMap);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(resultObject == null) {
                            Toast.makeText(FindAccountActivity.this, "계정 찾기에 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            String strResult = resultObject.getString("RESULT");

                            if(strResult.equals("SUCCESS")) {
                                String strRegisterSNS = resultObject.getString("REGISTER_SNS");
                                String strUserEmail = resultObject.getString("USER_EMAIL");
                                String strUserID = resultObject.getString("USER_ID");
                                String strRegisterDate = resultObject.getString("REGISTER_DATE");

                                if(strUserID.contains("@") && strUserID.contains(".")) {            // email 로 판단
                                    int nLoc = strUserID.indexOf("@");
                                    String strHeader = strUserID.substring(0, nLoc);
                                    //ddrzealot  9

                                    if(nLoc/2 > 0) {
                                        strHeader = strHeader.substring(0, nLoc/2);

                                        for(int i = 0 ; i < nLoc / 2 ; i++) {
                                            strHeader += "*";
                                        }
                                    }

                                    int nLoc2 = strUserID.indexOf(".");
                                    String strMiddle = strUserID.substring(nLoc+1, nLoc2);

                                    if(nLoc2/2 > 0) {
                                        strMiddle = strMiddle.substring(0, nLoc2/2);

                                        for(int i = 0 ; i < nLoc2 / 2 ; i++) {
                                            strMiddle += "*";
                                        }
                                    }

                                    String strTail = strUserID.substring(nLoc2);

                                    strUserID = strHeader + "@" + strMiddle + strTail;
                                } else {
                                    int nLength = strUserID.length();

                                    if(nLength/2 > 0) {
                                        strUserID = strUserID.substring(0, nLength/2);

                                        for(int i = 0 ; i < nLength / 2 ; i++) {
                                            strUserID += "*";
                                        }
                                    }
                                }

                                strRegisterDate = strRegisterDate.substring(0, 10);

                                if(strRegisterSNS.equals("0")) {                // Panbook 계정
                                    Intent intent = new Intent(FindAccountActivity.this, CommonPopup.class);
                                    intent.putExtra("TITLE", "계정 찾기");
                                    intent.putExtra("CONTENTS", "아이디 조회 결과\n입력하신 정보와 일치하는 계정은\n아래와 같습니다.\n\n" + strUserID + "\n\n("
                                                                            + strRegisterDate + " 가입)");
                                    intent.putExtra("TWOBTN", false);
                                    intent.putExtra("CENTER", true);
                                    startActivity(intent);
                                } else {
                                    if(strRegisterSNS.equals("1"))
                                        strRegisterSNS = "KakaoTalk";
                                    else if(strRegisterSNS.equals("2")) {
                                        strRegisterSNS = "Naver";
                                    } else if(strRegisterSNS.equals("3")) {
                                        strRegisterSNS = "Daum";
                                    } else if(strRegisterSNS.equals("4")) {
                                        strRegisterSNS = "Facebook";
                                    } else if(strRegisterSNS.equals("5")) {
                                        strRegisterSNS = "Twitter";
                                    } else if(strRegisterSNS.equals("6")) {
                                        strRegisterSNS = "Instagram";
                                    }

                                    Intent intent = new Intent(FindAccountActivity.this, CommonPopup.class);
                                    intent.putExtra("TITLE", "소셜 계정 찾기");
                                    intent.putExtra("CONTENTS", "아이디 조회 결과\n입력하신 정보와 일치하는 소셜 계정은\n아래와 같습니다.\n\n" + strRegisterSNS +
                                                                            "\n" + strUserID + "\n\n(" + strRegisterDate + " 가입)");
                                    intent.putExtra("TWOBTN", false);
                                    intent.putExtra("CENTER", true);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(FindAccountActivity.this, "계정 찾기에 실패했습니다.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickFindAccountBtn(View view) {
        final String strAuthNum = inputAuthNumView.getText().toString();

        if(strAuthNum.length() < 6) {
            Toast.makeText(this, "인증번호 6자리를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(FindAccountActivity.this, "인증번호를 확인하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestAuthNum(new OkHttpClient(), strEmail, strAuthNum);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!bResult) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(FindAccountActivity.this, "인증번호가 틀렸습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        findAccount();
                    }
                });
            }
        }).start();
    }
}