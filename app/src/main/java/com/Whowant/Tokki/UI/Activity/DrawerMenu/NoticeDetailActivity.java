package com.Whowant.Tokki.UI.Activity.DrawerMenu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;

public class NoticeDetailActivity extends AppCompatActivity {
    private String strNoticeTitle;
    private String strNoticeContents;
    private String strDate;
    private String strUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("공지사항");

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        strNoticeContents = getIntent().getStringExtra("NOTICE_CONTENTS");
        strNoticeTitle = getIntent().getStringExtra("NOTICE_TITLE");
        strDate = getIntent().getStringExtra("DATE");
        strUserName = getIntent().getStringExtra("USER_NAME");

        TextView noticeTitleView = findViewById(R.id.noticeTitleView);
        TextView noticeContentsView = findViewById(R.id.noticeContentsView);
        TextView dateView = findViewById(R.id.dateView);
        TextView userNameView = findViewById(R.id.userNameView);

        noticeTitleView.setText(strNoticeTitle);
        noticeContentsView.setText(strNoticeContents);
        dateView.setText(strDate);
        userNameView.setText(strUserName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
