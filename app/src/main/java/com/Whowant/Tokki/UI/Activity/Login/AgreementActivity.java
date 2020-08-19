package com.Whowant.Tokki.UI.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Popup.CommonPopup;
import com.Whowant.Tokki.Utils.CustomUncaughtExceptionHandler;

public class AgreementActivity extends AppCompatActivity {
    private String strEmail;
    private String strSNSID;
    private String strNickName;
    private String strPhoto;
    private int    nSNS;

    private boolean bCheck1 = false;
    private boolean bCheck2 = false;
    private boolean bTotakCheck = false;

    private ImageView totalCheckbox, checkBtn1, checkBtn2;
    private LinearLayout totalAgreeLayout;
    private Button addBtn;
    private boolean bShowTemrs1, bShowTerms2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        Thread.UncaughtExceptionHandler handler = Thread
                .getDefaultUncaughtExceptionHandler();

        if (!(handler instanceof CustomUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        }

        bShowTemrs1 = false;
        bShowTerms2 = false;

        strEmail = getIntent().getStringExtra("USER_EMAIL");
        strSNSID = getIntent().getStringExtra("SNS_ID");
        strNickName = getIntent().getStringExtra("USER_NAME");
        strPhoto = getIntent().getStringExtra("USER_PHOTO");
        nSNS = getIntent().getIntExtra("SNS", 0);
        addBtn = findViewById(R.id.addBtn);
        totalAgreeLayout = findViewById(R.id.totalAgreeLayout);
        totalCheckbox = findViewById(R.id.totalCheckbox);
        checkBtn1 = findViewById(R.id.checkBtn1);
        checkBtn2 = findViewById(R.id.checkBtn2);

        /*
        intent.putExtra("USER_EMAIL", strEmail);
                intent.putExtra("SNS_ID", strSNSID);
                intent.putExtra("USER_NAME", strNickName);
                intent.putExtra("USER_PHOTO", strPhoto);
         */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        KakaoSDKAdapter.unregisterKakaoTalk(AgreementActivity.this);
    }

    public void onClickTotakCheckBtn(View view) {
        bTotakCheck = !bTotakCheck;

        if(bTotakCheck) {
            bCheck1 = true;
            bCheck2 = true;
            initViews();
        } else {
            bCheck1 = false;
            bCheck2 = false;
            initViews();
        }
    }

    private void initViews() {
        totalAgreeLayout.setBackgroundColor(Color.parseColor("#e8e8e8"));
        totalCheckbox.setImageResource(0);
        checkBtn1.setImageResource(0);
        checkBtn2.setImageResource(0);
        addBtn.setBackgroundColor(Color.parseColor("#e8e8e8"));
        addBtn.setTextColor(Color.parseColor("#969696"));

        if(bCheck1) {
            checkBtn1.setImageResource(R.drawable.check_box_on);
        }

        if(bCheck2) {
            checkBtn2.setImageResource(R.drawable.check_box_on);
        }

        if(bCheck1 && bCheck2) {
            bTotakCheck = true;
            totalAgreeLayout.setBackgroundColor(ContextCompat.getColor(AgreementActivity.this, R.color.colorPrimary));
            totalCheckbox.setImageResource(R.drawable.all_chek_box);
            addBtn.setBackgroundColor(ContextCompat.getColor(AgreementActivity.this, R.color.colorPrimary));
            addBtn.setTextColor(Color.parseColor("#ffffff"));
        } else {
            bTotakCheck = false;
            totalAgreeLayout.setBackgroundColor(Color.parseColor("#e8e8e8"));
            totalCheckbox.setImageResource(0);
            addBtn.setBackgroundColor(Color.parseColor("#e8e8e8"));
            addBtn.setTextColor(Color.parseColor("#969696"));
        }
    }

    public void onClickPopBtn(View view) {
        Intent intent = new Intent(AgreementActivity.this, CommonPopup.class);
        intent.putExtra("TITLE", "개인정보 수집 정책");
        intent.putExtra("CONTENTS", "약관이 들어갈 곳입니다.\n약관 약관 약관 \n 약관 약관 약관 약관 약관 약관 약관 약관 약관 약관 약관약관");
        intent.putExtra("TWOBTN", false);
        startActivity(intent);
    }

    public void onClickCheck2(View view) {
        bCheck2 = !bCheck2;

        if(bCheck2) {
            Intent intent = new Intent(AgreementActivity.this, TermsActivity.class);
            intent.putExtra("TERMS_TYPE", 0);
            startActivityForResult(intent, 2000);
        }

        initViews();
    }

    public void onClickCheck1(View view) {                  // 개인정보 취급방침
        bCheck1 = !bCheck1;

        if(bCheck1) {
            Intent intent = new Intent(AgreementActivity.this, TermsActivity.class);
            intent.putExtra("TERMS_TYPE", 1);
            startActivityForResult(intent, 2000);
        }

        initViews();
    }

    public void onClickRegisterBtn(View view) {
        if(!bCheck1) {
            Toast.makeText(this, "개인정보 수집정책에 동의해주셔야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!bCheck2) {
            Toast.makeText(this, "서비스 이용약관에 동의해주셔야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(AgreementActivity.this, PersonalInfoActivity.class);
        intent.putExtra("SNS", nSNS);
        intent.putExtra("SNS_ID", strSNSID);
        intent.putExtra("USER_NAME", strNickName);
        intent.putExtra("USER_EMAIL", strEmail);
        intent.putExtra("USER_PHOTO", strPhoto);
        startActivity(intent);
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }
}
