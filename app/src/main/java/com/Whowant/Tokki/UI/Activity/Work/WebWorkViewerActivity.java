package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Market.MarketContentsFragment;
import com.Whowant.Tokki.UI.Activity.Market.MarketGenreFragment;
import com.Whowant.Tokki.UI.Activity.Market.MarketPagerAdapter;
import com.Whowant.Tokki.UI.Activity.Market.MarketTagFragment;
import com.Whowant.Tokki.Utils.CommonUtils;
import com.Whowant.Tokki.VO.EpisodeVO;
import com.Whowant.Tokki.VO.WebWorkVO;
import com.Whowant.Tokki.VO.WorkVO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class WebWorkViewerActivity extends AppCompatActivity {

    WorkVO work;
    int episodeIndex = 0;
    int lastOrder = -1;
    int lastIndex = -1;

    int episodeID = 0;

    ArrayList<WebWorkVO> webs;

    TextView title;

    WebWorkPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_work_viewer);

        viewPager = findViewById(R.id.viewPager);


       title = findViewById(R.id.titleView );
/*
        webview = findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용


 */
        work = ViewerActivity.workVO;//(WorkVO)getIntent().getSerializableExtra("WORK");
        episodeIndex = getIntent().getIntExtra("EPISODE_INDEX",0);
        lastOrder = getIntent().getIntExtra("LAST_ORDER",-1);

        episodeIndex = work.getEpisodeList().size() - episodeIndex - 1;

        EpisodeVO episode = work.getEpisodeList().get(episodeIndex);

        title.setText(episode.getStrTitle());

        episodeID = episode.getnEpisodeID();

        getEpisodeNovelData();


    }
    void getEpisodeNovelData()
    {

        CommonUtils.showProgressDialog(WebWorkViewerActivity.this, "서버와 통신중입니다. 잠시만 기다려주세요.");

        new Thread(new Runnable() {
            @Override
            public void run() {
                webs = HttpClient.getEpisodeNovelData(new OkHttpClient(), String.valueOf(episodeID));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        CommonUtils.hideProgressDialog();
                        WebWorkVO data = webs.get(1);
                        webview.loadData(data.getRaw(), "text/html; charset=utf-8", "UTF-8");
                        */
                        CommonUtils.hideProgressDialog();
                        pagerAdapter = new WebWorkPagerAdapter(getSupportFragmentManager());

                        for(int i = 0;i< webs.size();i++)
                        {
                            WebWorkVO data = webs.get(i);
                            WebWorkFragment fragmet =  new WebWorkFragment();
                            fragmet.webWork = data;
                            pagerAdapter.fragments.add(fragmet);

                        }
                        viewPager.setAdapter(pagerAdapter);
                        pagerAdapter.notifyDataSetChanged();




                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }


    public class WebWorkPagerAdapter extends FragmentStatePagerAdapter {

        public List<Fragment> fragments=new ArrayList<>();

        public WebWorkPagerAdapter(FragmentManager fm) {
            super(fm);
           // fragments.add(new MarketContentsFragment());
           // fragments.add(new MarketGenreFragment());
           // fragments.add(new MarketTagFragment());
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}