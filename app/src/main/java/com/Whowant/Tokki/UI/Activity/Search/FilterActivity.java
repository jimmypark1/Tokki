package com.Whowant.Tokki.UI.Activity.Search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FilterAdapter adapter;
    ArrayList<String> mArrayList = new ArrayList<>();

    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getData();
        initView();
        initData();
    }

    private void getData() {
        if(getIntent() != null && getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("title");
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText(title);
        findViewById(R.id.tv_top_layout_title).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());
        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FilterAdapter(this, mArrayList);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        mArrayList.add("제목");
        mArrayList.add("저자");
        mArrayList.add("최근에 읽은");
        mArrayList.add("최근에 추가된");

        adapter.notifyDataSetChanged();
    }

    public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        ArrayList<String> arrayList;

        public FilterAdapter(Context context, ArrayList<String> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_filter, parent, false);
            return new FilterViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            String title = arrayList.get(position);

            if(holder instanceof FilterViewHolder) {
                FilterViewHolder viewHolder = (FilterViewHolder)holder;

                viewHolder.filterTv.setText(title);
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
        RadioButton filterRb;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);

            filterLl = itemView.findViewById(R.id.ll_filter);
            filterTv = itemView.findViewById(R.id.tv_filter);
            filterRb = itemView.findViewById(R.id.rb_filter);
        }
    }
}