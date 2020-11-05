package com.Whowant.Tokki.UI.Activity.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.CreateWorkActivity;
import com.Whowant.Tokki.UI.Activity.Work.WorkWriteMainActivity;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.WorkVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class WriterWorkListActivity extends AppCompatActivity {
    private ArrayList<WorkVO> workList;
    private ListView listView;
    private TextView emptyView;
    private CWorkListAdapter aa;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_work_list);

        userID = getIntent().getStringExtra("USER_ID");

        listView = findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
        emptyView.setVisibility(View.INVISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WorkVO workVO = workList.get(position);
                Intent intent = new Intent(WriterWorkListActivity.this, WorkWriteMainActivity.class);
                intent.putExtra("WORK_ID", "" + workVO.getnWorkID());
                intent.putExtra("USER_AUTHORITY", "" + workVO.getnUserAuthority());
                intent.putExtra("EDIT_AUTHORITY", "" + workVO.getnEditAuthority());
                startActivity(intent);
            }
        });

        GetAllWorkList();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetAllWorkList();
    }

    private void GetAllWorkList() {
        CommonUtils.showProgressDialog(WriterWorkListActivity.this, "서버에서 작품 목록을 가져오고 있습니다. 잠시만 기다려주세요.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                workList = HttpClient.GetAllWorkListWithID(new OkHttpClient(), userID);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if (workList == null) {
                            emptyView.setVisibility(View.VISIBLE);
                            return;
                        }

                        aa = new CWorkListAdapter(WriterWorkListActivity.this, R.layout.my_work_write_row, workList);
                        listView.setAdapter(aa);

                        if (workList.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickTopRightBtn(View view) {
        Intent intent = new Intent(WriterWorkListActivity.this, CreateWorkActivity.class);
        intent.putExtra("WRITER_ID", userID);
        startActivity(intent);
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

    public class CWorkListAdapter extends ArrayAdapter<Object> {
        private int mCellLayout;
        private LayoutInflater mLiInflater;
        private SharedPreferences.Editor editor;

        CWorkListAdapter(Context context, int layout, List titles) {
            super(context, layout, titles);
            mCellLayout = layout;
            mLiInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            WorkVO workVO = workList.get(position);

            if (convertView == null) {
                convertView = mLiInflater.inflate(R.layout.my_work_write_row, parent, false);
            }

            ImageView coverImgView = convertView.findViewById(R.id.coverImgView);
            TextView titleView = convertView.findViewById(R.id.titleView);
            TextView synopsisView = convertView.findViewById(R.id.synopsisView);
            TextView writerNameView = convertView.findViewById(R.id.writerNameView);
            TextView dateView = convertView.findViewById(R.id.dateView);
            coverImgView.setClipToOutline(true);

            String strImgUrl = workVO.getCoverFile();
            if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                Glide.with(WriterWorkListActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(R.drawable.no_poster)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverImgView);
            } else {
                if (!strImgUrl.startsWith("http")) {
                    strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                }

                Glide.with(WriterWorkListActivity.this)
                        .asBitmap() // some .jpeg files are actually gif
                        .placeholder(R.drawable.no_poster)
                        .load(strImgUrl)
                        .apply(new RequestOptions().override(800, 800))
                        .into(coverImgView);
            }

            titleView.setText(workVO.getTitle());
            synopsisView.setText(workVO.getSynopsis());
            writerNameView.setText("by " + workVO.getStrWriterName());
            dateView.setText(workVO.getCreatedDate().substring(0, 10));

            return convertView;
        }
    }
}