package com.Whowant.Tokki.UI.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.Whowant.Tokki.UI.Activity.Market.CarrotBuyFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageCarrotAccuireFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageCarrotInfoGetFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageCarrotInfoUseFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageCarrotMainFragment;

import java.util.ArrayList;

public class MyPageCarrotInfoAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();

    public MyPageCarrotInfoAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);

        this.mContext = context;

        fragments.add(new MyPageCarrotAccuireFragment());

        fragments.add(new CarrotBuyFragment());
        fragments.add(new MyPageCarrotMainFragment());
     //   fragments.add(new MyPageCarrotInfoGetFragment());

       // fragments.add(new MyPageCarrotInfoGetFragment());
//
        titles.add("당근얻기");
        titles.add("당근사기");
        titles.add("당근내역");
        // titles.add("누적 사용 당근");
       // titles.add("총 획득 당근");

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
