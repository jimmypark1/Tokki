package com.Whowant.Tokki.UI.Activity.Writer;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.LiteratureWriteActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WriterVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class FollowingWriterListActivity extends AppCompatActivity {
    private ListView listView;
    private TextView emptyView;
    private ArrayList<WriterVO> writerList;
    private SharedPreferences pref;
    private CFollowArrayAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_writer_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("팔로우 작가 리스트");

        pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);

        writerList = new ArrayList<WriterVO>();

        aa = new CFollowArrayAdapter(this, R.layout.follow_writer_row, writerList);
        listView.setAdapter(aa);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WriterVO vo = writerList.get(position);
                Intent intent = new Intent(FollowingWriterListActivity.this, WriterMainActivity.class);
                intent.putExtra("USER_ID", vo.getStrWriterID());
                startActivity(intent);
            }
        });
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

    @Override
    public void onResume() {
        super.onResume();
        getWriterList();
    }

    private void getWriterList() {
        CommonUtils.showProgressDialog(this, "팔로우 리스트를 가져오고 있습니다.");
        writerList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                writerList.addAll(HttpClient.getMyFollowingList(new OkHttpClient(), pref.getString("USER_ID", "Guest")));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(writerList == null || writerList.size() == 0) {
                            Toast.makeText(FollowingWriterListActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        aa.notifyDataSetChanged();

                        if(writerList.size() > 0) {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    public class CFollowArrayAdapter extends ArrayAdapter<Object>
    {
        private LayoutInflater mLiInflater;

        CFollowArrayAdapter(Context context, int layout, List titles)
        {
            super(context, layout, titles);
            mLiInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLiInflater.inflate(R.layout.follow_writer_row, parent, false);

            WriterVO vo = writerList.get(position);

            ImageView faceView = convertView.findViewById(R.id.faceView);
            TextView nameView = convertView.findViewById(R.id.nameView);
            TextView writerCommentView = convertView.findViewById(R.id.writerCommentView);
            TextView followCountView = convertView.findViewById(R.id.followerView);
            ImageView emptyIconView = convertView.findViewById(R.id.emptyIconView);
            ImageView lv1IconView = convertView.findViewById(R.id.lv1IconView);
            ImageView lv5IconView = convertView.findViewById(R.id.lv5IconView);
            ImageView lv10IconView = convertView.findViewById(R.id.lv10IconView);
            RelativeLayout smallLv10View = convertView.findViewById(R.id.smallLv10View);
            RelativeLayout levelBGView = convertView.findViewById(R.id.levelBGView);
            TextView smallLvView = convertView.findViewById(R.id.smallLvView);

            String strPhoto = vo.getStrWriterPhoto();

            if(!strPhoto.startsWith("http"))
                strPhoto = CommonUtils.strDefaultUrl + "images/" + strPhoto;

            Glide.with(FollowingWriterListActivity.this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(strPhoto)
                    .apply(new RequestOptions().circleCrop())
                    .into(faceView);

            nameView.setText(vo.getStrWriterName());

            String strComment = vo.getStrWriterComment();
            if(strComment == null || strComment.length() == 0 || strComment.equals("null"))
                strComment = "소개글이 없습니다.";

            writerCommentView.setText(strComment);
            followCountView.setText(CommonUtils.getPointCount(vo.getnFollowcount()));

            emptyIconView.setVisibility(View.GONE);
            lv1IconView.setVisibility(View.GONE);
            lv5IconView.setVisibility(View.GONE);
            lv10IconView.setVisibility(View.GONE);
            smallLv10View.setVisibility(View.GONE);

            int nLevel = CommonUtils.getLevel(vo.getnDonationCarrot());
            switch(nLevel) {
                case 1:
                    levelBGView.setBackgroundResource(R.drawable.lv1_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv1_bg);
                    smallLvView.setText("LV.1");
                    lv1IconView.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    levelBGView.setBackgroundResource(R.drawable.lv2_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv2_bg);
                    smallLvView.setText("LV.2");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    levelBGView.setBackgroundResource(R.drawable.lv3_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv3_bg);
                    smallLvView.setText("LV.3");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    levelBGView.setBackgroundResource(R.drawable.lv4_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv4_bg);
                    smallLvView.setText("LV.4");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    levelBGView.setBackgroundResource(R.drawable.lv5_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv5_bg);
                    smallLvView.setText("LV.5");
                    lv5IconView.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    levelBGView.setBackgroundResource(R.drawable.lv6_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv6_bg);
                    smallLvView.setText("LV.6");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    levelBGView.setBackgroundResource(R.drawable.lv7_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv7_bg);
                    smallLvView.setText("LV.7");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    levelBGView.setBackgroundResource(R.drawable.lv8_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv8_bg);
                    smallLvView.setText("LV.8");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 9:
                    levelBGView.setBackgroundResource(R.drawable.lv9_bg);
                    smallLvView.setBackgroundResource(R.drawable.lv9_bg);
                    smallLvView.setText("LV.9");
                    emptyIconView.setVisibility(View.VISIBLE);
                    break;
                case 10:
                    levelBGView.setBackgroundResource(R.drawable.lv10_bg);
                    smallLvView.setVisibility(View.GONE);
                    lv10IconView.setVisibility(View.VISIBLE);
                    smallLv10View.setVisibility(View.VISIBLE);
                    break;
            }

            return convertView;
        }
    }
}
