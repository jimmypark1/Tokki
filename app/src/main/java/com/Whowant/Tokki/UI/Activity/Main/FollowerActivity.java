package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.FlowerAdapter;
import com.google.android.material.tabs.TabLayout;

public class FollowerActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String strWriterID;
    private String strWriterName;
    private FlowerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower);

//        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
//        String strMyID = pref.getString("USER_NAME", "Guest");

        int nFollowerCount = getIntent().getIntExtra("FOLLOWER_COUNT", 0);
        int nFollowingCount = getIntent().getIntExtra("FOLLOWING_COUNT", 0);
        strWriterName = getIntent().getStringExtra("WRITER_NAME");
        strWriterID = getIntent().getStringExtra("WRITER_ID");

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle(strWriterName);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new FlowerAdapter(getSupportFragmentManager(), nFollowerCount, nFollowingCount, strWriterID);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        int nIndex = getIntent().getIntExtra("SELECTED_INDEX", 1);

        if(nIndex == 1)
            viewPager.setCurrentItem(1);
        else
            viewPager.setCurrentItem(0);
    }

    public void onClickTopLeftBtn(View view) {
        finish();
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
}
