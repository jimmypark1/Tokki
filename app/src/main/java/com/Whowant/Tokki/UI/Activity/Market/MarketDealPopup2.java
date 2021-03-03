package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.Whowant.Tokki.R;

public class MarketDealPopup2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_deal_popup2);

        String desc ="토키 관리자에게 거래요청 메시지를 전송했습니다.\n매니저가 배정되어 계약에 필요한 중개를지원해 드립니다.\n빠른 시일 내 연락드리겠습니다.\n감사합니다.";

        TextView info = findViewById(R.id.desc);
        info.setText(desc);
    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickDoneBtn(View view) {


        finish();
    }
}