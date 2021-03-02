package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.Whowant.Tokki.R;

public class MarketDealPopup extends AppCompatActivity {

    EditText carrot;
    private Intent oldIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_deal_popup);

        oldIntent = getIntent();

        carrot = findViewById(R.id.carrot);

    }

    public void onClickCloseBtn(View view) {
        finish();
    }

    public void onClickDoneBtn(View view) {



        oldIntent.putExtra("CARROT",carrot.getText().toString());
        setResult(RESULT_OK, oldIntent);
        finish();
    }

}