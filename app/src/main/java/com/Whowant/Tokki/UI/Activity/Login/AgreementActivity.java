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
    private String strID;
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

        strID = getIntent().getStringExtra("ID");
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

        bCheck1 = true;
        bCheck2 = true;
        initViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
       // totalAgreeLayout.setBackgroundColor(Color.parseColor("#e8e8e8"));
       // totalCheckbox.setImageResource(0);
       // checkBtn1.setImageResource(0);
       // checkBtn2.setImageResource(0);
//        addBtn.setBackgroundColor(Color.parseColor("#e8e8e8"));
//        addBtn.setTextColor(Color.parseColor("#969696"));

        addBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
        addBtn.setTextColor(Color.parseColor("#999999"));

        if(bCheck1 && bCheck2 == false) {
            totalCheckbox.setImageResource(R.drawable.i_chck_circle_1);

            checkBtn1.setImageResource(R.drawable.i_chck_circle_2);
            checkBtn2.setImageResource(R.drawable.i_chck_circle_1);

        }

        else if(bCheck2 && bCheck1 == false) {
            totalCheckbox.setImageResource(R.drawable.i_chck_circle_1);

            checkBtn1.setImageResource(R.drawable.i_chck_circle_1);
            checkBtn2.setImageResource(R.drawable.i_chck_circle_2);
        }

        else if(bCheck1 && bCheck2) {
            bTotakCheck = true;
          //  totalAgreeLayout.setBackgroundColor(ContextCompat.getColor(AgreementActivity.this, R.color.colorPrimary));
            totalCheckbox.setImageResource(R.drawable.i_chck_circle_2);
            checkBtn1.setImageResource(R.drawable.i_chck_circle_2);

            checkBtn2.setImageResource(R.drawable.i_chck_circle_2);

            //    addBtn.setBackgroundColor(ContextCompat.getColor(AgreementActivity.this, R.color.colorPrimary));
        //    addBtn.setTextColor(Color.parseColor("#ffffff"));

            addBtn.setBackgroundResource(R.drawable.common_btn_bg);
            addBtn.setTextColor(Color.parseColor("#ffffff"));

         } else {
            bTotakCheck = false;
            checkBtn1.setImageResource(R.drawable.i_chck_circle_1);

            checkBtn2.setImageResource(R.drawable.i_chck_circle_1);

            //  totalAgreeLayout.setBackgroundColor(Color.parseColor("#e8e8e8"));
            totalCheckbox.setImageResource(R.drawable.i_chck_circle_1);
         //   addBtn.setBackgroundColor(Color.parseColor("#e8e8e8"));
         //   addBtn.setTextColor(Color.parseColor("#969696"));
            addBtn.setBackgroundResource(R.drawable.common_btn_disable_bg);
            addBtn.setTextColor(Color.parseColor("#999999"));

        }


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

        Intent intent = new Intent(AgreementActivity.this, PWRegisterActivity.class);                 // 약관 동의시 개인정보 입력화면으로 이동
        intent.putExtra("ID", strID);
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
