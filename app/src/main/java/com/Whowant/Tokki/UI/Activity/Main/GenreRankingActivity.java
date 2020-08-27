package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.DrawerMenu.NoticeActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;

public class GenreRankingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<WorkVO> bestList;
    private ArrayList<HashMap<String, String>> genreList;

    private ListView listView;
    private CGenreRankingArrayAdapter aa;

    private LinearLayout tabLayout;
    private ArrayList<View> tabViewList;
    private int nSelectedGenreIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_ranking);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("인기작");

        ImageButton leftBtn = findViewById(R.id.leftBtn);
        ImageButton rightBtn = findViewById(R.id.rightBtn);
        leftBtn.setImageResource(R.drawable.back_button);
        rightBtn.setVisibility(View.INVISIBLE);
        ImageView cenverLogoView = findViewById(R.id.cenverLogoView);
        cenverLogoView.setVisibility(View.INVISIBLE);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText("인기작");

        bestList = new ArrayList<>();
        tabViewList = new ArrayList<>();
        listView = findViewById(R.id.listView);
        tabLayout = findViewById(R.id.tabLayout);

        listView.setOnItemClickListener(this);

        getGenreList();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        WorkVO vo = bestList.get(position);
        Intent intent = new Intent(GenreRankingActivity.this, WorkMainActivity.class);
        intent.putExtra("WORK_ID", vo.getnWorkID());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getGenreList() {
        CommonUtils.showProgressDialog(GenreRankingActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                genreList = HttpClient.getGenreList(new OkHttpClient());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(genreList == null) {
                            Toast.makeText(GenreRankingActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        setTabLayout();
                        getGenreRankingData();
                    }
                });
            }
        }).start();
    }

    private void setTabLayout() {
        tabLayout.removeAllViews();
        tabViewList.clear();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i = 0 ; i < genreList.size() ; i++) {
            final int nIndex = i;
            View view = inflater.inflate(R.layout.genre_tab_view, null);

            HashMap<String, String> currentMap = genreList.get(i);

            TextView workTab = view.findViewById(R.id.workTab);
            ImageView workSelectColor = view.findViewById(R.id.workSelectColor);

            if(i == 0) {
                workTab.setTextColor(getResources().getColor(R.color.colorPrimary));
                workSelectColor.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                workSelectColor.setBackgroundColor(Color.parseColor("#e3e3e3"));
                workTab.setTextColor(Color.parseColor("#d1d1d1"));
            }

            workTab.setText(currentMap.get("GENRE_NAME"));
            workTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View oldView = tabViewList.get(nSelectedGenreIndex);
                    ImageView lineView = oldView.findViewById(R.id.workSelectColor);
                    TextView textView = oldView.findViewById(R.id.workTab);
                    lineView.setBackgroundColor(Color.parseColor("#e3e3e3"));
                    textView.setTextColor(Color.parseColor("#d1d1d1"));

                    View tabView = tabViewList.get(nIndex);
                    lineView = tabView.findViewById(R.id.workSelectColor);
                    textView = tabView.findViewById(R.id.workTab);
                    textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                    lineView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    nSelectedGenreIndex = nIndex;

                    getGenreRankingData();
                }
            });

            tabLayout.addView(view);
            tabViewList.add(view);
        }
    }

    private void getGenreRankingData() {
        CommonUtils.showProgressDialog(GenreRankingActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> map = genreList.get(nSelectedGenreIndex);
                String genreID = map.get("GENRE_ID");
                bestList = HttpClient.getGenreRankingList(new OkHttpClient(), genreID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(bestList == null) {
                            Toast.makeText(GenreRankingActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        aa = new CGenreRankingArrayAdapter(GenreRankingActivity.this, R.layout.best_row, bestList);
                        listView.setAdapter(aa);
                    }
                });
            }
        }).start();
    }

    public class CGenreRankingArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CGenreRankingArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.best_row, parent, false);

            WorkVO vo = bestList.get(position);

            String strImgUrl = vo.getCoverFile();

            ImageView coverView = convertView.findViewById(R.id.coverImgView);
            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
            TextView starPointView = convertView.findViewById(R.id.startPointView);
            TextView hitsCountView = convertView.findViewById(R.id.hitsCountView);
            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            TextView subcriptionCountView = convertView.findViewById(R.id.subcriptionCountView);
            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
            coverView.setClipToOutline(true);

            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(GenreRankingActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster_vertical)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            } else {
                if(!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(GenreRankingActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster_vertical)
                        .load(strImgUrl)
                        .apply(new RequestOptions().centerCrop())
                        .into(coverView);
            }

            titleView.setText(vo.getTitle());
            synopsisView.setText(vo.getSynopsis());

            float fStarPoint = vo.getfStarPoint();
            if(fStarPoint == 0)
                starPointView.setText("0");
            else
                starPointView.setText(String.format("%.1f", vo.getfStarPoint()));

            writerNameView.setText(vo.getStrWriterName());
            hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
            commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));
            subcriptionCountView.setText(CommonUtils.getPointCount(vo.getnKeepcount()));

            return convertView;
        }
    }
}
