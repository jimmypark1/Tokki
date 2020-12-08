package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import okhttp3.OkHttpClient;

public class ChangeTitlePopup extends AppCompatActivity {
    private EditText inputTitleView;
    private int      nEpisodeID;
    private String   strTitle;
    private Intent oldIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_title_popup);

        inputTitleView = findViewById(R.id.inputTitleView);
        oldIntent = getIntent();
        nEpisodeID = oldIntent.getIntExtra("EPISODE_ID", 0);
        strTitle = oldIntent.getStringExtra("TITLE");

        inputTitleView.setText(strTitle);
        if (strTitle.length() <= 30)
            inputTitleView.setSelection(strTitle.length());
        inputTitleView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_GO:
                    case EditorInfo.IME_ACTION_SEND:
                        onClickOKBtn(inputTitleView);
                        break;
                    default:
                        // 기본 엔터키 동작
                        return false;
                }
                return true;
            }
        });
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickOKBtn(View view) {
        strTitle = inputTitleView.getText().toString();
        if(strTitle.length() == 0) {
            Toast.makeText(ChangeTitlePopup.this, "변경할 제목을 입력하세요", Toast.LENGTH_LONG).show();
            return;
        }

        CommonUtils.showProgressDialog(ChangeTitlePopup.this, "제목을 바꾸고 있습니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.requestChangeEpisodeTitle(new OkHttpClient(), strTitle, nEpisodeID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bResult == false) {
                            Toast.makeText(ChangeTitlePopup.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        oldIntent.putExtra("TITLE", strTitle);
                        setResult(RESULT_OK, oldIntent);
                        finish();
                    }
                });
            }
        }).start();
    }
}
