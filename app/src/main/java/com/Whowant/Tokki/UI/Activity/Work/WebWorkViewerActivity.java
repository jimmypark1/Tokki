package com.Whowant.Tokki.UI.Activity.Work;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer;

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


    Boolean show = false;


    WebWorkPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    private RecyclerView episodeListView;
    private ArrayList<EpisodeVO> episodeList;

    private WebEpisodeListAdapter ea;
    Animation translateDown, translateUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_work_viewer);

        viewPager = findViewById(R.id.viewPager);

        title = findViewById(R.id.titleView );



        work = ViewerActivity.workVO;//(WorkVO)getIntent().getSerializableExtra("WORK");
        episodeIndex = getIntent().getIntExtra("EPISODE_INDEX",0);
        lastOrder = getIntent().getIntExtra("LAST_ORDER",-1);

        episodeListView = findViewById(R.id.episodeListView);
        episodeList = work.getEpisodeList();

        ea = new WebEpisodeListAdapter(WebWorkViewerActivity.this, episodeList);
        episodeListView.setAdapter(ea);
        episodeListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        episodeListView.getLayoutManager().scrollToPosition(episodeList.size() - episodeIndex - 1);


        episodeIndex = work.getEpisodeList().size() - episodeIndex - 1;

        EpisodeVO episode = work.getEpisodeList().get(episodeIndex);

        title.setText(episode.getStrTitle());

        episodeID = episode.getnEpisodeID();

        getEpisodeNovelData();
        ToggleButton episodeListBtn = findViewById(R.id.episodeListBtn);
        LinearLayout episodeListLayout = findViewById(R.id.episodeListLayout);
        translateDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);
        translateUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up);

        episodeListBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    episodeListLayout.setVisibility(View.VISIBLE);
                    translateDown.setFillAfter(true);
                    episodeListLayout.startAnimation(translateDown);
                } else {
                    episodeListLayout.startAnimation(translateUp);
                    episodeListLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

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
                        // Create an object of page transformer
                        BookFlipPageTransformer bookFlipPageTransformer = new BookFlipPageTransformer();

                        bookFlipPageTransformer.setEnableScale(true);
                        bookFlipPageTransformer.setScaleAmountPercent(10f);
                        viewPager.setPageTransformer(true, bookFlipPageTransformer);
                        pagerAdapter.notifyDataSetChanged();




                    }
                });
            }
        }).start();
    }

    public void onClickTopLeftBtn(View view) {
        finish();
    }
    public void onClickSettingsBtn(View view) {

        int pos = viewPager.getCurrentItem();
        WebWorkFragment fragment = (WebWorkFragment)pagerAdapter.getItem(pos);
        if(show)
        {
            fragment.showMenu(show);
            show = false;

        }
        else
        {
            fragment.showMenu(show);
            show = true;
        }
        if(pos > 0)
        {
           // fragment.showMenu(show);

        }
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

    public class WebEpisodeListAdapter extends RecyclerView.Adapter<WebEpisodeListAdapter.WebEpisodeListViewHolder> {
        private ArrayList<EpisodeVO> itemsList;
        private Activity mContext;

        public WebEpisodeListAdapter(Activity context, ArrayList<EpisodeVO> itemsList) {
            this.mContext = context;
            this.itemsList = itemsList;
        }

        @Override
        public WebEpisodeListViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.work_main_episode_row, null);

            return new WebEpisodeListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(WebEpisodeListViewHolder holder, int position) {
            if (position >= episodeList.size())
                return;

            int nIndex = position;
            EpisodeVO vo = work.getEpisodeList().get(nIndex);

            TextView episodeTitleView = holder.itemView.findViewById(R.id.episodeTitleView);
            TextView postAvailableView = holder.itemView.findViewById(R.id.postAvailableView);

            TextView dateTimeView = holder.itemView.findViewById(R.id.dateTimeView);
            TextView startPointView = holder.itemView.findViewById(R.id.startPointView);
            TextView hitsCountView = holder.itemView.findViewById(R.id.hitsCountView);
            TextView commentCountView = holder.itemView.findViewById(R.id.commentCountView);
//                LinearLayout chatCountLayout = holder.itemView.findViewById(R.id.chatCountLayout);
            TextView chatCountView = holder.itemView.findViewById(R.id.chatCountView);
//                TextView tabCountView = holder.itemView.findViewById(R.id.tabCountView);

            episodeTitleView.setText(vo.getStrTitle());
            dateTimeView.setText(vo.getStrDate().substring(0, 10));
            startPointView.setText(String.format("%.1f", vo.getfStarPoint()));
            hitsCountView.setText(CommonUtils.getPointCount(vo.getnTapCount()));
            commentCountView.setText(CommonUtils.getPointCount(vo.getnCommentCount()));

            ImageView menuBtn = holder.itemView.findViewById(R.id.menuBtn);

            postAvailableView.setVisibility(View.GONE);
            menuBtn.setVisibility(View.GONE);

            chatCountView.setText("" + vo.getnChatCount());
//                tabCountView.setText("" + vo.getnTapCount());
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
        }

        public class WebEpisodeListViewHolder extends RecyclerView.ViewHolder {
            public WebEpisodeListViewHolder(View view) {
                super(view);
/*
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (isClickedList)
                                    return false;
                                fX = motionEvent.getX();
                                fY = motionEvent.getY();
                                break;
                            case MotionEvent.ACTION_MOVE: {
                                float fEndX = motionEvent.getX();
                                float fEndY = motionEvent.getY();

                                if (fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                                    return false;
                                }
                                break;
                            }
                            case MotionEvent.ACTION_CANCEL:
                                return false;
                            case MotionEvent.ACTION_UP: {
                                float fEndX = motionEvent.getX();
                                float fEndY = motionEvent.getY();

                                if (fX >= fEndX + 10 || fX <= fEndX - 10 || fY >= fEndY + 10 || fY <= fEndY - 10) {              // 10px 이상 움직였다면
                                    return false;
                                } else {
                                    int nPosition = getAdapterPosition();

                                    EpisodeVO episodeVO = workVO.getEpisodeList().get(nPosition);
                                    if(workVO.getnInteractionEpisodeID() > 0 && episodeVO.getnEpisodeID() > workVO.getnInteractionEpisodeID()) {                // 클릭한 에피소드가 분기보다 위의 에피소드 라면. 즉, 분기 이후의 에피소드 라면
                                        checkInteractionSelect(episodeList.size() - nPosition - 1);
                                        return false;
                                    }

                                    Intent intent = new Intent(ViewerActivity.this, ViewerActivity.class);
                                    intent.putExtra("EPISODE_INDEX", episodeList.size() - nPosition - 1);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            break;
                        }
                        return true;
                    }
                });

 */
            }
        }
    }

}