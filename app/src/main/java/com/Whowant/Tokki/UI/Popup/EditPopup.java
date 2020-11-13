package com.Whowant.Tokki.UI.Popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.DeviceUtils;
import com.Whowant.Tokki.Utils.SimplePreference;

import okhttp3.OkHttpClient;

public class EditPopup extends AppCompatActivity {

    public static final int TYPE_BUG_REPORT = 0;
    public static final int TYPE_ADD_BOOK_LIST = 1;
    public static final int TYPE_RENAME_BOOK_LIST = 2;

    int type = TYPE_BUG_REPORT;

    Activity mActivity;

    String readingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DeviceUtils.setStatusColor(this, "#b2000000", true);

        super.onCreate(savedInstanceState);

        mActivity = this;

        getData();
        setContentView(addTypeView());
        initView();
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            type = getIntent().getIntExtra("type", TYPE_BUG_REPORT);
            readingId = getIntent().getStringExtra("readingId");
        }
    }

    private int addTypeView() {
        switch (type) {
            case TYPE_BUG_REPORT:
                return R.layout.popup_bug_report;
            case TYPE_ADD_BOOK_LIST:
                return R.layout.popup_add_book_list;
            case TYPE_RENAME_BOOK_LIST:
                return R.layout.popup_rename_book_list;
        }

        return R.layout.popup_bug_report;
    }

    private void initView() {

    }

    // 닫기 버튼
    public void btnClose(View v) {
        finish();
    }

    // 신고하기 버튼
    public void btnReport(View v) {
        String summary = ((EditText) findViewById(R.id.et_popup_bug_report_summary)).getText().toString();
        String step = ((EditText) findViewById(R.id.et_popup_bug_report_step)).getText().toString();

        Toast.makeText(this, "신고하기 : " + summary + " / " + step, Toast.LENGTH_SHORT).show();
    }

    // 독서목록 추가 버튼
    public void btnAddBook(View v) {
        String name = ((EditText) findViewById(R.id.et_popup_add_book_list_name)).getText().toString();
        CreateReadingList(name);
    }

    // 독서목록 이름변경 버튼
    public void btnRenameBook(View v) {
        String name = ((EditText) findViewById(R.id.et_popup_rename_book_list_name)).getText().toString();
        renameReadingList(readingId, name);
    }

    private void CreateReadingList(String readingName) {
        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String strMyID = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");

                boolean bResult = HttpClient.CreateReadingList(new OkHttpClient(), strMyID, readingName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (bResult) {
                            Intent intent = new Intent();
                            intent.putExtra("name", readingName);
                            setResult(RESULT_OK, intent);
                        } else {
                            Toast.makeText(mActivity, "서버와의 통신에 실패했습니다.", Toast.LENGTH_LONG).show();
                            setResult(RESULT_CANCELED);
                        }
                        finish();
                    }
                });
            }
        }).start();
    }

    private void renameReadingList(String readingId, String readingName) {
        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.renameReadingList(new OkHttpClient(), readingId, readingName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (bResult) {
                            Intent intent = new Intent();
                            intent.putExtra("name", readingName);
                            setResult(RESULT_OK, intent);
                        } else {
                            Toast.makeText(mActivity, "서버와의 통신에 실패했습니다.", Toast.LENGTH_LONG).show();
                            setResult(RESULT_CANCELED);
                        }
                        finish();
                    }
                });
            }
        }).start();
    }
}