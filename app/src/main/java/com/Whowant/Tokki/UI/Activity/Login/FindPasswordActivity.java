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
import com.Whowant.Tokki.UI.Activity.Admin.AproveWaitingEpisodeListActivity;
import com.Whowant.Tokki.UI.Activity.Work.GenreSelectActivity;
import com.Whowant.Tokki.UI.Popup.ChangePasswordPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;

import java.util.HashMap;

import okhttp3.OkHttpClient;

public class FindPasswordActivity extends AppCompatActivity {
    private EditText inputIDView;
    private EditText inputEmailView;
    private String strEmail;
    private Button sendMailBtn;
    private Button registerBtn;
    private EditText inputAuthNumView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        inputIDView = findViewById(R.id.inputIDView);
        inputEmailView = findViewById(R.id.inputEmailView);
        sendMailBtn = findViewById(R.id.sendMailBtn);
        registerBtn = findViewById(R.id.registerBtn);
        inputAuthNumView = findViewById(R.id.inputAuthNumView);
        inputAuthNumView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(inputAuthNumView.getText().length() >= 6) {
                    registerBtn.setEnabled(true);
                    registerBtn.setTextColor(ContextCompat.getColor(FindPasswordActivity.this, R.color.colorWhite));
                    registerBtn.setBackgroundResource(R.drawable.common_btn_bg);
                } else {
                    registerBtn.setEnabled(false);
                    registerBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
                    registerBtn.setTextColor(Color.parseColor("#969696"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("패스워드 찾기");
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

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickFindIDBtn(View view) {
        startActivity(new Intent(FindPasswordActivity.this, FindAccountActivity.class));
        finish();
    }

    public void onClickSendBtn(View view) {
        strEmail = inputEmailView.getText().toString();

        if(strEmail.length() == 0) {
            Toast.makeText(this, "이메일 주소를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        sendMailBtn.setText("재전송");
//        registerBtn.setBackgroundResource(R.drawable.common_btn_bg);
//        registerBtn.setTextColor(ContextCompat.getColor(FindPasswordActivity.this, R.color.colorWhite));
//        registerBtn.setEnabled(true);

        CommonUtils.showProgressDialog(FindPasswordActivity.this, "인증번호 전송을 요청하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendEmail(new OkHttpClient(), strEmail);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(FindPasswordActivity.this, "인증번호 전송을 실패했습니다. 이메일 주소를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        inputAuthNumView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();

    }

    public void onClickFindAccountBtn(View view) {
        final String strUserID = inputIDView.getText().toString();

        if(strUserID.length() == 0) {
            Toast.makeText(FindPasswordActivity.this, "ID를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        final String strAuthNum = inputAuthNumView.getText().toString();

        if(strAuthNum.length() < 6) {
            Toast.makeText(this, "인증번호 6자리를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(FindPasswordActivity.this, "인증번호를 확인하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestAuthNum(new OkHttpClient(), strEmail, strAuthNum);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!bResult) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(FindPasswordActivity.this, "인증번호가 틀렸습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        findPassword();
                    }
                });
            }
        }).start();
    }

    private void findPassword() {
        final String strUserID = inputIDView.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("USER_ID", strUserID);
                userMap.put("USER_EMAIL", strEmail);

                final boolean bResult = HttpClient.findPassword(new OkHttpClient(), userMap);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult == false) {
                            Toast.makeText(FindPasswordActivity.this, "입력하신 계정정보가 올바르지 않습니다.\n다시 확인해 주세요.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Intent intent = new Intent(FindPasswordActivity.this, ChangePasswordPopup.class);
                        intent.putExtra("USER_ID", strUserID);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }
}
