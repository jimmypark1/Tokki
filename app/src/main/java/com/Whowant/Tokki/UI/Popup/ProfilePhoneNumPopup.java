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

public class ProfilePhoneNumPopup extends AppCompatActivity {
    private EditText inputPhonenumView;
    String strUserID;
    String strPhoeNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_phone_num_popup);

        inputPhonenumView = findViewById(R.id.inputPhonenumView);
        strUserID = getIntent().getStringExtra("USER_ID");
        strPhoeNum = getIntent().getStringExtra("USER_PHONENUM");
        inputPhonenumView.setText(strPhoeNum);
        inputPhonenumView.setSelection(strPhoeNum.length());
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickOKBtn(View view) {
        String strNewPhoneNum = inputPhonenumView.getText().toString();

        if(strNewPhoneNum.length() == 0) {
            Toast.makeText(this, "변경할 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if(strNewPhoneNum.equals(strPhoeNum)) {
            Toast.makeText(this, "같은 전화번호로 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(ProfilePhoneNumPopup.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), strUserID, "USER_PHONENUM", strNewPhoneNum);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(ProfilePhoneNumPopup.this, "전화번호 변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_PHONENUM", strNewPhoneNum);
                        editor.commit();

                        finish();
                    }
                });
            }
        }).start();
    }
}
