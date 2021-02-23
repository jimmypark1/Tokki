package com.Whowant.Tokki.UI.Activity.Market;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.MarketVO;
import com.bumptech.glide.Glide;

public class MarketDetailActivity extends AppCompatActivity {

    private TextView title;
    private TextView sypnosis;
    private TextView tag;
    private TextView career;
    private TextView owner;
    private TextView copyright;

    private TextView owner1;
    private TextView copyright1;
    private TextView career1;
    private TextView status;

    private TextView field;
    private TextView price;

    ImageView cover;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);

        cover = findViewById(R.id.coverImgView);
        title = findViewById(R.id.titleView0);
        sypnosis = findViewById(R.id.synopsisView);
        tag = findViewById(R.id.tag);
        career = findViewById(R.id.career);
        owner = findViewById(R.id.copyright0);
        copyright = findViewById(R.id.copyright0);

        owner1 = findViewById(R.id.owner);
        copyright1 = findViewById(R.id.copyright);
        career1 = findViewById(R.id.career1);

        status = findViewById(R.id.statusLabel0);
        field = findViewById(R.id.statusView);
        price = findViewById(R.id.priceView);

        //

        MarketVO market = (MarketVO)getIntent().getSerializableExtra("MARKET_DATA");

        int nStatus = market.getStatus();
        if(nStatus==0)
        {
            status.setText("기획중");
        }
        else if(nStatus==1)
        {
            status.setText("연재중");
        }
        else if(nStatus==2)
        {
            status.setText("완결");

        }
        int nField = market.getField();
        if(nField == 0)
        {
            field.setText("영화");

        }
        price.setText(String.valueOf(market.getPrice()));

        
        title.setText(market.getTitle());
        sypnosis.setText(market.getSypnopsis());
        tag.setText(market.getTag());
        career.setText(market.getCareer());
        owner.setText("저작권 :" +market.getCopyright0());
        copyright.setText("판권 :" +market.getCopyright1());

        career1.setText(market.getCareer());
        owner1.setText(market.getCopyright0());
        copyright1.setText(market.getCopyright1());

        String strCover = CommonUtils.strDefaultUrl + "images/" + market.getCover();

        Glide.with(this)
                .asBitmap() // some .jpeg files are actually gif
                .placeholder(R.drawable.no_poster)
                .load(strCover)
                .into(cover);


    }
    public void onClickTopLeftBtn(View view) {
        finish();
    }

}