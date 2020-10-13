package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.Whowant.Tokki.R;

public class LegalNoticePopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_notice_popup);

        String str = "* 작품을 작성하시기 전 유의사항입니다.";
        TextView textview = (TextView)findViewById(R.id.legalNoticeText);
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#ff5700")), 14, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(ssb);
    }

    public void onClickCloseBtn(View view) {
        finish();
    }
}