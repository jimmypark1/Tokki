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

public class EditMyCommentPopup extends AppCompatActivity {
    private SharedPreferences pref;
    private EditText inputNameView;
    private String strDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_comment_popup);

        pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        strDesc = pref.getString("USER_DESC", "");
        inputNameView = findViewById(R.id.inputNameView);

        inputNameView.setText(strDesc);
        inputNameView.setSelection(strDesc.length());
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickOKBtn(View view) {
        String strNewName = inputNameView.getText().toString();

        if(strNewName.length() == 0) {
            Toast.makeText(this, "변경할 내 소개를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if(strNewName.equals(strDesc)) {
            Toast.makeText(this, "같은 내용으로 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(EditMyCommentPopup.this, "데이터를 전송하고 있습니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestSendUserProfile(new OkHttpClient(), pref.getString("USER_ID", "Guest"), "USER_DESC", strNewName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(!bResult) {
                            Toast.makeText(EditMyCommentPopup.this, "내 소개 변경에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("USER_DESC", strNewName);
                        editor.commit();

                        finish();
                    }
                });
            }
        }).start();
    }
}
