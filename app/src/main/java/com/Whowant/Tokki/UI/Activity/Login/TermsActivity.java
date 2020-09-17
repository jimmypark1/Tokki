package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.Whowant.Tokki.R;

public class TermsActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private WebView webView;
    private final int SERVICE_TERMS = 0;
    private final int PERSONAL_TERMS = 1;
    private int nType = SERVICE_TERMS;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        scrollView = findViewById(R.id.scrollView);
        webView = findViewById(R.id.webView);
        TextView titleView = findViewById(R.id.titleView);
        nextBtn = findViewById(R.id.nextBtn);

        nType = getIntent().getIntExtra("TERMS_TYPE", SERVICE_TERMS);

        if(nType == PERSONAL_TERMS) {
            titleView.setText("개인정보 수집 정책");
            scrollView.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl("https://sites.google.com/view/tokki-app-privacypolicy");

        } else {
            titleView.setText("서비스 이용약관");
        }

        boolean bMain = getIntent().getBooleanExtra("MAIN", false);
        if(bMain) {
            nextBtn.setText("확인");
        }
    }

    public void onClickBackBtn(View view) {
        finish();
    }
}