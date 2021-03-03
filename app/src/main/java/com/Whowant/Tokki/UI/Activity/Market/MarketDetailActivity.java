package com.Whowant.Tokki.UI.Activity.Market;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Fragment.Friend.MessageDetailActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.MarketVO;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;

import okhttp3.OkHttpClient;

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
    Button sendBt;

    TextView name;
    ImageView cover;
    MarketVO market;
    int mode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);

        cover = findViewById(R.id.coverImgView);
        title = findViewById(R.id.titleView0);
        sypnosis = findViewById(R.id.synopsisView);
        //   tag = findViewById(R.id.tag);
        //   career = findViewById(R.id.career);
        owner = findViewById(R.id.work_genre);
        copyright = findViewById(R.id.work_tag);

        owner1 = findViewById(R.id.owner);
        copyright1 = findViewById(R.id.copyright);
        career1 = findViewById(R.id.career1);

        name = findViewById(R.id.writer_name);

        status = findViewById(R.id.statusLabel0);
        field = findViewById(R.id.statusView);
        price = findViewById(R.id.priceView);
        sendBt = findViewById(R.id.send);

        String userId = SimplePreference.getStringPreference(MarketDetailActivity.this, "USER_INFO", "USER_ID", "Guest");


        market = (MarketVO) getIntent().getSerializableExtra("MARKET_DATA");
        if (market.getWriteId().contains(userId)) {
            sendBt.setVisibility(View.GONE);
        }
        int nStatus = market.getStatus();
        if (nStatus == 0) {
            status.setText("기획중");
        } else if (nStatus == 1) {
            status.setText("연재중");
        } else if (nStatus == 2) {
            status.setText("완결");

        }
        int nField = market.getField();
        if (nField == 0) {
            field.setText("영화");

        }
        /*
          carrotNum.text = String(Int( nPrice! / 120)) + "개"

         */
        long nPrice = market.getPrice();

        int nCarrot = (int) nPrice / 120;
        float div = (float) nPrice / 1000000f;
        if (div > 1) {
            sendBt.setText("거래요청");

            ImageView carrotView = findViewById(R.id.carrotView);
            ViewGroup.LayoutParams params = carrotView.getLayoutParams();
            params.width = 0;
            carrotView.setVisibility(View.GONE);
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String formattedPrice = formatter.format(market.getPrice());

            price.setText(formattedPrice + "원");

            TextView infoView = findViewById(R.id.messageInfo);
            infoView.setText("거래요청을 하시면 토키 관리자가 중개를 지원해드립니다");
            infoView.setTextColor(Color.parseColor("#767676"));

            mode = 1;

        } else {
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String formattedPrice = formatter.format(market.getPrice());
            price.setText(String.valueOf(nCarrot) + "개 (" + formattedPrice + "원)");

            TextView infoView = findViewById(R.id.messageInfo);
            infoView.setTextColor(Color.parseColor("#767676"));


        }


        name.setText(market.getName());
        title.setText(market.getTitle());
        sypnosis.setText(market.getSypnopsis());

        //   career.setText(market.getCareer());

        if (market.getGenre() != null && market.getGenre().length() > 0) {

            String strGenre = market.getGenre();
            strGenre = strGenre.replace(",", " / ");

            owner.setText(strGenre);
        } else
            owner.setText("");


        if (market.getTag() != null && market.getTag().length() > 0)
            copyright.setText(market.getTag());
        else
            copyright.setText("");

        career1.setText(market.getCareer());
        owner1.setText(market.getCopyright0());
        if (market.getCopyright1() != null && market.getCopyright1().length() > 0)
        {
            copyright1.setText("본인소유");

        }
        else
        {
            copyright1.setText("");

        }

        String strCover = CommonUtils.strDefaultUrl + "images/" + market.getCover();

        Glide.with(this)
                .asBitmap() // some .jpeg files are actually gif
                .placeholder(R.drawable.no_poster)
                .load(strCover)
                .into(cover);


        setViews();
    }

    void setViews()
    {


        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean ret = HttpClient.setViewsOnMarket(new OkHttpClient(),market.getWorkId());


            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }
    public void onClickSendMsgBtn(View view) {

        if(mode == 0)
        {
            CommonUtils.showProgressDialog(MarketDetailActivity.this, "서버와 통신중입니다.");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String userId = SimplePreference.getStringPreference(MarketDetailActivity.this, "USER_INFO", "USER_ID", "Guest");


                    final int oldThreadId =  HttpClient.getMarketMsgThreadId(new OkHttpClient(),userId,market.getWorkId());

                    if(oldThreadId == 0)
                    {
                        final int threadId = HttpClient.createRoomForWriterOnMarket(new OkHttpClient(),userId,market.getWriteId(),market.getWorkId());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.hideProgressDialog();
                                if(threadId == 0) {
                                    Toast.makeText(MarketDetailActivity.this, "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent intent = new Intent(MarketDetailActivity.this, MessageDetailActivity.class);
                                intent.putExtra("RECEIVER_ID", market.getWriteId());
                                intent.putExtra("RECEIVER_NAME", market.getName());
                                intent.putExtra("WRITER_ID", market.getWriterId());
                                intent.putExtra("WORK_TITLE",market.getTitle());

                                intent.putExtra("THREAD_ID", threadId);
                                intent.putExtra("MSG_TYPE", 1);
                                startActivity(intent);

                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.hideProgressDialog();

                                Intent intent = new Intent(MarketDetailActivity.this, MessageDetailActivity.class);
                                intent.putExtra("RECEIVER_ID", market.getWriteId());
                                intent.putExtra("RECEIVER_NAME", market.getName());
                                intent.putExtra("WRITER_ID", market.getWriterId());
                                intent.putExtra("WORK_TITLE",market.getTitle());

                                intent.putExtra("THREAD_ID", oldThreadId);
                                intent.putExtra("MSG_TYPE", 1);
                                startActivity(intent);

                            }
                        });
                    }

                }
            }).start();
        }
        else
        {
            Intent intent = new Intent(MarketDetailActivity.this, MarketDealPopup2.class);

            startActivity(intent);
        }




    }
    /*
       TokkiAPI.shared.createRoomForWriterOnMarket(userID: userId!, partnerId: partnerId!, workId: market.workId, completion: { [self](success,value) in
            if(success == true)
            {
                let json = JSON(value)
                let threadId =  json["THREAD_ID"].intValue
                performSegue(withIdentifier: "exec_market_message", sender: threadId)

            }
        })
     */

}