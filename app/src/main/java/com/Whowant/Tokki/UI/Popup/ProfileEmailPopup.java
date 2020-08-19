package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import okhttp3.OkHttpClient;

public class ProfileEmailPopup extends AppCompatActivity {
    private EditText inputEmailView;
    private String strEmail;
    private String strUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_email_popup);

        strEmail = getIntent().getStringExtra("USER_EMAIL");
        strUserID = getIntent().getStringExtra("USER_ID");

        inputEmailView = findViewById(R.id.inputEmailView);
        inputEmailView.setText(strEmail);
        inputEmailView.setSelection(strEmail.length());
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickOKBtn(View view) {
        String strNewEmail = inputEmailView.getText().toString();

        if(strNewEmail.length() == 0) {
            Toast.makeText(this, "변경할 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if(strNewEmail.equals(strEmail)) {
            Toast.makeText(this, "같은 이메일 주소로 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(ProfileEmailPopup.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), strUserID, "USER_EMAIL", strNewEmail);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(ProfileEmailPopup.this, "이메일 변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_EMAIL", strNewEmail);
                        editor.commit();

                        finish();
                    }
                });
            }
        }).start();
    }
}
