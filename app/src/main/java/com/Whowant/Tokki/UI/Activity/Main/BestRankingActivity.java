package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class BestRankingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<WorkVO> bestList;
    private ListView listView;
    private CBestRankingArrayAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_ranking);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("인기작");
        bestList = new ArrayList<>();
        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener(this);

        getBestRankingData();
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

    private void getBestRankingData() {
        CommonUtils.showProgressDialog(BestRankingActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                bestList = HttpClient.getBestRankingList(new OkHttpClient());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        aa = new CBestRankingArrayAdapter(BestRankingActivity.this, R.layout.best_row, bestList);
                        listView.setAdapter(aa);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        WorkVO vo = bestList.get(position);
        Intent intent = new Intent(BestRankingActivity.this, WorkMainActivity.class);
        intent.putExtra("WORK_ID", vo.getnWorkID());
        startActivity(intent);
    }

    public class CBestRankingArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CBestRankingArrayAdapter(Context context, int layout, List titles)
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

            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(BestRankingActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster_vertical)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            } else {
                if(!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(BestRankingActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strImgUrl)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            }

            titleView.setText(vo.getTitle());
            synopsisView.setText(vo.getSynopsis());

            float fStarPoint = vo.getfStarPoint();
            if(fStarPoint == 0)
                starPointView.setText("0");
            else
                starPointView.setText(String.format("%.1f", vo.getfStarPoint()));

            hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
            commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));
            subcriptionCountView.setText(CommonUtils.getPointCount(vo.getnKeepcount()));
            writerNameView.setText(vo.getStrWriterName());

            ImageView rightArrowView = convertView.findViewById(R.id.rightArrowView);
            TextView rightTextView = convertView.findViewById(R.id.rightTextView);
            TextView bestBGView = convertView.findViewById(R.id.bestBGView);

            if(position == 0) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("1");
                rightArrowView.setBackgroundResource(R.drawable.up_red_arrow);
                rightTextView.setTextColor(Color.parseColor("#ff0000"));
                rightTextView.setText("3");
            } else if(position == 1) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("2");
                rightArrowView.setBackgroundResource(R.drawable.up_red_arrow);
                rightTextView.setTextColor(Color.parseColor("#ff0000"));
                rightTextView.setText("1");
            } else if(position == 2) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("3");
                rightArrowView.setBackgroundResource(R.drawable.down_blue_arrow);
                rightTextView.setTextColor(Color.parseColor("#0000ff"));
                rightTextView.setText("1");
            } else if(position == 3) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("4");
//                bestBGView.setVisibility(View.INVISIBLE);
                rightArrowView.setBackgroundResource(R.drawable.keep_icon);
                rightTextView.setTextColor(getResources().getColor(R.color.colorTextGray));
            } else if(position == 4) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("5");
//                bestBGView.setVisibility(View.INVISIBLE);
                rightArrowView.setBackgroundResource(R.drawable.up_red_arrow);
                rightTextView.setTextColor(Color.parseColor("#ff0000"));
                rightTextView.setText("2");
            } else if(position == 5) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("6");
//                bestBGView.setVisibility(View.INVISIBLE);
                rightArrowView.setBackgroundResource(R.drawable.keep_icon);
                rightTextView.setTextColor(getResources().getColor(R.color.colorTextGray));
                rightTextView.setText("");
            } else if(position == 6) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("7");
//                bestBGView.setVisibility(View.INVISIBLE);
                rightArrowView.setBackgroundResource(R.drawable.down_blue_arrow);
                rightTextView.setTextColor(Color.parseColor("#0000ff"));
                rightTextView.setText("3");
            } else if(position == 7) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("8");
//                bestBGView.setVisibility(View.INVISIBLE);
                rightArrowView.setBackgroundResource(R.drawable.keep_icon);
                rightTextView.setTextColor(getResources().getColor(R.color.colorTextGray));
                rightTextView.setText("");
            } else if(position == 8) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("9");
//                bestBGView.setVisibility(View.INVISIBLE);
                rightArrowView.setBackgroundResource(R.drawable.down_blue_arrow);
                rightTextView.setTextColor(Color.parseColor("#0000ff"));
                rightTextView.setText("2");
            } else if(position == 9) {
                bestBGView.setVisibility(View.VISIBLE);
                bestBGView.setText("10");
//                bestBGView.setVisibility(View.INVISIBLE);
                rightArrowView.setBackgroundResource(R.drawable.up_red_arrow);
                rightTextView.setTextColor(Color.parseColor("#ff0000"));
                rightTextView.setText("3");
            }

            return convertView;
        }
    }
}
