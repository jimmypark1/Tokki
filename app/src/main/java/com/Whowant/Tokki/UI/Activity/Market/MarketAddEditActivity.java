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
import com.Whowant.Tokki.UI.Activity.Photopicker.PhotoPickerActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkEditActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkRegActivity;
import com.Whowant.Tokki.UI.Popup.MediaSelectPopup;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.MarketVO;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MarketAddEditActivity extends AppCompatActivity {

    WorkVO work;
    TextView title;
    TextView synopsis;
//    TextView date;

    TextView name;
    TextView infoGenre;
    TextView infoTag;

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
    TextView genre;
    TextView tag;

    TextView carrotNum;
    TextView priceDetail;


    String strField = "";
    String strGenre = "";
    String strTag = "";

    String tags = "";
    String genres = "";
    private ArrayList<String> tagList, genreList;

    void getTagData() {
        tagList = new ArrayList<>();
        genreList = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject resultObject = HttpClient.getWorkTagWithID(new OkHttpClient(), "" + work.getnWorkID());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultObject == null) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(MarketAddEditActivity.this, "?????? ????????? ??????????????? ??????????????????.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            JSONArray tagArray = resultObject.getJSONArray("TAG_LIST");

                            for (int i = 0; i < tagArray.length(); i++) {
                                JSONObject object = tagArray.getJSONObject(i);
                               // tagList.add(object.getString("TAG_TITLE"));
                                tags =tags + object.getString("TAG_TITLE");
                            }

                            JSONArray genreArray = resultObject.getJSONArray("GENRE_LIST");
                            for (int i = 0; i < genreArray.length(); i++) {
                                JSONObject object = genreArray.getJSONObject(i);
                               // genreList.add(object.getString("GENRE_NAME"));
                            }

                            String strGenre = "";
                            for ( String genre : genreList) {
                                if (strGenre.length() > 0)
                                    strGenre += " / ";

                                strGenre += genre;
                            }
//                            initViews();
                            infoGenre.setText(strGenre);

                            String strTags = "";
                            for ( String tag : tagList) {
                                if (strTags.length() > 0)
                                    strTags += " ";
                                strTags += tag;
                            }

                            infoTag.setText(strTags);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_add_edit);

        title = findViewById(R.id.tv_row_literature_title);
        cover = findViewById(R.id.iv_row_literature_photo);

       // date = findViewById(R.id.tv_row_literature_date);

        below100Ck = findViewById(R.id.price0_ck);
        above100Ck = findViewById(R.id.price1_ck);

        fieldDownBt = findViewById(R.id.fieldDown);
        genreDownBt = findViewById(R.id.genreDown);

        priceInput = findViewById(R.id.price_input);
        priceFrame0 = findViewById(R.id.price_frame_info);

        priceFrame1 = findViewById(R.id.price_info1_frame);
        preceAboveInfoFrame = findViewById(R.id.price_above_info_frame);

        field = findViewById(R.id.field);
        genre = findViewById(R.id.genre);
        carrotNum = findViewById(R.id.price_info0);
        priceDetail= findViewById(R.id.price_info1);
        tag = findViewById(R.id.tag);

        name = findViewById(R.id.name);
        infoGenre = findViewById(R.id.info_genre);
        infoTag = findViewById(R.id.info_tag);


        synopsis = findViewById(R.id.tv_row_literature_contents);
        work = (WorkVO)getIntent().getSerializableExtra("WORK");

        priceFrame0.setVisibility(View.INVISIBLE);
        priceFrame1.setVisibility(View.INVISIBLE);

        preceAboveInfoFrame.setVisibility(View.INVISIBLE);
        getTagData();
        title.setText(work.getStrTitle());
        synopsis.setText(work.getSynopsis());
        //date.setText(work.getCreatedDate());
        String strType = "";
        int nType = work.getnTarget();
        if(nType == 0)
        {
            strType = "????????????";
        }
        else if(nType == 1)
        {
            strType = "?????????";

        }
        else if(nType == 3)
        {
            strType = "?????????";

        }
        String strName = work.getStrWriterName();

        name.setText(strName + " | " + strType);
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

                Double nCarrot = Double.parseDouble( priceInput.getText().toString() )/ 120;
                Double nPrice = Double.parseDouble(priceInput.getText().toString());
                double div =  nPrice / 1000000;
                if(div > 1)
                {
                    proceeAbove100();
                }
                else
                {
                    processBelow100();
                    carrotNum.setText(String.valueOf(nCarrot) +"???");
                    priceDetail.setText("????????? : 20% ( ?????? ??? " + String.format("%,d", (int)(0.8*nPrice)) + "??? )" );

                }

             //   String strPrice =  String.format("%,d", data.getPrice());
            //    viewHolder.price.setText(String.valueOf(nCarrot) + "???" +" (" + strPrice +"???)");

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

        Double nCarrot = Double.parseDouble( priceInput.getText().toString() )/ 120;
        Double nPrice = Double.parseDouble(priceInput.getText().toString());
        double div =  nPrice / 1000000;
        if(div > 1)
        {
            Toast.makeText(MarketAddEditActivity.this, "100?????? ?????? ????????? ??????????????? ???????????????", Toast.LENGTH_SHORT).show();

        }
        else
        {
            below100Ck.setImageResource(R.drawable.i_radio_2);

            above100Ck.setImageResource(R.drawable.i_radio_1);

            priceFrame0.setVisibility(View.VISIBLE);
            priceFrame1.setVisibility(View.VISIBLE);
            preceAboveInfoFrame.setVisibility(View.INVISIBLE);
        }


    }
    public void onClickBelow100(View view) {

        //i_radio_2
        processBelow100();



    }
    public void onClickWantToField(View view) {
        PopupMenu popup = new PopupMenu(MarketAddEditActivity.this, fieldDownBt);

        popup.getMenuInflater().inflate(R.menu.market_field_menu, popup.getMenu());


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
                            if(field.equals("?????????"))
                            {
                                popup.getMenu().removeItem(R.id.drama);

                            }
                            else if(field.equals("????????????"))
                            {
                                popup.getMenu().removeItem(R.id.web_drama);

                            }

                            else if(field.equals("??????"))
                            {
                                popup.getMenu().removeItem(R.id.screen);

                            }
                            else if(field.equals("??????"))
                            {
                                popup.getMenu().removeItem(R.id.publish);

                            }
                            else if(field.equals("??????"))
                            {
                                popup.getMenu().removeItem(R.id.comic);

                            }
                            else if(field.equals("???????????????"))
                            {
                                popup.getMenu().removeItem(R.id.animation);

                            }
                            else if(field.equals("??????"))
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

                   }
                return true;
            }
        });

        popup.show();//showing popup menu

    }
    //int nCarrot = data.getPrice() / 120;
    public void onClickWantToGenre(View view) {

        PopupMenu popup = new PopupMenu(MarketAddEditActivity.this, genreDownBt);

        popup.getMenuInflater().inflate(R.menu.market_genre_menu, popup.getMenu());


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
                            if(field.equals("??????"))
                            {
                                popup.getMenu().removeItem(R.id.drama);

                            }
                            else if(field.equals("?????????"))
                            {
                                popup.getMenu().removeItem(R.id.web_drama);

                            }

                            else if(field.equals("??????"))
                            {
                                popup.getMenu().removeItem(R.id.screen);

                            }
                            else if(field.equals("??????"))
                            {
                                popup.getMenu().removeItem(R.id.publish);

                            }
                            else if(field.equals("BL"))
                            {
                                popup.getMenu().removeItem(R.id.comic);

                            }
                            else if(field.equals("???????????????"))
                            {
                                popup.getMenu().removeItem(R.id.animation);

                            }
                            else if(field.equals("??????"))
                            {
                                popup.getMenu().removeItem(R.id.performance);

                            }
                        }

                    }
                });
            }
        }).start();

/*
 <item
        android:id="@+id/horror"
        android:title="??????"/>

    <item
        android:id="@+id/romance"
        android:title="?????????"/>

    <item
        android:id="@+id/action"
        android:title="??????"/>
    <item
        android:id="@+id/fanfic"
        android:title="??????"/>
    <item
        android:id="@+id/bl"
        android:title="BL"/>

 */
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                // AlertDialog.Builder builder = new AlertDialog.Builder(AproveWaitingEpisodeListActivity.this);
                // AlertDialog alertDialog = null;

                switch(item.getItemId()) {
                    case R.id.horror:
                        nGenre = 0;
                        strGenre  = item.getTitle().toString();
                        genre.setText(item.getTitle());
                        break;
                    case R.id.romance:
                        nGenre = 1;
                        strGenre  = item.getTitle().toString();
                        genre.setText(item.getTitle());

                        break;
                    case R.id.action:
                        nGenre = 2;
                        strGenre  = item.getTitle().toString();
                        genre.setText(item.getTitle());


                        break;
                    case R.id.fanfic:
                        nGenre = 3;
                        strGenre  = item.getTitle().toString();
                        genre.setText(item.getTitle());


                        break;
                    case R.id.bl:
                        nGenre = 4;
                        strGenre  = item.getTitle().toString();
                        genre.setText(item.getTitle());


                        break;


                }
                return true;
            }
        });

        popup.show();//showing popup menu

    }
    public void onClickWantToTag(View view) {

        PopupMenu popup = new PopupMenu(MarketAddEditActivity.this, genreDownBt);

      //  popup.getMenuInflater().inflate(R.menu.market_genre_menu, popup.getMenu());
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> datas = HttpClient.getMarketTagRank(new OkHttpClient());

                for(int i=0;i<datas.size();i++)
                {
                    String tag = datas.get(i);
                    popup.getMenu().add(tag);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        popup.show();//showing popup menu

                    }
                });


            }
         }).start();

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                strTag  = item.getTitle().toString();
                tag.setText(item.getTitle());

                return true;
            }
        });


    }
    public void onClickRegister(View view) {

//registerWorkOnMarket

        String price = priceInput.getText().toString();
        if(price.length() == 0  || field.getText().toString().length() == 0 || strGenre.length() == 0 || strTag.length() == 0) {
            Toast.makeText(MarketAddEditActivity.this, "?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();

            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                MarketVO market = new MarketVO();
                market.setStatus(work.getStatus());
                market.setWorkId(String.valueOf(work.getnWorkID()));
                market.setCareer(work.getStrCareer());
            //    market.setStrTag(work.());
                if(work.getnOwner() == 0)
                {
                    market.setCopyright0("????????????");

                }
                else
                {
                    market.setCopyright0("????????????");

                }
                if(work.getCopyright() == 0)
                {
                    market.setCopyright1("????????????");

                }
                else
                {
                    market.setCopyright1("????????????");

                }
                if(nField != -1)
                {
                    market.setStrField(field.getText().toString());

                }
                market.setPrice(Integer.parseInt(priceInput.getText().toString()));
                market.setField(nField);
                market.setCareer(work.getStrCareer());
                market.setStrGenre(strGenre);
                market.setStrTag(strTag);

                boolean ret = HttpClient.registerWorkOnMarket(new OkHttpClient(),market);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        if(ret == true)
                        {
                       //     finish();
                            Intent intent = new Intent(MarketAddEditActivity.this, MarketAddActivity.class);
                           // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            setResult(1111,intent);
                            finish();
                            //startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MarketAddEditActivity.this, "???????????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();

                        }

                         */
                        Intent intent = new Intent(MarketAddEditActivity.this, MarketAddCompleteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(intent);
                        finish();

                    }
                });
            }
        }).start();



    }



}