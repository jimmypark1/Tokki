package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import okhttp3.OkHttpClient;

public class ProfileNamePopup extends AppCompatActivity {
    private EditText inputNameView;
    private String strName;
    private String strUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_name_popup);

        strName = getIntent().getStringExtra("USER_NAME");
        strUserID = getIntent().getStringExtra("USER_ID");

        inputNameView = findViewById(R.id.inputNameView);
        inputNameView.setText(strName);
        inputNameView.setSelection(strName.length());

        inputNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(inputNameView.getText().toString().length() > 8) {
                    String strTemp = inputNameView.getText().toString().substring(0, 8);
                    inputNameView.setText(strTemp);
                    inputNameView.setSelection(strTemp.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickOKBtn(View view) {
        String strNewName = inputNameView.getText().toString();

        if(strNewName.length() == 0) {
            Toast.makeText(this, "변경할 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if(strNewName.equals(strName)) {
            Toast.makeText(this, "같은 닉네임으로 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(ProfileNamePopup.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), strUserID, "USER_NAME", strNewName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(ProfileNamePopup.this, "닉네임 변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_NAME", strNewName);
                        editor.commit();

                        finish();
                    }
                });
            }
        }).start();
    }
}
