package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class GenreSelectActivity extends AppCompatActivity {
    private ArrayList<String> genreList;
    private TextView genreView;
    private ListView genreListView;
    private TextView okBtn;
    private boolean[] bSelectedArray;
    private CGenreArrayAdapter aa;
    private String strCurrentGenre = "";
    private int nSelectedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_select);

        genreView = findViewById(R.id.genreView);
        genreListView = findViewById(R.id.genreListView);
        okBtn = findViewById(R.id.okBtn);

        strCurrentGenre = getIntent().getStringExtra("GENRE");

        getGenreList();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    private void getGenreList() {
        genreList = new ArrayList<>();

        CommonUtils.showProgressDialog(GenreSelectActivity.this, "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                genreList = HttpClient.getAllGenreList(new OkHttpClient());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(genreList == null) {
                            Toast.makeText(GenreSelectActivity.this, "장르 목록을 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        bSelectedArray = new boolean[genreList.size()];
                        for(int i = 0 ; i < bSelectedArray.length ; i++) {
                            bSelectedArray[i] = false;
                        }

                        if(strCurrentGenre.length() > 0) {
                            String[] genreArr = strCurrentGenre.split(" ");

                            for(int i = 0 ; i < genreArr.length ; i++) {
                                String strGenre = genreArr[i];

                                for(int j = 0 ; j < genreList.size() ; j++) {
                                    String genre = genreList.get(j);

                                    if(strGenre.equals(genre)) {
                                        bSelectedArray[j] = true;
                                        nSelectedCount ++;
                                    }
                                }
                            }
                        }

                        genreView.setText(strCurrentGenre);
                        aa = new CGenreArrayAdapter(GenreSelectActivity.this, R.layout.genre_row, genreList);
                        genreListView.setAdapter(aa);
                        genreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                boolean bSelected = bSelectedArray[position];

                                if(!bSelected) {
                                    if(nSelectedCount < 3) {
                                        bSelected = true;
                                        nSelectedCount ++;

                                        if(strCurrentGenre.length() > 0)
                                            strCurrentGenre += " / ";

                                        strCurrentGenre += genreList.get(position);
                                        bSelectedArray[position] = bSelected;
                                    }
                                } else {
                                    bSelected = false;
                                    nSelectedCount --;

                                    String[] genreArr = strCurrentGenre.split(" / ");
                                    String strNewGenre = "";
                                    for(String genre : genreArr) {
                                        if(!genre.equals(genreList.get(position))) {
                                            if(strNewGenre.length() > 0)
                                                strNewGenre += " / ";
                                            strNewGenre += genre;
                                        }
                                    }

                                    strCurrentGenre = strNewGenre;
                                    bSelectedArray[position] = bSelected;
                                }

                                if(strCurrentGenre.length() > 0) {
                                    okBtn.setBackgroundColor(ContextCompat.getColor(GenreSelectActivity.this, R.color.colorPrimary));
                                    okBtn.setTextColor(ContextCompat.getColor(GenreSelectActivity.this, R.color.colorWhite));
                                    okBtn.setEnabled(true);
                                } else {
                                    okBtn.setBackgroundColor(Color.parseColor("#e8e8e8"));
                                    okBtn.setTextColor(Color.parseColor("#969696"));
                                    okBtn.setEnabled(false);
                                }

                                genreView.setText(strCurrentGenre);
                                aa.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    public class CGenreArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CGenreArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.genre_row, parent, false);

            RelativeLayout bgView = convertView.findViewById(R.id.bgView);
            TextView genreNameView = convertView.findViewById(R.id.genreNameView);
            ImageView iconView = convertView.findViewById(R.id.iconView);

            String strGenre = genreList.get(position);
            boolean bSelected = bSelectedArray[position];

            if(bSelected) {
                bgView.setBackgroundResource(R.drawable.genre_selected_bg);
                genreNameView.setTextColor(ContextCompat.getColor(GenreSelectActivity.this, R.color.colorPrimary));
                iconView.setBackgroundResource(R.drawable.check);
            } else {
                bgView.setBackgroundResource(R.drawable.genre_gray_bg);
                genreNameView.setTextColor(Color.parseColor("#969696"));
                iconView.setBackgroundResource(R.drawable.plus_button);
            }

            genreNameView.setText(strGenre);
            return convertView;
        }
    }

    public void OnClickOkBtn(View view) {
        Intent oldIntent = getIntent();
        oldIntent.putExtra("GENRE", strCurrentGenre);
        setResult(RESULT_OK, oldIntent);
        finish();
    }
}