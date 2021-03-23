package com.Whowant.Tokki.UI.Activity.Market;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.Utils.SimplePreference;
import com.Whowant.Tokki.VO.MarketMsg;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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





        getUserType();


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


    private void getUserType() {
//        CommonUtils.showProgressDialog(getActivity(), "서버에서 데이터를 가져오고 있습니다. 잠시만 기다려주세요.");


        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SimplePreference.getStringPreference(TransactionActivity.this, "USER_INFO", "USER_ID", "Guest");

                JSONObject resultObject = HttpClient.getUserInfo(new OkHttpClient(), userId);
                // JSONObject resultObject = HttpClient.getMyInfo(new OkHttpClient(), userId);
                CommonUtils.hideProgressDialog();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        CommonUtils.hideProgressDialog();

                        try {
                            if (resultObject == null) {
                                return;
                            }

                            String ret = resultObject.getString("COMMENT");
                            int nType = resultObject.getInt("TYPE");
                            if(nType == 0 ||nType == 1)
                            {

                                tabLayout.addTab(tabLayout.newTab().setText("거래중"));
                                tabLayout.addTab(tabLayout.newTab().setText("거래완료"));


                                tabLayout.addTab(tabLayout.newTab().setText("당근충전"));

                                pagerAdapter = new TransactionAdapter(getSupportFragmentManager(),0);

                                viewPager.setAdapter(pagerAdapter);

                            }
                            else
                            {
                                tabLayout.addTab(tabLayout.newTab().setText("거래중"));
                                tabLayout.addTab(tabLayout.newTab().setText("거래완료"));

                                tabLayout.addTab(tabLayout.newTab().setText("정산"));

                                pagerAdapter = new TransactionAdapter(getSupportFragmentManager(),1);

                                viewPager.setAdapter(pagerAdapter);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }

}