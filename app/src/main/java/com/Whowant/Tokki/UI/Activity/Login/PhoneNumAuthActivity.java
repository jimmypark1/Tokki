package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.UserProfileActivity;
import com.Whowant.Tokki.Utils.CommonUtils;

import okhttp3.OkHttpClient;

public class PhoneNumAuthActivity extends AppCompatActivity {
    private EditText inputPhoneNumView;
    private EditText inputAuthNumView;
    private Button authNumBtn;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_num_auth);

        inputPhoneNumView = findViewById(R.id.inputPhoneNumView);
        inputAuthNumView = findViewById(R.id.inputAuthNumView);
        inputAuthNumView.setEnabled(false);
        authNumBtn = findViewById(R.id.authNumBtn);
        nextBtn = findViewById(R.id.authBtn);

        authNumBtn.setEnabled(false);
        nextBtn.setEnabled(false);

        TextWatcher inputPhoneNumTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputPhoneNumView.getText().toString().length() == 0) {
                    authNumBtn.setEnabled(false);
                    authNumBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
                    authNumBtn.setTextColor(Color.parseColor("#999999"));
                } else {
                    authNumBtn.setEnabled(true);
                    authNumBtn.setBackgroundResource(R.drawable.common_btn_bg);
                    authNumBtn.setTextColor(Color.parseColor("#ffffff"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        inputPhoneNumView.addTextChangedListener(inputPhoneNumTextWatcher);

        TextWatcher inputAuthNumTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputAuthNumView.getText().toString().length() == 0) {
                    nextBtn.setEnabled(false);
                    nextBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
                    nextBtn.setTextColor(Color.parseColor("#999999"));
                } else {
                    nextBtn.setEnabled(true);
                    nextBtn.setBackgroundResource(R.drawable.common_btn_bg);
                    nextBtn.setTextColor(Color.parseColor("#ffffff"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        inputAuthNumView.addTextChangedListener(inputAuthNumTextWatcher);
    }

    public void onClickBackBtn(View view) {
        finish();
    }

    public void onClickRequestAuthNumBtn(View view) {
        CommonUtils.showProgressDialog(PhoneNumAuthActivity.this, "서버와 통신중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestAuthNum(new OkHttpClient(), inputPhoneNumView.getText().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(PhoneNumAuthActivity.this, "인증번호 요청에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        inputAuthNumView.setEnabled(true);
                        Toast.makeText(PhoneNumAuthActivity.this, "인증번호를 요청했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public void onClickRequestAuthBtn(View view) {
        if(inputAuthNumView.getText().toString().length() == 0) {
            Toast.makeText(PhoneNumAuthActivity.this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(PhoneNumAuthActivity.this, "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String strResult = HttpClient.requestAuth(new OkHttpClient(), inputPhoneNumView.getText().toString(), inputAuthNumView.getText().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(strResult == null) {
                            Toast.makeText(PhoneNumAuthActivity.this, "인증번호 요청에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(strResult.equals("SUCCESS")) {
                            Intent intent = new Intent(PhoneNumAuthActivity.this, AgreementActivity.class);
                            intent.putExtra("SNS", 0);
                            intent.putExtra("ID", inputPhoneNumView.getText().toString());
                            startActivity(intent);
                        } else if(strResult.equals("DISCORD")) {
                            Toast.makeText(PhoneNumAuthActivity.this, "인증번호가 맞지않습니다.", Toast.LENGTH_SHORT).show();
                        } else if(strResult.equals("TIMEOVER")) {
                            Toast.makeText(PhoneNumAuthActivity.this, "인증시간이 초과되었습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }).start();
    }
}