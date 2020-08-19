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
import com.google.firebase.database.core.Repo;

import okhttp3.OkHttpClient;

public class ReportPopup extends AppCompatActivity {
    private EditText inputReasonView;
    private int nCommentID;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_popup);

        nCommentID = getIntent().getIntExtra("COMMENT_ID", -1);

        inputReasonView = findViewById(R.id.inputReasonView);
        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
    }

    public void onClickAddBtn(View view) {
        String strReason = inputReasonView.getText().toString();

        if(strReason.length() == 0) {
            Toast.makeText(this, "신고 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(ReportPopup.this, "신고 내용을 전송 중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //OkHttpClient httpClient, String strUserID, String strAdminID, String strReason
                final int nResult = HttpClient.requestCommentReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nCommentID, strReason);
//                boolean bResult = HttpClient.requestCommentReport(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nCommentID, strReason);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(nResult == 0) {
                            Toast.makeText(ReportPopup.this, "신고 되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if(nResult == 1) {
                            Toast.makeText(ReportPopup.this, "이미 신고한 댓글입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ReportPopup.this, "신고에 실패하였습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickCloseBtn(View view) {
        finish();
    }
}
