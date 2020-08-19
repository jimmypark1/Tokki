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

public class ProfileBirthdayPopup extends AppCompatActivity {
    String strUserID;
    String strUserBirthday;

    private EditText inputBirthdayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_birthday_popup);

        inputBirthdayView = findViewById(R.id.inputBirthdayView);

        strUserID = getIntent().getStringExtra("USER_ID");
        strUserBirthday = getIntent().getStringExtra("USER_BIRTHDAY");

        inputBirthdayView.setText(strUserBirthday);
        inputBirthdayView.setSelection(strUserBirthday.length());
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickOKBtn(View view) {
        String strNewBirthday = inputBirthdayView.getText().toString();

        if(strNewBirthday.length() == 0) {
            Toast.makeText(this, "변경할 생년월일 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if(strNewBirthday.equals(strUserBirthday)) {
            Toast.makeText(this, "같은 생년월일로 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(ProfileBirthdayPopup.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), strUserID, "USER_BIRTHDAY", strNewBirthday);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(ProfileBirthdayPopup.this, "생년월일 변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_BIRTHDAY", strNewBirthday);
                        editor.commit();

                        finish();
                    }
                });
            }
        }).start();
    }
}
