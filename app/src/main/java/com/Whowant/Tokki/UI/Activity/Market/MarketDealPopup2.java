package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Fragment.Friend.MessageDetailActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.MarketVO;

import okhttp3.OkHttpClient;

public class MarketDealPopup2 extends AppCompatActivity {

    MarketVO market;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_deal_popup2);

        market = (MarketVO)getIntent().getSerializableExtra("MARKET_DATA");

        String desc ="토키 관리자에게 거래요청 메시지를 전송했습니다.\n매니저가 배정되어 계약에 필요한 중개를지원해 드립니다.\n빠른 시일 내 연락드리겠습니다.\n감사합니다.";

        TextView info = findViewById(R.id.desc);
        info.setText(desc);
    }

    void postProcess()
    {
        String userId = SimplePreference.getStringPreference(MarketDealPopup2.this, "USER_INFO", "USER_ID", "Guest");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean ret = HttpClient.sendEmailToAdmin(new OkHttpClient(), String.valueOf(market.getMarketId()),userId ,market.getWorkId());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ret == false) {
                            Toast.makeText(MarketDealPopup2.this, "거래요청 메시지를 보내는데 실패했습니다.잠시후 이용해 주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        finish();

                    }
                });
            }
        }).start();
    }
    public void onClickCloseBtn(View view) {

        finish();
    }

    public void onClickDoneBtn(View view) {
        postProcess();

    }
}