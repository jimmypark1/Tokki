package com.Whowant.Tokki.UI.Activity.Market;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.R;

public class MarketAddCompleteActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_add_complete);


    }
    public void onClickTopLeftBtn(View view) {

        finish();
    }
    public void onClickMoveToMarket(View view) {

        Intent intent = new Intent(MarketAddCompleteActivity.this, MarketMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        finish();
    }

}