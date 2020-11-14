package com.Whowant.Tokki.UI.Activity.Mypage;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.MyPageFollowerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MyPageFollowerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyPageFollowerAdapter pagerAdapter;

    String writerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_follower);

        getData();
        initView();
    }

    private void getData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            writerId = getIntent().getStringExtra("writerId");
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_top_layout_title)).setText("마이페이지");

        findViewById(R.id.ib_top_layout_back).setOnClickListener((v) -> finish());
        findViewById(R.id.ib_top_layout_back).setVisibility(View.VISIBLE);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new MyPageFollowerAdapter(this, getSupportFragmentManager(), writerId);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}