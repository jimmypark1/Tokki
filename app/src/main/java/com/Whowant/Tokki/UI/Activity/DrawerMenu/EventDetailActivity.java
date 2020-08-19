package com.Whowant.Tokki.UI.Activity.DrawerMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;
import com.bumptech.glide.Glide;

public class EventDetailActivity extends AppCompatActivity {
    String strEventTitle;
    String strEventFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        strEventTitle = getIntent().getStringExtra("EVENT_TITLE");
        strEventFile = getIntent().getStringExtra("EVENT_FILE");

        ImageView eventImgView = findViewById(R.id.eventImgView);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        TextView titleView = findViewById(R.id.titleView);
        titleView.setText(strEventTitle);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle(strEventTitle);

        if(strEventFile != null && strEventFile.length() > 0 && !strEventFile.equals("null")) {
            if(!strEventFile.startsWith("http"))
                strEventFile = CommonUtils.strDefaultUrl + "images/" + strEventFile;

            Glide.with(EventDetailActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strEventFile)
                    .into(eventImgView);
        }
    }

    public void onClickTopLeftBtn(View view) {
        finish();
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
