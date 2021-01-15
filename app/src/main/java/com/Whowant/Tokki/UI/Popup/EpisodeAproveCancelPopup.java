package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import okhttp3.OkHttpClient;

public class EpisodeAproveCancelPopup extends AppCompatActivity {
    private EditText inputReasonView;
    private int nEpisodeID;
    private SharedPreferences pref;
    private boolean bCancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_aprove_cancel_popup);

        nEpisodeID = getIntent().getIntExtra("EPISODE_ID", -1);
        bCancel = getIntent().getBooleanExtra("CANCEL", false);

        TextView titleView = findViewById(R.id.titleView);
        if(bCancel)
            titleView.setText("게시 취소");

        inputReasonView = findViewById(R.id.inputReasonView);
        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
    }

    public void onClickAddBtn(View view) {
        String strReason = inputReasonView.getText().toString();
        
        if(strReason.length() == 0) {
            if(bCancel)
                Toast.makeText(this, "승인 거절 사유를 입력해주세요.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "게시 취소 사유를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonUtils.showProgressDialog(EpisodeAproveCancelPopup.this, "서버와 통신중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //OkHttpClient httpClient, String strUserID, String strAdminID, String strReason
                boolean bResult = HttpClient.requestAproveCancel(new OkHttpClient(), pref.getString("USER_ID", "Guest"), nEpisodeID, strReason);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult) {
                            if(bCancel)
                                Toast.makeText(EpisodeAproveCancelPopup.this, "게시 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(EpisodeAproveCancelPopup.this, "게시 거절되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            if(bCancel)
                                Toast.makeText(EpisodeAproveCancelPopup.this, "게시 취소에 실패하였습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(EpisodeAproveCancelPopup.this, "게시 거절에 실패하였습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
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
