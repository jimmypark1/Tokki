package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import okhttp3.OkHttpClient;

public class InputRecommendCodeActivity extends AppCompatActivity {
//    private String strEmail;
//    private String strSNSID;
//    private String strNickName;
//    private String strPhoto;
//    private int    nSNS;

    private EditText inputCode1View, inputCode2View, inputCode3View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recommand_code);

        inputCode1View = findViewById(R.id.inputCode1View);
        inputCode2View = findViewById(R.id.inputCode2View);
        inputCode3View = findViewById(R.id.inputCode3View);
    }
    
    public void onClickCodeBtn(View view) {
        if(inputCode1View.getText().toString().length() < 4 || inputCode2View.getText().toString().length() < 4 || inputCode3View.getText().toString().length() < 4) {
            Toast.makeText(this, "추천인 코드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
//        Toast.makeText(this, "준비 중입니다.", Toast.LENGTH_SHORT).show();
        CommonUtils.showProgressDialog(InputRecommendCodeActivity.this, "서버와 접속중입니다.");

        final String strRecommendCode = inputCode1View.getText().toString() + "-" + inputCode2View.getText().toString() + "-" + inputCode3View.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                final boolean bResult = HttpClient.sendRecommendCode(new OkHttpClient(), pref.getString("USER_ID", "Guest"), strRecommendCode);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(InputRecommendCodeActivity.this, "추천인 코드가 잘못되었습니다. 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(InputRecommendCodeActivity.this, "추천인 코드가 등록 되어 500 당근을 지급 받았습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(InputRecommendCodeActivity.this, RegisterCompleteActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickNextBtn(View view) {
        Intent intent = new Intent(InputRecommendCodeActivity.this, RegisterCompleteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}