package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;

public class EmailAuthActivity extends AppCompatActivity {
    private String strEmail;
    private String strSNSID;
    private String strNickName;
    private String strPhoto;
    private int    nSNS;

    private LinearLayout topLayout;
    private EditText inputEmailView;
    private EditText inputAuthNumView;
    private Button registerBtn;
    private Button sendMailBtn;

    private boolean bSend = false;
    private boolean bProfile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_auth);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }
//
//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle("이메일 인증");
//        }

        bProfile = getIntent().getBooleanExtra("PROFILE", false);

        strEmail = getIntent().getStringExtra("USER_EMAIL");
        strSNSID = getIntent().getStringExtra("SNS_ID");
        strNickName = getIntent().getStringExtra("USER_NAME");
        strPhoto = getIntent().getStringExtra("USER_PHOTO");
        nSNS = getIntent().getIntExtra("SNS", 0);

        topLayout = findViewById(R.id.topLayout);
        inputEmailView = findViewById(R.id.inputEmailView);
        inputAuthNumView = findViewById(R.id.inputAuthNumView);
        registerBtn = findViewById(R.id.registerBtn);
        sendMailBtn = findViewById(R.id.sendMailBtn);

//        if(bProfile) {
//            topLayout.setVisibility(View.INVISIBLE);
//        }

        inputEmailView.setText(strEmail);
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

    public void onClickSendBtn(View view) {
        strEmail = inputEmailView.getText().toString();
        
        if(strEmail.length() == 0) {
            Toast.makeText(this, "이메일 주소를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        sendMailBtn.setText("재전송");
        registerBtn.setBackgroundResource(R.drawable.round_square_btn_bg);
        registerBtn.setEnabled(true);

        CommonUtils.showProgressDialog(EmailAuthActivity.this, "인증번호 전송을 요청하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendEmail(new OkHttpClient(), strEmail);
                
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(EmailAuthActivity.this, "인증번호 전송을 실패했습니다. 이메일 주소를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        inputAuthNumView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();

    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickRegisterBtn(View view) {
        final String strAuthNum = inputAuthNumView.getText().toString();
        
        if(strAuthNum.length() < 6) {
            Toast.makeText(this, "인증번호 6자리를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(EmailAuthActivity.this, "인증번호를 확인하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestAuthNum(new OkHttpClient(), strEmail, strAuthNum);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(EmailAuthActivity.this, "인증번호가 틀렸습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(bProfile) {
                            setResult(RESULT_OK);
                            finish();
                            return;
                        }

                        Intent intent = new Intent(EmailAuthActivity.this, InputRecommendCodeActivity.class);
                        intent.putExtra("SNS", nSNS);
                        intent.putExtra("SNS_ID", strSNSID);
                        intent.putExtra("USER_NAME", strNickName);
                        intent.putExtra("USER_EMAIL", strEmail);
                        intent.putExtra("USER_PHOTO", strPhoto);
                        startActivity(intent);
                    }
                });
            }
        }).start();
    }
}
