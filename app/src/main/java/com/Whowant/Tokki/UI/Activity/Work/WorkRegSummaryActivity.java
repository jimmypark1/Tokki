package com.Whowant.Tokki.UI.Activity.Work;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.VO.EpisodeVO;

import java.util.ArrayList;

public class WorkRegSummaryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<EpisodeVO> mArrayList = new ArrayList<>();
    WorkRegSummaryAdapter adapter;

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_reg_summary);

        mActivity = this;

        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("회차 쓰기");

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        findViewById(R.id.ib_top_layout_dot).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_dot).setOnClickListener((v) -> {

        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new WorkRegSummaryAdapter(mActivity, mArrayList);
        recyclerView.setAdapter(adapter);
    }

    public class WorkRegSummaryAdapter extends RecyclerView.Adapter {

        Context context;
        ArrayList<EpisodeVO> arrayList;

        public WorkRegSummaryAdapter(Context context, ArrayList<EpisodeVO> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_work_reg_summary, parent, false);
            return new WorkRegSummaryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    public class WorkRegSummaryViewHolder extends RecyclerView.ViewHolder {

        public WorkRegSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}