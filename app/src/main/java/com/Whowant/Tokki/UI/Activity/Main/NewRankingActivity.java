package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class NewRankingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<WorkVO> bestList;
    private ListView listView;
    private CNewRankingArrayAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ranking);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("최신작");

        bestList = new ArrayList<>();
        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener(this);

        getNewRankingData();
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

    private void getNewRankingData() {
        CommonUtils.showProgressDialog(NewRankingActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                bestList = HttpClient.getNewRankingList(new OkHttpClient());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        aa = new CNewRankingArrayAdapter(NewRankingActivity.this, R.layout.best_row, bestList);
                        listView.setAdapter(aa);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        WorkVO vo = bestList.get(position);
        Intent intent = new Intent(NewRankingActivity.this, WorkMainActivity.class);
        intent.putExtra("WORK_ID", vo.getnWorkID());
        startActivity(intent);
    }

    public class CNewRankingArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CNewRankingArrayAdapter(Context context, int layout, List titles)
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
            TextView newView = convertView.findViewById(R.id.newView);
            LinearLayout rightLayout = convertView.findViewById(R.id.rightLayout);
            rightLayout.setVisibility(View.GONE);
            TextView bestBGView = convertView.findViewById(R.id.bestBGView);
            bestBGView.setVisibility(View.GONE);
//            newView.setVisibility(View.VISIBLE);

            TextView writerNameView = convertView.findViewById(R.id.writerNameView);

            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(NewRankingActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster_vertical)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            } else {
                if(!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(NewRankingActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(strImgUrl)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            }

            writerNameView.setText(vo.getStrWriterName());
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

            return convertView;
        }
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }
}
