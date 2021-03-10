package com.Whowant.Tokki.UI.Activity.Search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class SearchCategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SearchCategoryAdapter adapter;
    ArrayList<WorkListVo> mArrayList = new ArrayList<>();
    String genre;
    String order = "UPDATE";

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_category);

        mActivity = this;

        getData();
        initView();

        getGenreWorkList("");
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            genre = getIntent().getStringExtra("genre");
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText(genre);

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        findViewById(R.id.ib_top_layout_filter).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_filter).setOnClickListener((v) -> {
            Intent intent = new Intent(this, FilterActivity.class);
            intent.putExtra("title", "정렬");
            intent.putExtra("type", FilterActivity.TYPE_SEARCH_REUSLT);
            intent.putExtra("order", order);
            startActivityForResult(intent, 101);
        });

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new SearchCategoryAdapter(this, mArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                WorkListVo item = mArrayList.get(position);

                Intent intent = new Intent(mActivity, WorkMainActivity.class);
                intent.putExtra("WORK_ID", item.getWORK_ID());
                intent.putExtra("WORK_TYPE", item.getTARGET());

                startActivity(intent);
            }
        });
    }

    public class SearchCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<WorkListVo> arrayList;

        public SearchCategoryAdapter(Context context, ArrayList<WorkListVo> arrayList) {
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
                            .placeholder(R.drawable.no_poster)
                            .load(R.drawable.no_poster_vertical)
                            .into(viewHolder.photoIv);
                } else {
                    if (!strImgUrl.startsWith("http")) {
                        strImgUrl = CommonUtils.strDefaultUrl + "images/" + strImgUrl;
                    }

                    Glide.with(context)
                            .asBitmap() // some .jpeg files are actually gif
                            .placeholder(R.drawable.no_poster)
                            .load(strImgUrl)
                            .into(viewHolder.photoIv);
                }

                viewHolder.titleTv.setText(item.getWORK_TITLE());
                viewHolder.writerTv.setText("by " + item.getWRITER_NAME());
                viewHolder.heartTv.setText(CommonUtils.getPointCount(item.getKEEP_COUNT()));
                viewHolder.tabTv.setText(CommonUtils.getPointCount(item.getHITS_COUNT()));
                viewHolder.synopsisTv.setText(item.getWORK_SYNOPSIS());

                float fStarPoint = item.getSTAR_POINT();

                if (fStarPoint == 0)
                    viewHolder.starTv.setText("0");
                else
                    viewHolder.starTv.setText(String.format("%.1f", fStarPoint));

                viewHolder.optionIv.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    private void getGenreWorkList(String order) {
        mArrayList.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mArrayList.addAll(HttpClient.getGenreWorkList(new OkHttpClient(), genre, order));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            if (data != null) {
                order = data.getStringExtra("order");
                getGenreWorkList(order);
            }
        }
    }
}