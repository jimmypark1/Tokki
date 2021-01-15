package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import okhttp3.OkHttpClient;

public class MemberWithdrawActivity extends AppCompatActivity {
    private EditText inputReasoneView;
    private SharedPreferences pref;
    private String strUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_withdraw);

        inputReasoneView = findViewById(R.id.inputReasoneView);
        pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
        strUserID = getIntent().getStringExtra("USER_ID");
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickOKBtn(View view) {
        String strReason = inputReasoneView.getText().toString();

        if(strReason.length() == 0) {
            Toast.makeText(this, "탈퇴 사유를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(MemberWithdrawActivity.this, "탈퇴 사유를 전송 중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //OkHttpClient httpClient, String strUserID, String strAdminID, String strReason
                boolean bResult = HttpClient.requestWithdrawUser(new OkHttpClient(), strUserID, pref.getString("USER_ID", "Guest"), strReason);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(MemberWithdrawActivity.this, "탈퇴 처리되었습니다.", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(MemberWithdrawActivity.this, "탈퇴되지 않았습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }).start();
    }
}
