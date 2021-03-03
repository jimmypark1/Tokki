package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkRegActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.MarketVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MarketAddEditActivity extends AppCompatActivity {

    WorkVO work;
    TextView title;
    TextView synopsis;
    TextView date;

    ImageView cover;

    ImageView below100Ck;
    ImageView above100Ck;

    ImageView fieldDownBt;
    ImageView genreDownBt;

    EditText priceInput;
    int nField = -1;
    int nGenre = -1;
    LinearLayout priceFrame0;
    LinearLayout priceFrame1;
    LinearLayout preceAboveInfoFrame;
    TextView field;
    TextView carrotNum;
    TextView priceDetail;


    String strField = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_add_edit);

        title = findViewById(R.id.tv_row_literature_title);
        cover = findViewById(R.id.iv_row_literature_photo);

        date = findViewById(R.id.tv_row_literature_date);

        below100Ck = findViewById(R.id.price0_ck);
        above100Ck = findViewById(R.id.price1_ck);

        fieldDownBt = findViewById(R.id.fieldDown);
        genreDownBt = findViewById(R.id.genreDown);

        priceInput = findViewById(R.id.price_input);
        priceFrame0 = findViewById(R.id.price_frame_info);

        priceFrame1 = findViewById(R.id.price_info1_frame);
        preceAboveInfoFrame = findViewById(R.id.price_above_info_frame);

        field = findViewById(R.id.field);
        carrotNum = findViewById(R.id.price_info0);
        priceDetail= findViewById(R.id.price_info1);


        synopsis = findViewById(R.id.tv_row_literature_contents);
        work = (WorkVO)getIntent().getSerializableExtra("WORK");


        title.setText(work.getStrTitle());
        synopsis.setText(work.getSynopsis());
        date.setText(work.getCreatedDate());

        String strCover = CommonUtils.strDefaultUrl + "images/" + work.getCoverFile();


        Glide.with(this)
                .asBitmap() // some .jpeg files are actually gif
                .placeholder(R.drawable.no_poster)
                .load(strCover)
                .into(cover);


        preceAboveInfoFrame.setVisibility(View.INVISIBLE);

        below100Ck.setImageResource(R.drawable.i_radio_2);

        above100Ck.setImageResource(R.drawable.i_radio_1);

        priceInput.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)  {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("TAG","text = "+priceInput.getText().toString());

                int nCarrot = Integer.parseInt( priceInput.getText().toString() )/ 120;
                int nPrice = Integer.parseInt(priceInput.getText().toString());
                float div =  nPrice / 1000000.0f;
                if(div > 1)
                {
                    proceeAbove100();
                }
                else
                {
                    processBelow100();
                    carrotNum.setText(String.valueOf(nCarrot) +"개");
                    priceDetail.setText("수수료 : 20% ( 정산 시 " + String.format("%,d", (int)(0.8*nPrice)) + "원 )" );

                }

             //   String strPrice =  String.format("%,d", data.getPrice());
            //    viewHolder.price.setText(String.valueOf(nCarrot) + "개" +" (" + strPrice +"원)");

            }
        });

    }
    public void onClickTopLeftBtn(View view) {
        finish();
    }

    void getFields()
    {
        //getFieldForWork
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<String> ret = HttpClient.getFieldForWork(new OkHttpClient(),String.valueOf(work.getnWorkID()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }
        }).start();

    }
    void proceeAbove100()
    {
        below100Ck.setImageResource(R.drawable.i_radio_1);

        above100Ck.setImageResource(R.drawable.i_radio_2);

        priceFrame0.setVisibility(View.INVISIBLE);
        priceFrame1.setVisibility(View.INVISIBLE);

        preceAboveInfoFrame.setVisibility(View.VISIBLE);
    }

    public void onClickAbove100(View view) {


        proceeAbove100();

    }
    void processBelow100()
    {
        below100Ck.setImageResource(R.drawable.i_radio_2);

        above100Ck.setImageResource(R.drawable.i_radio_1);

        priceFrame0.setVisibility(View.VISIBLE);
        priceFrame1.setVisibility(View.VISIBLE);
        preceAboveInfoFrame.setVisibility(View.INVISIBLE);
    }
    public void onClickBelow100(View view) {

        //i_radio_2
        processBelow100();



    }
    public void onClickWantToField(View view) {
        PopupMenu popup = new PopupMenu(MarketAddEditActivity.this, fieldDownBt);

        popup.getMenuInflater().inflate(R.menu.market_field_menu, popup.getMenu());

/*
  <item
        android:id="@+id/drama"
        android:title="드라마"/>

    <item
        android:id="@+id/web_drama"
        android:title="웹드라마"/>

    <item
        android:id="@+id/screen"
        android:title="영화"/>
    <item
        android:id="@+id/publish"
        android:title="출판"/>
    <item
        android:id="@+id/comic"
        android:title="만화(웹툰)"/>
    <item
        android:id="@+id/animation"
        android:title="애니매이션"/>
    <item
        android:id="@+id/performance"
        android:title="공연"/>
    <item
        android:id="@+id/etc"
        android:title="기타"/>
 */
        new Thread(new Runnable() {
            @Override
            public void run() {


                ArrayList<String> ret = HttpClient.getFieldForWork(new OkHttpClient(),String.valueOf(work.getnWorkID()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        for(int i=0;i<ret.size();i++)
                        {
                            String field = ret.get(i);
                            if(field.equals("드라마"))
                            {
                                popup.getMenu().removeItem(R.id.drama);

                            }
                            else if(field.equals("웹드라마"))
                            {
                                popup.getMenu().removeItem(R.id.web_drama);

                            }

                            else if(field.equals("영화"))
                            {
                                popup.getMenu().removeItem(R.id.screen);

                            }
                            else if(field.equals("출판"))
                            {
                                popup.getMenu().removeItem(R.id.publish);

                            }
                            else if(field.equals("만화(웹툰)"))
                            {
                                popup.getMenu().removeItem(R.id.comic);

                            }
                            else if(field.equals("애니매이션"))
                            {
                                popup.getMenu().removeItem(R.id.animation);

                            }
                            else if(field.equals("공연"))
                            {
                                popup.getMenu().removeItem(R.id.performance);

                            }
                         }

                    }
                });
            }
        }).start();


         popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                // AlertDialog.Builder builder = new AlertDialog.Builder(AproveWaitingEpisodeListActivity.this);
                // AlertDialog alertDialog = null;

                switch(item.getItemId()) {
                    case R.id.drama:
                        nField = 0;
                        strField  = item.getTitle().toString();
                        field.setText(item.getTitle());
                        break;
                    case R.id.web_drama:
                        nField = 1;
                        strField  = item.getTitle().toString();
                        field.setText(item.getTitle());

                        break;
                    case R.id.screen:
                        nField = 2;
                        strField  = item.getTitle().toString();
                        field.setText(item.getTitle());


                        break;
                    case R.id.publish:
                        nField = 3;
                        strField  = item.getTitle().toString();
                        field.setText(item.getTitle());


                        break;
                    case R.id.comic:
                        nField = 4;
                        strField  = item.getTitle().toString();
                        field.setText(item.getTitle());


                        break;
                    case R.id.animation:
                        nField = 5;
                        field.setText(item.getTitle());


                        break;
                    case R.id.performance:
                        nField = 6;
                        strField  = item.getTitle().toString();
                        field.setText(item.getTitle());


                        break;
                    case R.id.etc:
                        nField = 7;
                        strField  = item.getTitle().toString();
                        field.setText(item.getTitle());


                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }
    //int nCarrot = data.getPrice() / 120;
    public void onClickWantToGenre(View view) {



    }
    public void onClickRegister(View view) {

//registerWorkOnMarket

        String price = priceInput.getText().toString();
        if(price.length() == 0) {
            Toast.makeText(MarketAddEditActivity.this, "가결을 입력하세요.", Toast.LENGTH_SHORT).show();

            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                MarketVO market = new MarketVO();
                market.setStatus(work.getStatus());
                market.setWorkId(String.valueOf(work.getnWorkID()));
                market.setCareer(work.getStrCareer());
                if(work.getnOwner() == 0)
                {
                    market.setCopyright0("본인소유");

                }
                else
                {
                    market.setCopyright0("타인소유");

                }
                if(work.getCopyright() == 0)
                {
                    market.setCopyright1("본인소유");

                }
                else
                {
                    market.setCopyright1("타인소유");

                }
                if(nField != -1)
                {
                    market.setStrField(field.getText().toString());

                }
                market.setPrice(Integer.parseInt(priceInput.getText().toString()));
                market.setField(nField);
                market.setCareer(work.getStrCareer());

                boolean ret = HttpClient.registerWorkOnMarket(new OkHttpClient(),market);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ret == true)
                        {
                            finish();
                        }
                        else
                        {
                            Toast.makeText(MarketAddEditActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        }).start();



    }



}