package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.Whowant.Tokki.UI.Activity.Work.LiteratureListActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class KeepActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private CKeepArrayAdapter aa;
    private ArrayList<WorkVO> keepList;
    private ListView listView;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep);

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        keepList = new ArrayList<>();
        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener(this);

        getKeepListData();
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

    private void getKeepListData() {
        CommonUtils.showProgressDialog(KeepActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");
        keepList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                keepList = HttpClient.getKeepWorkList(new OkHttpClient(), pref.getString("USER_ID", "Guest"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        aa = new CKeepArrayAdapter(KeepActivity.this, R.layout.best_row, keepList);
                        listView.setAdapter(aa);
                    }
                });
            }
        }).start();
    }

    public void onClickHomeBtn(View view) {
        startActivity(new Intent(KeepActivity.this, MainActivity.class));
        finish();
    }

    public void onClickKeepBtn(View view) {

    }

    public void onClickWriteBtn(View view) {
        startActivity(new Intent(KeepActivity.this, LiteratureListActivity.class));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        WorkVO vo = keepList.get(position);
        Intent intent = new Intent(KeepActivity.this, WorkMainActivity.class);
        intent.putExtra("WORK_ID", vo.getnWorkID());
        startActivity(intent);
    }

    public class CKeepArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CKeepArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.best_row, parent, false);

            LinearLayout rightLayout = convertView.findViewById(R.id.rightLayout);
            rightLayout.setVisibility(View.GONE);

            WorkVO vo = keepList.get(position);

            String strImgUrl = vo.getCoverFile();

            ImageView coverView = convertView.findViewById(R.id.coverImgView);
            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
            TextView starPointView = convertView.findViewById(R.id.startPointView);
            TextView hitsCountView = convertView.findViewById(R.id.hitsCountView);
            TextView commentCountView = convertView.findViewById(R.id.commentCountView);
            TextView subcriptionCountView = convertView.findViewById(R.id.subcriptionCountView);
            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
            TextView bestBGView = convertView.findViewById(R.id.bestBGView);

            bestBGView.setVisibility(View.GONE);
            if(strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(KeepActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster_vertical)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverView);
            } else {
                if(!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(KeepActivity.this)
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
            commentCountView.setText("" + vo.getnCommentCount());
            subcriptionCountView.setText("" + vo.getnKeepcount());

            return convertView;
        }
    }
}
