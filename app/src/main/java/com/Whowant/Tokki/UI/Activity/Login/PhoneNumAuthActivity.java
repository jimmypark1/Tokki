package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.BuildConfig;
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
    private TextView inputAuthNumTimer;

    CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 60000;
    final int COUNT_DOWN_INTERVAL = 1000;
    boolean isTimeOver = false;
    long AuthCount;
    int nAuthType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_num_auth);

        nAuthType = getIntent().getIntExtra("AUTH_TYPE",0);

        inputPhoneNumView = findViewById(R.id.inputPhoneNumView);
        inputAuthNumView = findViewById(R.id.inputAuthNumView);
        inputAuthNumView.setEnabled(false);
        authNumBtn = findViewById(R.id.authNumBtn);
        nextBtn = findViewById(R.id.authBtn);

        TextView title = findViewById(R.id.titleView);
        if(nAuthType == 1)
        {
            title.setText("비밀번호 찾기");
        }

        authNumBtn.setEnabled(false);
        nextBtn.setEnabled(false);

        inputAuthNumTimer = findViewById(R.id.inputAuthNumTimer);

        TextWatcher inputPhoneNumTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputPhoneNumView.getText().toString().length() == 0) {
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
                if (inputAuthNumView.getText().toString().length() == 0) {
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

    @Override
    protected void onPause() {
        super.onPause();

        cancel();
        inputPhoneNumView.setClickable(false);
        inputPhoneNumView.setFocusable(false);
        authNumBtn.setEnabled(false);
        authNumBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
        authNumBtn.setTextColor(Color.parseColor("#999999"));
        inputAuthNumView.setClickable(false);
        inputAuthNumView.setFocusable(false);
    }

    public void countDownTimer() {
        isTimeOver = false;

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                AuthCount = millisUntilFinished / 1000;

                if ((AuthCount - ((AuthCount / 60) * 60)) >= 10) {
                    inputAuthNumTimer.setText((AuthCount / 60) + ":" + (AuthCount - ((AuthCount / 60) * 60)));
                } else {
                    inputAuthNumTimer.setText((AuthCount / 60) + ":0" + (AuthCount - ((AuthCount / 60) * 60)));
                }
            }

            @Override
            public void onFinish() {
                isTimeOver = true;
            }
        }.start();
    }

    public final void cancel() {
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    public void onClickBackBtn(View view) {
        finish();
    }

    public void onClickRequestAuthNumBtn(View view) {
        CommonUtils.showProgressDialog(PhoneNumAuthActivity.this, "서버와 통신중입니다.");

        authNumBtn.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                authNumBtn.setText("인증번호 재요청");
                authNumBtn.setEnabled(true);
            }
        }, 2000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int bResult = HttpClient.requestAuthNum(new OkHttpClient(), inputPhoneNumView.getText().toString(),nAuthType);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (bResult == -1) {
                            Toast.makeText(PhoneNumAuthActivity.this, "인증번호 요청에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (bResult == 1) {
                            if(nAuthType == 0)
                            {
                                Toast.makeText(PhoneNumAuthActivity.this, "이미 가입된 번호입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }

                        inputAuthNumView.setEnabled(true);
                        Toast.makeText(PhoneNumAuthActivity.this, "인증번호를 요청했습니다.", Toast.LENGTH_SHORT).show();
                        countDownTimer();
                        inputAuthNumView.getText().clear();
                    }
                });
            }
        }).start();
    }

    public void onClickRequestAuthBtn(View view) {
        if (inputAuthNumView.getText().toString().length() == 0) {
            Toast.makeText(PhoneNumAuthActivity.this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isTimeOver) {
            Toast.makeText(PhoneNumAuthActivity.this, "인증번호 입력 시간이 초과되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(PhoneNumAuthActivity.this, "서버와 통신중입니다.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String strResult = HttpClient.requestAuth(new OkHttpClient(), inputPhoneNumView.getText().toString(), inputAuthNumView.getText().toString(),nAuthType);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (strResult == null) {
                            Toast.makeText(PhoneNumAuthActivity.this, "인증번호 요청에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (strResult.equals("SUCCESS")) {
                            if(nAuthType == 0)
                            {
                                Intent intent = new Intent(PhoneNumAuthActivity.this, AgreementActivity.class);
                                intent.putExtra("SNS", 0);
                                intent.putExtra("ID", inputPhoneNumView.getText().toString());
                                startActivity(intent);

                            }
                            else
                            {
                                Intent intent = new Intent(PhoneNumAuthActivity.this, PWRegisterActivity.class);
                                intent.putExtra("SNS", 0);
                                intent.putExtra("AUTH_TYPE", 1);

                                intent.putExtra("ID", inputPhoneNumView.getText().toString());
                                startActivity(intent);

                            }

                        } else if (strResult.equals("DISCORD")) {
                            Toast.makeText(PhoneNumAuthActivity.this, "인증번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                        }

                        else if (strResult.equals("DUPLICATE")) // 테스트용
                        {
                            if(BuildConfig.DEBUG)
                            {
                                Intent intent = new Intent(PhoneNumAuthActivity.this, PWRegisterActivity.class);
                                intent.putExtra("SNS", 0);
                                intent.putExtra("AUTH_TYPE", 1);

                                intent.putExtra("ID", inputPhoneNumView.getText().toString());
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(PhoneNumAuthActivity.this, "본인의 휴대폰이 아닙니다.", Toast.LENGTH_SHORT).show();

                            }
//

                        }


                    }
                });
            }
        }).start();
    }
}