package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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


        below100Ck.setImageResource(R.drawable.i_radio_2);

        above100Ck.setImageResource(R.drawable.i_radio_1);

    }
    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public void onClickAbove100(View view) {

        below100Ck.setImageResource(R.drawable.i_radio_1);

        above100Ck.setImageResource(R.drawable.i_radio_2);
    }
    public void onClickBelow100(View view) {

        //i_radio_2
        below100Ck.setImageResource(R.drawable.i_radio_2);

        above100Ck.setImageResource(R.drawable.i_radio_1);


    }
    public void onClickWantToField(View view) {
        PopupMenu popup = new PopupMenu(MarketAddEditActivity.this, fieldDownBt);

        popup.getMenuInflater().inflate(R.menu.market_field_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                // AlertDialog.Builder builder = new AlertDialog.Builder(AproveWaitingEpisodeListActivity.this);
                // AlertDialog alertDialog = null;

                switch(item.getItemId()) {
                    case R.id.drama:
                        nField = 0;
                        break;
                    case R.id.web_drama:
                        nField = 1;
                        break;
                    case R.id.screen:
                        nField = 2;

                        break;
                    case R.id.publish:
                        nField = 3;

                        break;
                    case R.id.comic:
                        nField = 4;

                        break;
                    case R.id.animation:
                        nField = 5;

                        break;
                    case R.id.performance:
                        nField = 6;

                        break;
                    case R.id.etc:
                        nField = 7;

                        break;
                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }
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
                market.setCopyright0(String.valueOf( work.getnOwner()));
                market.setPrice(Integer.parseInt(priceInput.getText().toString()));
                market.setField(nField);

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