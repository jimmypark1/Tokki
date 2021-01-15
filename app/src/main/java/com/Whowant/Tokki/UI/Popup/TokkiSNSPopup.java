package com.Whowant.Tokki.UI.Popup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.Whowant.Tokki.R;

public class TokkiSNSPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokki_sns_popup);
    }

    public void onClickCloseBtn(View view) {
        finish();
        overridePendingTransition(R.anim.cross_fade_in, R.anim.cross_fade_out);
    }

    public void onClickFacebookBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tokkinovel"));
        startActivity(intent);
    }

    public void onClickInstaBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.instagram.com/tokkinovel"));
        startActivity(intent);
    }

    public void onClickNaverBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.naver.com/whowant1901"));
        startActivity(intent);
    }

    public void onClickYoutubeBtn(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCcfdSM-j2cMWb5rpZXcFMvA"));
        startActivity(intent);
    }
}