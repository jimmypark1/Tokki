package com.Whowant.Tokki.UI.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageCarrotShopBuyFragment;
import com.Whowant.Tokki.UI.Fragment.MyPage.MyPageCarrotShopGetFragment;

import java.util.ArrayList;

public class MyPageCarrotShopAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();

    public MyPageCarrotShopAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.mContext = context;

        fragments.add(new MyPageCarrotShopGetFragment());
        fragments.add(new MyPageCarrotShopBuyFragment());

        titles.add("당근 얻기");
        titles.add("당근 사기");
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
