package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import java.util.HashMap;

import okhttp3.OkHttpClient;

public class ChangePasswordPopup extends AppCompatActivity {
    private EditText inputPWView;
    private EditText inputPWCheckView;

    private String strUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_popup);

        strUserID = getIntent().getStringExtra("USER_ID");
        inputPWView = findViewById(R.id.inputPWView);
        inputPWCheckView = findViewById(R.id.inputPWCheckView);
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickOKBtn(View view) {
        String strPW = inputPWView.getText().toString();
        String strPWCheck = inputPWCheckView.getText().toString();

        if(strPW.length() == 0) {
            Toast.makeText(this, "패스워드를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(strPWCheck.length() == 0) {
            Toast.makeText(this, "패스워드를 다시 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        if(!strPW.equals(strPWCheck)) {
            Toast.makeText(this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        CommonUtils.showProgressDialog(ChangePasswordPopup.this, "패스워드를 변경하고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("USER_ID", strUserID);
                userMap.put("USER_PW", strPW);
                boolean bResult = HttpClient.resetPassword(new OkHttpClient(), userMap);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            Toast.makeText(ChangePasswordPopup.this, "패스워드가 변경되었습니다. 로그인 해주세요.", Toast.LENGTH_LONG).show();
                            finish();
//                            Intent intent = new Intent(ChangePasswordPopup.this, LoginActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
                        } else {
                            Toast.makeText(ChangePasswordPopup.this, "패스워드 변경을 실패했습니다. 잠시후 다시 이용해 주세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}
