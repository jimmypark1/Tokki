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
    int readingId;
    String strName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DeviceUtils.setStatusColor(this, "#b2000000", true);
        super.onCreate(savedInstanceState);

        mActivity = this;
        getData();
        setContentView(addTypeView());
        if (type == TYPE_RENAME_BOOK_LIST)
            initView();
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            type = getIntent().getIntExtra("type", TYPE_BUG_REPORT);
            readingId = getIntent().getIntExtra("readingId", 0);
            strName = getIntent().getStringExtra("name");
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
        EditText editText = findViewById(R.id.et_popup_rename_book_list_name);
        editText.setText(strName);
        editText.requestFocus();
        editText.setSelection(strName.length());
    }

    // ?????? ??????
    public void btnClose(View v) {
        finish();
    }

    // ???????????? ??????
    public void btnReport(View v) {
        String summary = ((EditText) findViewById(R.id.et_popup_bug_report_summary)).getText().toString();
        String step = ((EditText) findViewById(R.id.et_popup_bug_report_step)).getText().toString();

        Toast.makeText(this, "???????????? : " + summary + " / " + step, Toast.LENGTH_SHORT).show();
    }

    // ???????????? ?????? ??????
    public void btnAddBook(View v) {
        String name = ((EditText) findViewById(R.id.et_popup_add_book_list_name)).getText().toString();
        CreateReadingList(name);
    }

    // ???????????? ???????????? ??????
    public void btnRenameBook(View v) {
        String name = ((EditText) findViewById(R.id.et_popup_rename_book_list_name)).getText().toString();
        renameReadingList("" + readingId, name);
    }

    private void CreateReadingList(String readingName) {
        CommonUtils.showProgressDialog(mActivity, "????????? ??????????????????.");

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
                            Toast.makeText(mActivity, "???????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
                            setResult(RESULT_CANCELED);
                        }
                        finish();
                    }
                });
            }
        }).start();
    }

    private void renameReadingList(String strReadingId, String readingName) {
        CommonUtils.showProgressDialog(mActivity, "????????? ??????????????????.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bResult = HttpClient.renameReadingList(new OkHttpClient(), strReadingId, readingName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (bResult) {
                            Intent intent = new Intent();
                            intent.putExtra("name", readingName);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(mActivity, "???????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}