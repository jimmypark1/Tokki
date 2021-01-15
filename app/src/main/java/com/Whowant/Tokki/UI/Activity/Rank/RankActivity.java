package com.Whowant.Tokki.UI.Activity.Rank;

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

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Search.FilterActivity;

public class RankActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("어드벤처");

        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());

        findViewById(R.id.ib_top_layout_filter).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_filter).setOnClickListener((v) -> startActivity(new Intent(this, FilterActivity.class)));
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new RankAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;

        public RankAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.row_search_category, parent, false);
            return new RankViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

    public class RankViewHolder extends RecyclerView.ViewHolder {

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}