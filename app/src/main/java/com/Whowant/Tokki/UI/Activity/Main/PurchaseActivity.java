package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.CoinPurchaseAdapter;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;

public class PurchaseActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoinPurchaseAdapter adapter;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        pref = getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("코인구매/충전");

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new CoinPurchaseAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        TextView myCoinView = findViewById(R.id.myCoinView);
        DecimalFormat format = new DecimalFormat("###,###");//콤마
        myCoinView.setText(format.format(pref.getInt("COIN_COUNT", 0)));
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