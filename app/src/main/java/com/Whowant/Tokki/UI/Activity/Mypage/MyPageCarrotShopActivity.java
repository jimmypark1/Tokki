package com.Whowant.Tokki.UI.Activity.Mypage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Adapter.MyPageCarrotShopAdapter;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import okhttp3.OkHttpClient;

public class MyPageCarrotShopActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyPageCarrotShopAdapter pagerAdapter;
    TextView carrotTv;

    int nCurrentCarrot = 0;

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_carrot_shop);

        mActivity = this;

        initView();

        getMyCarrotInfo();
    }

    private void initView() {
        findViewById(R.id.ll_top_layout_carrot).setVisibility(View.VISIBLE);
        carrotTv = findViewById(R.id.tv_top_layout_carrot);

        findViewById(R.id.ib_top_layout_close_back).setVisibility(View.VISIBLE);
        findViewById(R.id.ib_top_layout_close_back).setOnClickListener((v) -> finish());

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new MyPageCarrotShopAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void getMyCarrotInfo() {
        CommonUtils.showProgressDialog(mActivity, "서버와 통신중입니다. 잠시만 기다려 주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(mActivity, "USER_INFO", "USER_ID", "Guest");

                JSONObject resultObject = HttpClient.getMyCarrotInfo(new OkHttpClient(), userId);
                CommonUtils.hideProgressDialog();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.hideProgressDialog();

                        try {
                            if (resultObject == null) {
                                return;
                            }

                            if (resultObject.getString("RESULT").equals("SUCCESS")) {
                                nCurrentCarrot = resultObject.getInt("CURRENT_CARROT");
                            }

                            carrotTv.setText(new DecimalFormat("###,###").format(nCurrentCarrot) + " 개");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}