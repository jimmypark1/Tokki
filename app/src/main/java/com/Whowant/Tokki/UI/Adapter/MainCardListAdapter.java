package com.Whowant.Tokki.UI.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.NewRankingActivity;
import com.Whowant.Tokki.UI.Activity.Main.PopularActivity;
import com.Whowant.Tokki.UI.Custom.PopularDecoration;
import com.Whowant.Tokki.VO.MainCardVO;
import com.Whowant.Tokki.VO.WorkVO;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainCardListAdapter extends RecyclerView.Adapter<MainCardListAdapter.MainCardHolder> {                             // MainFragment 에 여러줄의 RecyclerView 를 표시하는 메인 Adapter
    private ArrayList<MainCardVO> mainCardList;
    private Activity mContext;
    private Timer timer;
    private int nCurrentItem = 0;
    private final int TIMER_SEC = 2500;
    private ArrayList singleSectionItems;
    private ArrayList<RecentListAdapter> adapterList;

    private SharedPreferences pref;

    public MainCardListAdapter(Activity context, ArrayList<MainCardVO> itemsList) {
        this.mContext = context;
        this.mainCardList = itemsList;
    }

    public void setData(ArrayList<MainCardVO> itemsList) {
        this.mainCardList = itemsList;
        notifyDataSetChanged();
    }

    @Override
    public MainCardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {                                   // 현재 3줄로 이루어져 있음. 0 - 추천작, 1 - 인기작, 2 - 최신작.  viewType 으로 구분하도록 되어있음
        View v = null;

        adapterList = new ArrayList<>();

        for (int i = 0 ; i < 7 ; i++) {
            adapterList.add(null);
        }

        if (viewType == 0)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_card_row_renewal, viewGroup, false);
        else if (viewType == 1)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_popular_viewpager, viewGroup, false);
        else if (viewType == 2)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_card, viewGroup, false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_recommend, viewGroup, false);

        MainCardHolder mh = new MainCardHolder(v);
        return mh;
    }

    @Override
    public int getItemViewType(int position) {
        return mainCardList.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(MainCardHolder itemRowHolder, int position) {

        MainCardVO item = mainCardList.get(position);
        int viewType = item.getViewType();

        if (viewType == 0) {                                                     // 추천
            ArrayList singleSectionItems = mainCardList.get(position).getAllItemInCard();
            if(singleSectionItems.size() > 0)
            {
               // itemRowHolder.bottomShadow.setVisibility(View.VISIBLE);

               // itemRowHolder.recyclerView.setVisibility(View.VISIBLE);

                MainRecommendAdapter adapter = new MainRecommendAdapter(mContext, singleSectionItems);
                itemRowHolder.recyclerView.setHasFixedSize(true);
                LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                itemRowHolder.recyclerView.setAdapter(adapter);

                LinearLayout dotLayout = itemRowHolder.itemView.findViewById(R.id.dotLayout);
                dotLayout.removeAllViews();
                final ArrayList<View> dotViewList = new ArrayList<>();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int i = 0; i < singleSectionItems.size(); i++) {
                    View view = inflater.inflate(R.layout.dot_view, null);
                    ImageView dotView = view.findViewById(R.id.dotView);
                    if (i == 0) {
                        dotView.setBackgroundResource(R.drawable.white_dot);
                    } else {
                        dotView.setBackgroundResource(R.drawable.gray_dot);
                    }

                    dotLayout.addView(view);
                    dotViewList.add(view);
                }

                PagerSnapHelper snapHelper = new PagerSnapHelper();
                itemRowHolder.recyclerView.setOnFlingListener(null);
                snapHelper.attachToRecyclerView(itemRowHolder.recyclerView);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemRowHolder.recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                            View currentView = snapHelper.findSnapView(lm);
                            int nPosition = lm.getPosition(currentView);

                            for (int i = 0; i < dotViewList.size(); i++) {
                                View wrapView = dotViewList.get(i);

                                ImageView dotView = wrapView.findViewById(R.id.dotView);
                                dotView.setBackgroundResource(R.drawable.gray_dot);

                                if (i == nPosition) {
                                    dotView.setBackgroundResource(R.drawable.white_dot);
                                }
                            }

                            nCurrentItem = nPosition;
                            setTimer(singleSectionItems, itemRowHolder.recyclerView);
                        }
                    });
                }

                setTimer(singleSectionItems, itemRowHolder.recyclerView);
                itemRowHolder.recyclerView.setLayoutManager(lm);
            }
            else
            {
                LinearLayout dotLayout = itemRowHolder.itemView.findViewById(R.id.dotLayout);
         //       dotLayout.removeAllViews();

         //       itemRowHolder.bottomShadow.setVisibility(View.GONE);
         //       itemRowHolder.recyclerView.setVisibility(View.GONE);
            }

        } else if (viewType == 1) {                          //  인기작, 2개씩 ViewPager 로 변환*
            ArrayList singleSectionItems = mainCardList.get(position).getAllItemInCard();
            PopularPagerAdapter adapter = new PopularPagerAdapter(mContext, singleSectionItems);
            itemRowHolder.viewPager.setHasFixedSize(true);
            LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            itemRowHolder.viewPager.addItemDecoration(new PopularDecoration(mContext));
            itemRowHolder.viewPager.setAdapter(adapter);

            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(itemRowHolder.viewPager);
            itemRowHolder.viewPager.setLayoutManager(lm);
            itemRowHolder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, PopularActivity.class));
                }
            });
        } else if (viewType == 2) {                                    // 최신작, 인기작, 장르별 순위
            final String sectionName = mainCardList.get(position).getStrHeaderTitle();
            singleSectionItems = mainCardList.get(position).getAllItemInCard();

            itemRowHolder.headerTitle.setText(sectionName);
            if (adapterList.get(position) == null) {
                RecentListAdapter recentListAdapter = new RecentListAdapter(mContext, singleSectionItems, mainCardList.get(position).getViewType());
                adapterList.add(position, recentListAdapter);
                itemRowHolder.recyclerView.setAdapter(recentListAdapter);
            } else {
                adapterList.get(position).setData(singleSectionItems);
            }

//            itemRowHolder.recyclerView.setHasFixedSize(true);
//            LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//            itemRowHolder.recyclerView.setLayoutManager(lm);
//            itemRowHolder.recyclerView.setAdapter(recentListAdapter);
//            recentListAdapter.setData(singleSectionItems);

            itemRowHolder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("장르별 순위".equals(sectionName)) {
                        mContext.startActivity(new Intent(mContext, PopularActivity.class));
                    } else {
                        Intent intent = new Intent(mContext, NewRankingActivity.class);
                        intent.putExtra("title", sectionName);
                        mContext.startActivity(intent);
                    }
                }
            });
        } else { // 추천작
            final String sectionName = mainCardList.get(position).getStrHeaderTitle();
            singleSectionItems = mainCardList.get(position).getAllItemInCard();

            pref = mContext.getSharedPreferences("USER_INFO", Activity.MODE_PRIVATE);
            String strUSerID = pref.getString("USER_NAME", "Guest");

            String title = strUSerID + "님을 위한 추천 작품";
            itemRowHolder.headerTitle.setText(title);

            if (adapterList.get(position) == null) {
                RecentListAdapter recentListAdapter = new RecentListAdapter(mContext, singleSectionItems, mainCardList.get(position).getViewType());
                adapterList.add(position, recentListAdapter);
                itemRowHolder.recyclerView.setAdapter(recentListAdapter);
            } else {
                adapterList.get(position).setData(singleSectionItems);
            }

//            RecentListAdapter recentListAdapter = new RecentListAdapter(mContext, singleSectionItems, mainCardList.get(position).getViewType());
//
//            itemRowHolder.recyclerView.setHasFixedSize(true);
//            LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//            itemRowHolder.recyclerView.setAdapter(recentListAdapter);
//            itemRowHolder.recyclerView.setLayoutManager(lm);

            itemRowHolder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewRankingActivity.bestList = new ArrayList<>();
                    ArrayList<WorkVO> list = mainCardList.get(1).getAllItemInCard();
                    NewRankingActivity.bestList.addAll(mainCardList.get(1).getAllItemInCard());
                    Intent intent = new Intent(mContext, NewRankingActivity.class);
                    intent.putExtra("title", title);
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return (null != mainCardList ? mainCardList.size() : 0);
    }


    public class MainCardHolder extends RecyclerView.ViewHolder {

        protected TextView headerTitle;
        protected TextView btnMore;
        protected RecyclerView recyclerView;
        protected RecyclerView viewPager;
        protected RelativeLayout bottomShadow;
//bottomShadow

        public MainCardHolder(View view) {
            super(view);

            this.headerTitle = view.findViewById(R.id.headerTitle);
            this.btnMore = view.findViewById(R.id.btnMore);
            this.recyclerView = view.findViewById(R.id.recyclerView);
            this.viewPager = view.findViewById(R.id.viewPager);
            this.bottomShadow = view.findViewById(R.id.bottomShadow);

            recyclerView.setHasFixedSize(true);
            LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(lm);
        }
    }

    private void setTimer(ArrayList singleSectionItems, RecyclerView recyclerView) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (nCurrentItem < singleSectionItems.size() - 1) {
                    nCurrentItem++;
                } else {
                    nCurrentItem = 0;
                }

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(nCurrentItem);
                    }
                });
            }
        }, TIMER_SEC, TIMER_SEC);
    }
}
