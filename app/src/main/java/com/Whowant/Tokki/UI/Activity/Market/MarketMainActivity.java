package com.Whowant.Tokki.UI.Activity.Market;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.Whowant.Tokki.Http.HttpClient;
import com.Whowant.Tokki.R;
import com.Whowant.Tokki.UI.Activity.Main.MainActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageAccountSettingActivity;
import com.Whowant.Tokki.UI.Activity.Mypage.MyPageFragment;
import com.Whowant.Tokki.UI.Adapter.FriendAdapter;
import com.Whowant.Tokki.VO.MyPageFeedVo;
import com.Whowant.Tokki.VO.WorkVO;
import com.google.android.material.tabs.TabLayout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MarketMainActivity extends AppCompatActivity {
    private MarketPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabLayout tabSubLayout;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mHorizontalView;
    private HorizontalAdapter mAdapter;
    public int selectedIndex = 0;
    public int selectedTopIndex = 0;

    ArrayList<HorizontalData> data = new ArrayList<>();
    ArrayList<String> datas = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_market_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("콘텐츠별"));
        tabLayout.addTab(tabLayout.newTab().setText("장르별"));
        tabLayout.addTab(tabLayout.newTab().setText("소재별"));


        mHorizontalView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // 기본값이 VERTICAL

        // setLayoutManager
        mHorizontalView.setLayoutManager(mLayoutManager);

        mAdapter = new HorizontalAdapter();

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
/*
marketSubs.append("전체")
            marketSubs.append("드라마")
            marketSubs.append("웹드라마")
            marketSubs.append("영화")
            marketSubs.append("출판")
            marketSubs.append("웹툰")
            marketSubs.append("애니메이션")
            marketSubs.append("공연")
 */
        data.add(new HorizontalData( "전체",0));
        data.add(new HorizontalData( "드라마",1));
        data.add(new HorizontalData( "웹드라마",2));
        data.add(new HorizontalData( "영화",3));
        data.add(new HorizontalData( "출판",4));
        data.add(new HorizontalData( "웹툰",5));
        data.add(new HorizontalData( "애니메이션",6));
        data.add(new HorizontalData( "공연",7));


        // set Data
        mAdapter.setData(data);





        // set Adapter
        mHorizontalView.setAdapter(mAdapter);
        pagerAdapter = new MarketPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                selectedTopIndex = pos;
                data.clear();
                if(pos == 0)
                {
                    selectedIndex = 0;
                    selectedTopIndex = 0;

                    data.add(new HorizontalData( "전체",0));
                    data.add(new HorizontalData( "드라마",1));
                    data.add(new HorizontalData( "웹드라마",2));
                    data.add(new HorizontalData( "영화",3));
                    data.add(new HorizontalData( "출판",4));
                    data.add(new HorizontalData( "웹툰",5));
                    data.add(new HorizontalData( "애니메이션",6));
                    data.add(new HorizontalData( "공연",7));
                    mAdapter.setData(data);
                    mAdapter.notifyDataSetChanged();

                    MarketContentsFragment fragment =  (MarketContentsFragment)pagerAdapter.getItem(selectedTopIndex);
                    fragment.topPosition = selectedTopIndex;

                    viewPager.setCurrentItem(pos);
                    scrollCenter();

                }
                else if(pos == 1)
                {
                    selectedIndex = 0;
                    selectedTopIndex = 1;

                    data.add(new HorizontalData( "전체",0));
                    data.add(new HorizontalData( "호러",1));
                    data.add(new HorizontalData( "로맨스",2));
                    data.add(new HorizontalData( "액션",3));
                    data.add(new HorizontalData( "팬픽",4));
                    data.add(new HorizontalData( "BL",5));
                    mAdapter.setData(data);
                    mAdapter.notifyDataSetChanged();

                    //      fragment.Update(selectedIndex,hField.getText());
                    viewPager.setCurrentItem(pos);
                    scrollCenter();

                    pagerAdapter.notifyDataSetChanged();

                }
                else if(pos == 2)
                {
                    selectedIndex = 0;
                    selectedTopIndex = 2;

                    viewPager.setCurrentItem(pos);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            datas = HttpClient.getMarketTagRank(new OkHttpClient());
                            data.add(new HorizontalData( "전체",0));

                            if (datas != null) {
                                for (int i = 0; i < datas.size(); i++) {

                                    String tag = datas.get(i);
                                    data.add(new HorizontalData( tag,i+1));

                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        mAdapter.setData(data);
                                        mAdapter.notifyDataSetChanged();
                                        scrollCenter();
                                        //    getMyFollowingList();
                                    }
                                });
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (datas == null) {
                                        Toast.makeText(MarketMainActivity.this, "서버와의 통신에 실패했습니다. 잠시후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }


                                    //    getMyFollowingList();
                                }
                            });
                        }
                    }).start();



                }


            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pagerAdapter != null)
            pagerAdapter.notifyDataSetChanged();

    }

    void scrollCenter()
    {
        mHorizontalView.smoothScrollToPosition(selectedIndex);
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

        int centerPosition = (firstVisibleItemPosition + lastVisibleItemPosition) / 2;

        if (selectedIndex > centerPosition) {
            mHorizontalView.smoothScrollToPosition(selectedIndex + 1);
        } else if (selectedIndex < centerPosition) {
            if(selectedIndex - 1 < 0)
            {
                mHorizontalView.smoothScrollToPosition(0);

            }
            else
            {
                mHorizontalView.smoothScrollToPosition(selectedIndex - 1);

            }
        }
    }
    public void onClickTopLeftBtn(View view) {
        finish();
    }


    public void onClickTransactionBtn(View view) {

     //   TransactionActivity
        //
        Intent intent = new Intent(this, TransactionActivity.class);

        startActivity(intent);
    }
    public void onClickMarketAdd(View view) {

        //   TransactionActivity
        //
        Intent intent = new Intent(this, MarketAddActivity.class);
      //  intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivityForResult(intent, 777);




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 777)
        {

            int result = data.getIntExtra("RESULT_TYPE",0);
            if(result == 1)
            {
                Intent intent = new Intent(MarketMainActivity.this, MainActivity.class);
                //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("MAIN_RESULT_TYPE", 1);

                setResult(777,intent);
                finish();
            }



        }
    }
    class HorizontalAdapter extends RecyclerView.Adapter<HorizontalViewHolder> {

        private ArrayList<HorizontalData> HorizontalDatas;

        public void setData(ArrayList<HorizontalData> list){
            HorizontalDatas = list;
        }

        @Override
        public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // 사용할 아이템의 뷰를 생성해준다.
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.market_sub_items, parent, false);

            HorizontalViewHolder holder = new HorizontalViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(HorizontalViewHolder holder, int position) {
            HorizontalData data = HorizontalDatas.get(position);

            if(data.index == selectedIndex)
            {

                holder.back.setBackgroundResource(R.drawable.round_blue);
                holder.description.setTextColor(Color.parseColor("#ffffff"));
            }
            else
            {

                holder.back.setBackgroundResource(R.drawable.round_trans);
                holder.description.setTextColor(Color.parseColor("#767676"));

            }
            holder.description.setText(data.getText());

            //   holder.icon.setImageResource(data.getImg());

        }

        @Override
        public int getItemCount() {
            return HorizontalDatas.size();
        }
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView icon;
        public TextView description;
        public RelativeLayout back;

        public HorizontalViewHolder(View itemView) {
            super(itemView);

            //   icon = (ImageView) itemView.findViewById(R.id.horizon_icon);
            description = (TextView) itemView.findViewById(R.id.horizon_description);
            back = (RelativeLayout) itemView.findViewById(R.id.back);

            itemView.setOnClickListener(this);


        }
        @Override
        public void onClick(View v) {
            selectedIndex = getAdapterPosition();
            mAdapter.notifyDataSetChanged();
           // mHorizontalView.getChildLayoutPosition(v);
           // mHorizontalView.scrollToPosition(selectedIndex);
            //mHorizontalView.smoothScrollToPosition(selectedIndex);

            mHorizontalView.smoothScrollToPosition(selectedIndex);
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

            int centerPosition = (firstVisibleItemPosition + lastVisibleItemPosition) / 2;

            if (selectedIndex > centerPosition) {
                mHorizontalView.smoothScrollToPosition(selectedIndex + 1);
            } else if (selectedIndex < centerPosition) {
                if(selectedIndex - 1 < 0)
                {
                    mHorizontalView.smoothScrollToPosition(0);

                }
                else
                {
                    mHorizontalView.smoothScrollToPosition(selectedIndex - 1);

                }
            }
            HorizontalData hField =  (HorizontalData)data.get(selectedIndex);
            if(selectedTopIndex == 0)
            {
                MarketContentsFragment fragment =  (MarketContentsFragment)pagerAdapter.getItem(selectedTopIndex);
                fragment.topPosition = selectedTopIndex;
                fragment.Update(selectedIndex,hField.getText());

            }
            else if(selectedTopIndex == 1)
            {
                MarketGenreFragment fragment =  (MarketGenreFragment)pagerAdapter.getItem(selectedTopIndex);
                fragment.topPosition = selectedTopIndex;
                fragment.Update(selectedIndex,hField.getText());

            }
            else if(selectedTopIndex == 2)
            {
                MarketTagFragment fragment =  (MarketTagFragment)pagerAdapter.getItem(selectedTopIndex);
                fragment.topPosition = selectedTopIndex;
                fragment.Update(selectedIndex,hField.getText());

            }

            pagerAdapter.notifyDataSetChanged();
            //mHorizontalView.smoothScrollToPosition(selectedIndex);

        }
    }

    class HorizontalData {

        //  private int img;
        private String text;
        int index;

        public HorizontalData( String text, int i){
            //   this.img = img;
            this.text = text;
            this.index = i;
        }

        public String getText() {
            return this.text;
        }


    }
}

