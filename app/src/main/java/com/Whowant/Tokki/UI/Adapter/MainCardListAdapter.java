package com.Whowant.Tokki.UI.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.GenreRankingActivity;
import com.Whowant.Tokki.UI.Activity.Main.NewRankingActivity;
import com.Whowant.Tokki.UI.Custom.PopularDecoration;
import com.Whowant.Tokki.VO.MainCardVO;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainCardListAdapter extends RecyclerView.Adapter<MainCardListAdapter.MainCardHolder> {
    private ArrayList<MainCardVO> mainCardList;
    private Activity mContext;
    private Timer timer;
    private int nCurrentItem = 0;

    public MainCardListAdapter(Activity context, ArrayList<MainCardVO> itemsList) {
        this.mContext = context;
        this.mainCardList = itemsList;
    }

    @Override
    public MainCardHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = null;

        if(viewType == 0)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_card_row_renewal, null);
        else if(viewType == 1)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_popular_viewpager, null);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_card, null);

        MainCardHolder mh = new MainCardHolder(v);
        return mh;
    }

    @Override
    public int getItemViewType(int position) {
        return mainCardList.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(MainCardHolder itemRowHolder, int position) {
        if(position == 0) {                                                     // 추천
            ArrayList singleSectionItems = mainCardList.get(position).getAllItemInCard();
            MainRecommendAdapter adapter = new MainRecommendAdapter(mContext, singleSectionItems);
            itemRowHolder.recyclerView.setHasFixedSize(true);
            LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            itemRowHolder.recyclerView.setAdapter(adapter);

            LinearLayout dotLayout = itemRowHolder.itemView.findViewById(R.id.dotLayout);
            final ArrayList<View> dotViewList = new ArrayList<>();
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for(int i = 0 ; i < singleSectionItems.size() ; i++) {
                View view = inflater.inflate(R.layout.dot_view, null);
                ImageView dotView = view.findViewById(R.id.dotView);
                if(i == 0) {
                    dotView.setBackgroundResource(R.drawable.white_dot);
                } else {
                    dotView.setBackgroundResource(R.drawable.gray_dot);
                }

                dotLayout.addView(view);
                dotViewList.add(view);
            }

            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(itemRowHolder.recyclerView);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                itemRowHolder.recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        View currentView = snapHelper.findSnapView(lm);
                        int nPosition = lm.getPosition(currentView);

                        for(int i = 0 ; i < dotViewList.size() ; i++) {
                            View wrapView = dotViewList.get(i);

                            ImageView dotView = wrapView.findViewById(R.id.dotView);
                            dotView.setBackgroundResource(R.drawable.gray_dot);

                            if(i == nPosition) {
                                dotView.setBackgroundResource(R.drawable.white_dot);
                            }
                        }
                        timer.cancel();
                        nCurrentItem = nPosition;
                        setTimer(singleSectionItems, itemRowHolder.recyclerView);
                    }
                });
            }

            setTimer(singleSectionItems, itemRowHolder.recyclerView);
            itemRowHolder.recyclerView.setLayoutManager(lm);
        } else if(position == 1) {                          //  인기작, 2개씩 ViewPager 로 변환*
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
                    mContext.startActivity(new Intent(mContext, GenreRankingActivity.class));
//                    if(position == 0)
//                        mContext.startActivity(new Intent(mContext, BestRankingActivity.class));
//                    else if(position == 1)
//                        mContext.startActivity(new Intent(mContext, GenreRankingActivity.class));
//                    else if(position == 2)
//                        mContext.startActivity(new Intent(mContext, NewRankingActivity.class));
//                    else if(position == 3)
//                        mContext.startActivity(new Intent(mContext, RecommandRankingActivity.class));
                }
            });
        } else {                                    // 최신작
            final String sectionName = mainCardList.get(position).getStrHeaderTitle();
            ArrayList singleSectionItems = mainCardList.get(position).getAllItemInCard();

            itemRowHolder.headerTitle.setText(sectionName);
            RecentListAdapter recentListAdapter = new RecentListAdapter(mContext, singleSectionItems, mainCardList.get(position).getViewType());

            itemRowHolder.recyclerView.setHasFixedSize(true);
            LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            itemRowHolder.recyclerView.setAdapter(recentListAdapter);
            itemRowHolder.recyclerView.setLayoutManager(lm);

            itemRowHolder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, NewRankingActivity.class));
//                    if(position == 0)
//                        mContext.startActivity(new Intent(mContext, BestRankingActivity.class));
//                    else if(position == 1)
//                        mContext.startActivity(new Intent(mContext, GenreRankingActivity.class));
//                    else if(position == 2)
//                        mContext.startActivity(new Intent(mContext, NewRankingActivity.class));
//                    else if(position == 3)
//                        mContext.startActivity(new Intent(mContext, RecommandRankingActivity.class));
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


        public MainCardHolder(View view) {
            super(view);

            this.headerTitle = view.findViewById(R.id.headerTitle);
            this.btnMore = view.findViewById(R.id.btnMore);
            this.recyclerView = view.findViewById(R.id.recyclerView);
            this.viewPager = view.findViewById(R.id.viewPager);
        }
    }

    private void setTimer(ArrayList singleSectionItems, RecyclerView recyclerView) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(nCurrentItem < singleSectionItems.size() - 1) {
                    nCurrentItem ++;
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
        }, 5000, 5000);
    }
}
