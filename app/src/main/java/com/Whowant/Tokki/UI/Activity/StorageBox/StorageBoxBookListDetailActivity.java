package com.Whowant.Tokki.UI.Activity.StorageBox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Work.WorkMainActivity;
import com.Whowant.Tokki.UI.TypeOnClickListener;
import com.Whowant.Tokki.UI.ViewHolder.SearchResultViewHolder;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.ItemClickSupport;
import com.Whowant.Tokki.VO.WorkListVo;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class StorageBoxBookListDetailActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StorageBoxBookListDetailAdapter adapter;
    ArrayList<WorkListVo> mArrayList = new ArrayList<>();

    String title = "";
    String readingId;

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_box_book_list_detail);

        mActivity = this;

        getData();
        initView();

        getReadingList(readingId);
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("title");
            readingId = getIntent().getStringExtra("readingId");
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText(title);

        (findViewById(R.id.ib_top_layout_back)).setVisibility(View.VISIBLE);
        (findViewById(R.id.ib_top_layout_back)).setOnClickListener((v) -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new StorageBoxBookListDetailAdapter(mActivity, mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                WorkListVo item = mArrayList.get(position);

                Intent intent = new Intent(mActivity, WorkMainActivity.class);
                intent.putExtra("WORK_ID", item.getWORK_ID());
                startActivity(intent);
            }
        });
    }


    public class StorageBoxBookListDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        private ArrayList<WorkListVo> arrayList;

        public StorageBoxBookListDetailAdapter(Context context, ArrayList<WorkListVo> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_search_category, parent, false);
            return new SearchResultViewHolder(v, new TypeOnClickListener() {
                @Override
                public void onClick(int type, int position) {

                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            WorkListVo item = arrayList.get(position);

            if (holder instanceof SearchResultViewHolder) {
                SearchResultViewHolder viewHolder = (SearchResultViewHolder) holder;

                String strImgUrl = item.getWORK_COVER_IMG();

                if (strImgUrl == null || strImgUrl.equals("null") || strImgUrl.equals("NULL") || strImgUrl.length() == 0) {
                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.round_4_dddddd)
                            .load(R.drawable.round_4_dddddd)
                            .into(viewHolder.photoIv);
                } else {
                    if (!strImgUrl.startsWith("http")) {
                        strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                    }

                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.round_4_dddddd)
                            .load(strImgUrl)
                            .into(viewHolder.photoIv);
                }

                viewHolder.titleTv.setText(item.getWORK_TITLE());
                viewHolder.writerTv.setText("by " + item.getWRITER_NAME());
                viewHolder.heartTv.setText(CommonUtils.getPointCount(item.getKEEP_COUNT()));
                viewHolder.tabTv.setText(CommonUtils.getPointCount(item.getTAB_COUNT()));
                viewHolder.synopsisTv.setText(item.getWORK_SYNOPSIS());

                float fStarPoint = item.getSTAR_POINT();

                if (fStarPoint == 0)
                    viewHolder.starTv.setText("0");
                else
                    viewHolder.starTv.setText(String.format("%.1f", fStarPoint));
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    private void getReadingList(String readingId) {
        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mArrayList.addAll(HttpClient.getReadingListDetail(new OkHttpClient(), readingId));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
}