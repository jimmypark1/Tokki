package com.Whowant.Tokki.UI.Activity.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.FlowerAdapter;
import com.Whowant.Tokki.UI.Adapter.PopularAdapter;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;

public class PopularActivity extends AppCompatActivity {                    // 인기작
    private ArrayList<HashMap<String, String>> genreList;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PopularAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        getGenreList();
    }

    private void getGenreList() {
        CommonUtils.showProgressDialog(PopularActivity.this, "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                genreList = HttpClient.getGenreList(new OkHttpClient());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        if(genreList == null) {
                            Toast.makeText(PopularActivity.this, "서버와의 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        pagerAdapter = new PopularAdapter(PopularActivity.this, getSupportFragmentManager(), genreList);
                        viewPager.setAdapter(pagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }
}