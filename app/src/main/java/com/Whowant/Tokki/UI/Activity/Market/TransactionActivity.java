package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.google.android.material.tabs.TabLayout;

import okhttp3.OkHttpClient;

public class TransactionActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private TransactionAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("거래중"));
        tabLayout.addTab(tabLayout.newTab().setText("거래완료"));
        tabLayout.addTab(tabLayout.newTab().setText("당근충전"));

        pagerAdapter = new TransactionAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);



       int nMsgInfo =  getIntent().getIntExtra("INTO_MSG_INFO",0);
       if(nMsgInfo > 0)
       {
           viewPager.setCurrentItem(2);

       }


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                viewPager.setCurrentItem(pos);

                if(pos == 0)
                {

                }
                else if(pos == 1)
                {

                }
                else if(pos == 1)
                {

                }


            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

}