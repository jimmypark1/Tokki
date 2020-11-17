package com.Whowant.Tokki.UI.Activity.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.TypeOnClickListener;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FilterAdapter adapter;
    ArrayList<FilterVo> mArrayList = new ArrayList<>();

    String title = "";

    public static final int TYPE_SEARCH_REUSLT = 0;
    public static final int TYPE_STORAGE_BOX = 1;

    private int type = TYPE_SEARCH_REUSLT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getData();
        initView();
        initData();
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("title");
            type = getIntent().getIntExtra("type", TYPE_SEARCH_REUSLT);
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText(title);
        findViewById(R.id.tv_top_layout_title).setVisibility(View.VISIBLE);

        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> {
            Intent intent = new Intent();
            intent.putExtra("order", isSelectedOrder());
            setResult(RESULT_OK, intent);
            finish();
        });
        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FilterAdapter(this, mArrayList);
        recyclerView.setAdapter(adapter);
    }

    private String isSelectedOrder() {
        for (FilterVo vo : mArrayList) {
            if (vo.isSelected()) {
                return vo.getOrder();
            }
        }

        return "";
    }

    private void initData() {
        switch (type) {
            case TYPE_SEARCH_REUSLT:
                mArrayList.add(new FilterVo("인기순", "POPULAR"));
                mArrayList.add(new FilterVo("최신순", "UPDATE"));
                break;

            case TYPE_STORAGE_BOX:
                mArrayList.add(new FilterVo("제목", "TITLE"));
                mArrayList.add(new FilterVo("저자", "WRITER"));
                mArrayList.add(new FilterVo("최근에 읽은", "RECENTLY"));
                mArrayList.add(new FilterVo("최근에 추가된", "UPDATE"));
                break;
        }

        adapter.notifyDataSetChanged();
    }

    public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<FilterVo> arrayList;

        public FilterAdapter(Context context, ArrayList<FilterVo> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_filter, parent, false);
            return new FilterViewHolder(v, new TypeOnClickListener() {
                @Override
                public void onClick(int type, int position) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        arrayList.get(i).setSelected(i == position);
                    }

                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            FilterVo item = arrayList.get(position);

            if (holder instanceof FilterViewHolder) {
                FilterViewHolder viewHolder = (FilterViewHolder) holder;

                viewHolder.filterTv.setText(item.getName());
                viewHolder.filterIv.setSelected(item.isSelected());
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder {

        LinearLayout filterLl;
        TextView filterTv;
        ImageView filterIv;

        public FilterViewHolder(@NonNull View itemView, TypeOnClickListener listener) {
            super(itemView);

            filterLl = itemView.findViewById(R.id.ll_filter);
            filterTv = itemView.findViewById(R.id.tv_filter);
            filterIv = itemView.findViewById(R.id.iv_filter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(0, getAdapterPosition());
                    }
                }
            });
        }
    }

    public class FilterVo {
        public String name;
        public boolean selected;
        public String order;

        public FilterVo(String name, String order) {
            this.name = name;
            this.order = order;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("order", isSelectedOrder());
        setResult(RESULT_OK, intent);
        finish();
    }
}