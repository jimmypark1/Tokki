package com.Whowant.Tokki.UI.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.Whowant.Tokki.UI.Activity.Market.CarrotBuyFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageCarrotInfoGetFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageCarrotInfoUseFragment;

import java.util.ArrayList;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MyPageCarrotMainAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();

    public MyPageCarrotMainAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);

        this.mContext = context;

        fragments.add(new MyPageCarrotInfoUseFragment());


        fragments.add(new MyPageCarrotInfoGetFragment());

        titles.add("누적 사용 당근");
        titles.add("총 획득 당근");

    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
